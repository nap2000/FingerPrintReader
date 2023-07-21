package au.smap.smapfingerprintreader.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mantra.morfinauth.DeviceInfo;
import com.mantra.morfinauth.MorfinAuth;
import com.mantra.morfinauth.MorfinAuth_Callback;
import com.mantra.morfinauth.enums.DeviceDetection;
import com.mantra.morfinauth.enums.DeviceModel;

public class FingerprintReader extends Application {

    private static FingerprintReader singleton;

    public TextView logView;

    public boolean isStartCaptureRunning;
    public boolean isStopCaptureRunning;
    public MorfinAuth morfinAuth;
    public String currentDevice;       // Name of currently connected device
    public String clientKey = "";

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public static FingerprintReader getInstance() {
        return singleton;
    }

    public void deviceDetected(String deviceName, DeviceDetection detection, boolean startCapture) {
        isStartCaptureRunning = false;
        isStopCaptureRunning = false;
        if (detection == DeviceDetection.CONNECTED) {
            if (deviceName != null) {
                currentDevice = deviceName;
            }
            setLogs("Device " + deviceName + " connected", false);

            try {
                DeviceInfo info = new DeviceInfo();
                int ret = morfinAuth.Init(DeviceModel.valueFor(currentDevice), (clientKey.isEmpty()) ? null : clientKey, info);
                if (ret != 0) {
                    setLogs("Init: " + ret + " (" + morfinAuth.GetErrorMessage(ret) + ")", true);
                } else {
                    setLogs("Init Success", false);
                    //setDeviceInfo(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setLogs("Device not found", false);
            }
            if(startCapture) {
                startCapture(60, 2);
            }
        } else if (detection == DeviceDetection.DISCONNECTED) {
            try {
                setLogs("Device Not Connected", true);
                currentDevice = null;
                try {
                    morfinAuth.Uninit();
                    //setClearDeviceInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    public void setScanner(Context context, MorfinAuth_Callback callback) {
        if(morfinAuth == null) {
            morfinAuth = new MorfinAuth(context, callback);
        }
    }
    public void destroy() {
        isStartCaptureRunning = false;
        isStopCaptureRunning = false;
        if(morfinAuth != null) {
            morfinAuth.Uninit();
            morfinAuth.Dispose();
        }
    }

    public void setLogs(final String logs, boolean isError) {
        logView.post(new Runnable() {
            @Override
            public void run() {
                if (isError) {
                    logView.setTextColor(Color.RED);
                } else {
                    logView.setTextColor(Color.BLACK);
                }
                logView.setText(logView.getText() + "\n" + logs);
            }
        });
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
        try {
            setLogs("Start capture: " + minQuality + " : " + timeOut, false);
            int ret = morfinAuth.StartCapture(minQuality, timeOut);
            if (ret != 0) {
                isStartCaptureRunning = false;
            }
            setLogs("StartCapture Ret: " + ret + " (" + morfinAuth.GetErrorMessage(ret) + ")", ret == 0 ? false : true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
