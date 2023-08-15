package au.smap.smapfingerprintreader.scanners;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.mantra.morfinauth.DeviceInfo;
import com.mantra.morfinauth.MorfinAuth;
import com.mantra.morfinauth.MorfinAuth_Callback;
import com.mantra.morfinauth.enums.DeviceDetection;
import com.mantra.morfinauth.enums.DeviceModel;
import com.mantra.morfinauth.enums.ImageFormat;
import com.mantra.morfinauth.enums.TemplateFormat;

import java.io.File;
import java.io.FileOutputStream;

import au.smap.smapfingerprintreader.application.FingerprintReader;
import au.smap.smapfingerprintreader.utilities.FileUtilities;

public class MFS500Scanner extends Scanner implements MorfinAuth_Callback {
    FingerprintReader app;
    Context context;
    public boolean isStartCaptureRunning;
    public boolean isStopCaptureRunning;
    public boolean setupComplete = false;   // Set true when a scanner has been enabled
    public String clientKey = "";
    TemplateFormat captureTemplateDatas;
    ImageFormat captureImageData;
    private enum ScannerAction {
        Capture, MatchISO, MatchAnsi
    }
    private ScannerAction scannerAction = ScannerAction.Capture;
    public MorfinAuth morfinAuth;
    public MFS500Scanner(Context context) {
        this.context = context;
        app = FingerprintReader.getInstance();

        app.setLogs("setScanner", false);
        morfinAuth = new MorfinAuth(context, this);
        app.setLogs("Scanner added", false);
        setupComplete = true;

        captureImageData = (ImageFormat.BMP);
        captureTemplateDatas = (TemplateFormat.FMR_V2005);

        if(app.currentDevice != null) {
            initialise();
        }
    }

    /*
     * Fingerprint reader callback functions
     * Called when the device is connected or disconnected
     */
    @Override
    public void OnDeviceDetection(String deviceName, DeviceDetection detection) {
        app.setLogs("Device Detection " + deviceName + (detection == DeviceDetection.CONNECTED ? " connected" : " disconnected"), false);

        isStartCaptureRunning = false;
        isStopCaptureRunning = false;
        if (detection == DeviceDetection.CONNECTED) {
            app.currentDevice = deviceName;
            app.setLogs("Device Detected " + deviceName + " connected", false);

            if(setupComplete) {
                initialise();
                startCapture(app.minQuality, app.timeOut);
            }

        } else if (detection == DeviceDetection.DISCONNECTED) {
            try {
                app.setLogs("Device Not Connected", true);
                app.currentDevice = null;
                morfinAuth.Uninit();
            } catch (Exception e) {
                app.setLogs("Failed to disconnect " + e.getMessage(), true);
                e.printStackTrace();
            } finally {

            }
        }
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
                    app.setLogs("Preview Error Code: " + errorCode + " (" + morfinAuth.GetErrorMessage(errorCode) + ")", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnComplete(int errorCode, int quality, int nfiq) {
        app.setLogs("Complete" + errorCode, false);
        try {
            isStartCaptureRunning = false;
            if (errorCode == 0) {
                app.setLogs("Capture Success" + quality, false);

                app.setLogs("Scanner action: " + scannerAction.name(), false);
                if (scannerAction == ScannerAction.Capture) {
                    int Size = app.lastDeviceInfo.Width * app.lastDeviceInfo.Height + 1111;
                    byte[] bImage = new byte[Size];
                    int[] tSize = new int[Size];
                    int ret = morfinAuth.GetImage(bImage, tSize, 1, captureImageData);
                    if (ret == 0) {
                        app.setLogs("Got image from reader: " + Size, false);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bImage, 0, bImage.length);
                        Uri uri = FileUtilities.getUri(context, app, bitmap);
                        app.model.getImage().postValue(uri);

                    } else {
                        app.setLogs("Get Image: " + morfinAuth.GetErrorMessage(ret), true);
                    }
                }

            } else {
                if(errorCode == -2057){
                    app.setLogs("Device Not Connected",true);
                }else{
                    app.setLogs("CaptureComplete: " + errorCode + " (" + morfinAuth.GetErrorMessage(errorCode) + ")", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() {

    }
    public void startCapture(int minQuality, int timeOut) {
        if (isStartCaptureRunning) {
            //setLogs("StartCapture Ret: " + MorfinAuthNative.CAPTURE_ALREADY_STARTED
            //        + " (" + morfinAuth.GetErrorMessage(MorfinAuthNative.CAPTURE_ALREADY_STARTED) + ")", true);
            return;
        }
        if (isStopCaptureRunning) {
            return;
        }
        //if (lastDeviceInfo == null) {
        //    setLogs("Please run device init first", true);
        //    return;
        //}
        isStartCaptureRunning = true;
        scannerAction = ScannerAction.Capture;
        try {
            app.setLogs("Start capture: " + minQuality + " : " + timeOut, false);
            int ret = morfinAuth.StartCapture(minQuality, timeOut);
            if (ret != 0) {
                isStartCaptureRunning = false;
            }
            app.setLogs("StartCapture Ret: " + ret + " (" + morfinAuth.GetErrorMessage(ret) + ")", ret == 0 ? false : true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        app.setLogs("Destroy", false);
        isStartCaptureRunning = false;
        isStopCaptureRunning = false;
        if(morfinAuth != null) {
            morfinAuth.Uninit();
            morfinAuth.Dispose();
            morfinAuth = null;
        }
    }

    private void initialise() {
        try {
            DeviceInfo info = new DeviceInfo();
            int ret = morfinAuth.Init(DeviceModel.valueFor(app.currentDevice), (clientKey.isEmpty()) ? null : clientKey, info);
            app.lastDeviceInfo = info;
            if (ret != 0) {
                app.setLogs("Init: " + ret + " (" + morfinAuth.GetErrorMessage(ret) + ")", true);
            } else {
                app.setLogs("Init Success", false);
                //setDeviceInfo(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
            app.setLogs("Initilialisation Failed " + e.getMessage(), false);
            app.currentDevice = null;
        }
    }
}
