package au.smap.smapfingerprintreader.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

import au.smap.smapfingerprintreader.application.FingerprintReader;

public class FileUtilities {
    public static Uri getUri(Context context, FingerprintReader app, Bitmap bitmap) {
        Uri uri = null;

        File imagePath = new File(context.getFilesDir(), "scan_images");
        if(!imagePath.exists()) {
            imagePath.mkdir();
        }

        try {
            File outputFile = File.createTempFile("fpr", ".png", imagePath);
            FileOutputStream of = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, of);

            uri = FileProvider.getUriForFile(context, "au.com.smap.FingerprintReader.fileprovider", outputFile);
        } catch(Exception e) {
            e.printStackTrace();
            app.setLogs("Error: " + e.getMessage(), true);
        }
        app.setLogs(uri.toString(), false);
        return uri;
    }
}
