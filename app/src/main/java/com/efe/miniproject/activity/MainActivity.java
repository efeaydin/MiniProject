package com.efe.miniproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.efe.miniproject.R;
import com.efe.miniproject.dialog.AppDialog;
import com.efe.miniproject.helper.VisionHelper;
import com.efe.miniproject.util.ImageUtil;
import com.efe.miniproject.util.PermissionsUtil;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements PermissionsUtil.CameraStateListener, VisionHelper.VisionListener {

    private static final int REQUEST_CODE_PICK_IMAGE_CAMERA = 12;

    String mPhotoFile;

    MaterialButton buttonTakePhoto;
    MotionLayout motionLayoutPoint;
    TextView textViewCurrentPoint;
    TextView textViewAddPoint;

    ArrayList<String> searchWordSet = new ArrayList<>();

    int totalPoint = 0;

    private static final int INCREMENT_AMOUNT = 1;

    Bitmap bitmapTakenPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillSearchWords();

        findViewByIds();
        initViews();



    }

    private void fillSearchWords() {
        searchWordSet.add("Pantene");
        searchWordSet.add("Orkid");
        searchWordSet.add("Gillette");
    }

    private void findViewByIds() {
        buttonTakePhoto = findViewById(R.id.button_take_photo);
        motionLayoutPoint = findViewById(R.id.motion_layout);
        textViewCurrentPoint = findViewById(R.id.text_view_current_point);
        textViewAddPoint = findViewById(R.id.text_view_add_point);
    }

    private void initViews() {
        textViewAddPoint.setText("+" + INCREMENT_AMOUNT);
        textViewCurrentPoint.setText(totalPoint + "");

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });

        motionLayoutPoint.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                textViewCurrentPoint.setText(totalPoint + "");
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });
    }

    @Override
    public void onCameraPermissionOk() {
        openImageCaptureActivity();
    }

    @Override
    public void onCameraPermissionFailed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE_CAMERA:
                    Glide.with(this)
                            .asBitmap()
                            .load(mPhotoFile)
                            .apply(new RequestOptions())
                            .listener(new RequestListener<Bitmap>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(final Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                                    bitmapTakenPhoto = resource;

                                    VisionHelper.init(MainActivity.this, MainActivity.this);

                                    return true;
                                }
                            })
                            .submit();
                    break;

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionsUtil.onRequestPermissionsResult(requestCode, grantResults, this);
    }

    void startCameraActivity() {

        if (PermissionsUtil.checkAndRequestCameraPermission(this, this)) {
            onCameraPermissionOk();
        }

    }

    private void openImageCaptureActivity() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Uri uri = setPhotoUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            intent.setClipData(ClipData.newRawUri("", uri));
        }
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE_CAMERA);
    }

    Uri setPhotoUri() {
        ImageUtil.PhotoFileResult result = ImageUtil.getPhotoFile(getApplicationContext());
        this.mPhotoFile = result.photoFile;
        return result.photoUri;
    }

    private void showToastFromAnotherThread(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVisionError(String error) {
        showToastFromAnotherThread(error);
    }

    @Override
    public void initVisionSuccess(VisionHelper visionHelper) {

        if (bitmapTakenPhoto != null) {
            final boolean result = visionHelper.hasSearchWords(bitmapTakenPhoto, searchWordSet);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result) {
                        totalPoint += INCREMENT_AMOUNT;
                        motionLayoutPoint.setProgress(0);
                        motionLayoutPoint.transitionToEnd();
                    } else {
                        new AppDialog.Builder(MainActivity.this)
                                .setIconVisibility(true)
                                .setTitle("Tüh :(")
                                .setSubtitle("Ne yazık ki bu fişten puan kazanamadınız.")
                                .setOkButton("Tamam", new AppDialog.OnClickListener() {
                                    @Override
                                    public void onClick(AppDialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .create().show();
                    }

                }
            });
        }

    }
}
