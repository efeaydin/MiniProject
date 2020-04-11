package com.efe.miniproject.util;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class ImageUtil {

    public static PhotoFileResult getPhotoFile(Context context) {
        PhotoFileResult result = new PhotoFileResult();
        Uri photoUri;
        File folder = new File(context.getFilesDir(), "images");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            // Do something on success
        } else {
            // Do something else on failure
        }

        File file = new File(folder, System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        result.photoUri = photoUri;
        result.photoFile = file.getAbsolutePath();
        return result;
    }

    public static class PhotoFileResult {
        public Uri photoUri;
        public String photoFile;
    }

}
