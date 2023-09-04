package au.smap.smapfingerprintreader.scanners;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;
import com.mantra.morfinauth.DeviceInfo;
import com.mantra.morfinauth.MorfinAuth;
import com.mantra.morfinauth.MorfinAuth_Callback;
import com.mantra.morfinauth.enums.DeviceDetection;
import com.mantra.morfinauth.enums.DeviceModel;
import com.mantra.morfinauth.enums.ImageFormat;
import com.mantra.morfinauth.enums.TemplateFormat;

import au.smap.smapfingerprintreader.application.FingerprintReader;
import au.smap.smapfingerprintreader.model.ScannerViewModel;
import au.smap.smapfingerprintreader.utilities.FileUtilities;

public class MFS100Scanner extends Scanner implements MFS100Event {
    FingerprintReader app;
    Context context;
    public boolean setupComplete = false;   // Set true when a scanner has been enabled

    private enum ScannerAction {
        Capture, MatchISO, MatchAnsi
    }
    private ScannerAction scannerAction = ScannerAction.Capture;
    public MFS100 mfs100;
    public MFS100Scanner(Context context) {
        this.context = context;
        app = FingerprintReader.getInstance();

        app.setLogs("MFS100 setScanner", false);
        mfs100 = new MFS100(this);
        app.setLogs("MFS100 Scanner added", false);
        setupComplete = true;

    }

    /*
     * Fingerprint reader callback functions
     * Called when the device is connected or disconnected
     */
    @Override
    public void OnDeviceAttached(int vendorID, int productID, boolean hasPermission) {
        app.setLogs("onDeviceAttached", false);
        if (!hasPermission) {
            return;
        }

        if (vendorID == 1204 || vendorID == 11279) {
            if (productID == 34323) {
                loadFirmware();

            } else if (productID == 4101) {
                initialize();
            }
        }
    }

    @Override
    public void OnDeviceDetached() {
        app.setLogs("onDeviceDetached", false);
        app.model.getScannerState().postValue(ScannerViewModel.DISCONNECTED);
    }

    @Override
    public void OnHostCheckFailed(String s) {
        app.setLogs("onHostCheckFailed", false);
    }

    @Override
    public void connect() {
        app.setLogs("Connect", false);
        mfs100.SetApplicationContext(context);
        app.model.getScannerState().postValue(ScannerViewModel.CONNECTED);
    }

    @Override
    public void startCapture(int minQuality, int timeOut) {
        app.setLogs("startCapture", false);
        FingerData fingerData = new FingerData();
        int result = mfs100.AutoCapture(fingerData, 10000, false);
        if (result == 0) {
            //return fingerData.FingerImage();
        } else {
            //return null;
        }
    }

    @Override
    public void destroy() {
        app.setLogs("destroy", false);
    }

    @Override
    public void initialise() {}
    @Override
    public void isConnected() {

    }

    private void initialize() {
        mfs100.Init();
        //onConnected();

    }

    private void loadFirmware() {
        mfs100.LoadFirmware();
    }

}
