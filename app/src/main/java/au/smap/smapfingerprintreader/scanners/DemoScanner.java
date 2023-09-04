package au.smap.smapfingerprintreader.scanners;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import au.smap.smapfingerprintreader.application.FingerprintReader;
import au.smap.smapfingerprintreader.utilities.FileUtilities;

public class DemoScanner extends Scanner {
    FingerprintReader app;
    Context context;

    public DemoScanner(Context context) {
        this.context = context;
        app = FingerprintReader.getInstance();

    }
    public void initialise() {

    }

    public void connect() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // Record successful connection after a delay
            app.model.getScannerState().postValue("connected");
        }, 2000);

    }
    public void startCapture(int minQuality, int timeOut) {
        app.model.getScannerState().postValue("scanning");
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            // Record successful connection after a delay
            app.setLogs("Demo capture", false);

            try {
                InputStream fis = context.getAssets().open("sample.png");
                Bitmap bitmap = BitmapFactory.decodeStream(fis);

                Uri uri = FileUtilities.getUri(context, app, bitmap);
                app.model.getImage().postValue(uri);

            } catch (Exception e) {
                app.setLogs("Error getting demo image", true);
            }

        }, 2000);

    }

    public void destroy() {

    }

    public void isConnected() {
    }
}
