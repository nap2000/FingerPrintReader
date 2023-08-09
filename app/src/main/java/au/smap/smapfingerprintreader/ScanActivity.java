package au.smap.smapfingerprintreader;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.mantra.morfinauth.DeviceInfo;
import com.mantra.morfinauth.MorfinAuth;
import com.mantra.morfinauth.MorfinAuthNative;
import com.mantra.morfinauth.MorfinAuth_Callback;
import com.mantra.morfinauth.enums.DeviceDetection;
import com.mantra.morfinauth.enums.DeviceModel;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileOutputStream;

import au.smap.smapfingerprintreader.application.FingerprintReader;
import au.smap.smapfingerprintreader.databinding.ActivityScanBinding;
import au.smap.smapfingerprintreader.model.ScannerViewModel;

public class ScanActivity extends AppCompatActivity implements MorfinAuth_Callback {

    AppBarConfiguration appBarConfiguration;
    FingerprintReader app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = FingerprintReader.getInstance();
        setContentView(R.layout.activity_main);
        app.logView = (TextView) findViewById(R.id.log);
        app.clearLogs();

        /*
         * Get the Intent that started the scan activity
         * Get the parameters
         */
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        app.minQuality = 20;
        app.timeOut = 10000;

        /*
         * Set up the scanner
         */
        app.setScanner(this, this);

        /*
         * Create Observers
         */
        app.model = new ViewModelProvider(this).get(ScannerViewModel.class);
        final Observer<Uri> imageObserver = new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                app.setLogs("Image changed called: " + uri.toString(), false);

                // Return the results
                Intent returnIntent = new Intent();
                returnIntent.setClipData(ClipData.newRawUri("fpr.png", uri));
                returnIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        };

        // Start observing the image live data
        app.model.getImage().observe(this, imageObserver);

        app.setLogs("Capture requested", false);
        if(app.currentDevice != null) {
            app.setLogs("Starting capture", false);
            app.startCapture(app.minQuality, app.timeOut);
        } else {
            app.setLogs("Connect the device", false);
        }
    }

    /*
     * Fingerprint reader callback functions
     * Called when the device is connected or disconnected
     */
    @Override
    public void OnDeviceDetection(String deviceName, DeviceDetection detection) {
        app.setLogs("Device Detection " + deviceName + (detection == DeviceDetection.CONNECTED ? " connected" : " disconnected"), false);
        app.deviceDetected(deviceName, detection, true);
    }

    @Override
    public void OnPreview(int errorCode, int quality, byte[] image) {
        try {
            if (errorCode == 0 && image != null) {

                app.setLogs("Preview Quality: " + quality, false);
            } else {
                if(errorCode == -2057){
                    app.setLogs("Device Not Connected",true);
                }else{
                    app.setLogs("Preview Error Code: " + errorCode + " (" + app.morfinAuth.GetErrorMessage(errorCode) + ")", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnComplete(int errorCode, int quality, int nfiq) {
        app.setLogs("Complete" + errorCode, false);
        app.complete(errorCode, quality, nfiq);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.destroy();
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_scan);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


}