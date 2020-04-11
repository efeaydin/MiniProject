package com.efe.miniproject.helper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

public class VisionHelper {

    private static VisionHelper self;

    private TextRecognizer textRecognizer;

    private boolean isInit = true;

    private VisionHelper(Context context, VisionListener listener) {

        textRecognizer = new TextRecognizer.Builder(context).build();

        if (!textRecognizer.isOperational()) {
            isInit = false;
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            //showToastFromAnotherThread("Vision kütüphanesi henüz indirilmedi.");
            listener.onVisionError("Vision kütüphanesi henüz indirilmedi.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                //Toast.makeText(MainActivity.this, "Low Storage", Toast.LENGTH_LONG).show();
                //showToastFromAnotherThread("Cihazda vision kütüphanesi için yeterli alan bulunmamakta.");
                listener.onVisionError("Cihazda vision kütüphanesi için yeterli alan bulunmamakta.");
            }
        }

    }

    public boolean hasSearchWords(Bitmap bitmap, ArrayList<String> searchWordSet) {
        Frame imageFrame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);


        for (int i = 0; i < textBlocks.size(); i++) {

            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));

            final String text = textBlock.getValue();

            for (String searchWord : searchWordSet) {
                if (text.contains(searchWord)) {

                    return true;
                }
            }
        }

        return false;
    }

    public static void init(Context context, VisionListener listener) {
        if (self == null) {
            self = new VisionHelper(context, listener);
            if (!self.isInit) {
                self = null;
            }
        }
        if (listener != null && self != null) {
            listener.initVisionSuccess(self);
        }

    }

    public interface VisionListener {
        void onVisionError(String error);

        void initVisionSuccess(VisionHelper visionHelper);
    }

}
