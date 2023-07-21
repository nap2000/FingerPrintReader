package au.smap.smapfingerprintreader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import au.smap.smapfingerprintreader.application.FingerprintReader;
import au.smap.smapfingerprintreader.databinding.ActivityScanBinding;

public class ScanActivity extends AppCompatActivity implements MorfinAuth_Callback {

    FingerprintReader app;
    private AppBarConfiguration appBarConfiguration;
    private ActivityScanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = FingerprintReader.getInstance();
        setContentView(R.layout.activity_main);
        app.logView = (TextView) findViewById(R.id.log);

        app.setScanner(this, this);

        /*
         * Get the Intent that started the scan activity
         * Get the parameters
         */
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if(app.currentDevice != null) {
            app.startCapture(10,10);
        }
    }

    /*
     * Fingerprint reader callback functions
     */
    @Override
    public void OnDeviceDetection(String deviceName, DeviceDetection detection) {
        app.deviceDetected(deviceName, detection, true);
    }

    /*
     * Smap Fingerprint Reader does not use preview
     */
    @Override
    public void OnPreview(int errorCode, int quality, byte[] image) {

    }

    @Override
    public void OnComplete(int errorCode, int quality, int nfiq) {
        try {
            app.isStartCaptureRunning = false;
            if (errorCode == 0) {
                app.setLogs("Capture Success" + quality, false);
                /*
                setTxtStatusMessage(quality);
                if (scannerAction == ScannerAction.Capture) {
                    int Size = lastDeviceInfo.Width * lastDeviceInfo.Height + 1111;
                    byte[] bImage = new byte[Size];
                    int[] tSize = new int[Size];
                    int ret = morfinAuth.GetTemplate(bImage, tSize, captureTemplateDatas);
                    if (ret == 0) {
                        lastCapFingerData = new byte[Size];
                        System.arraycopy(bImage, 0, lastCapFingerData, 0,
                                bImage.length);
                    } else {
                        setLogs(morfinAuth.GetErrorMessage(ret), true);
                    }
                }

                 */
            } else {
                if(errorCode == -2057){
                    app.setLogs("Device Not Connected",true);
                }else{
                    app.setLogs("CaptureComplete: " + errorCode + " (" + app.morfinAuth.GetErrorMessage(errorCode) + ")", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * Return the results of the Scan
         */
        Intent returnIntent = new Intent();
        returnIntent.putExtra("value", "Hello from the scanner");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            app.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_scan);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}