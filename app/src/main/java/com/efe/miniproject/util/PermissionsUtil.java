package com.efe.miniproject.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.efe.miniproject.dialog.AppDialog;

public class PermissionsUtil {

    private static boolean isCameraPermissionRequestCalled = false;

    public interface StateListener {

    }

    public interface CameraStateListener extends StateListener {

        void onCameraPermissionOk();

        void onCameraPermissionFailed();
    }

    enum PermissionRequestCode {
        CAMERA(123);

        private final int value;

        PermissionRequestCode(int value) {
            this.value = value;
        }
    }

    public static boolean isCameraPermissionOk(Context context) {
        return (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    static boolean isCameraPermissionDenied(Activity activity) {
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
            return false;

        if (!isCameraPermissionRequestCalled)
            return false;

        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
    }

    public static void checkCameraPermission(Activity activity, CameraStateListener listener) {
        isCameraPermissionRequestCalled = true;

        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            if (listener != null) {
                listener.onCameraPermissionOk();
            }
            return;
        }

        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, PermissionRequestCode.CAMERA.value);
        } else {
            if (listener != null) {
                listener.onCameraPermissionOk();
            }
        }
    }

    private static boolean isPermissionGranted(int[] grantResults) {
        if (grantResults.length == 0) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkAndRequestCameraPermission(final Activity activity, CameraStateListener listener) {
        //this.cameraPermissionListener = listener
        if (isCameraPermissionOk(activity)) {
            return true;
        } else {
            if (isCameraPermissionDenied(activity)) {
                new AppDialog.Builder(activity)
                        .setIconVisibility(true)
                        .setTitle("Kamera İzni")
                        .setSubtitle("Bu işlemi gerçekleştirmek için ayarlardan kameraya izin vermelisin")
                        .setOkButton("Ayarlar", new AppDialog.OnClickListener() {
                            @Override
                            public void onClick(AppDialog dialog) {
                                dialog.dismiss();
                                try {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                    intent.setData(uri);
                                    activity.startActivity(intent);
                                } catch (Throwable ignored) {

                                }
                            }
                        })
                        .create().show();
            } else {
                checkCameraPermission(activity, listener);
            }
            return false;
        }
    }

    public static void onRequestPermissionsResult(int requestCode, int[] grantResults, StateListener listener) {

        if (requestCode == PermissionRequestCode.CAMERA.value && listener instanceof CameraStateListener) {

            CameraStateListener cameraListener = (CameraStateListener) listener;
            if (isPermissionGranted(grantResults)) {
                cameraListener.onCameraPermissionOk();
            } else {
                cameraListener.onCameraPermissionFailed();
            }
        }
    }

}
