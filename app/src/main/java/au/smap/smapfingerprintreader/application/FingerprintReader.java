package au.smap.smapfingerprintreader.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.mantra.morfinauth.DeviceInfo;
import com.mantra.morfinauth.MorfinAuth;
import com.mantra.morfinauth.MorfinAuth_Callback;
import com.mantra.morfinauth.enums.DeviceDetection;
import com.mantra.morfinauth.enums.DeviceModel;
import com.mantra.morfinauth.enums.ImageFormat;
import com.mantra.morfinauth.enums.TemplateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import au.smap.smapfingerprintreader.model.ScannerViewModel;

public class FingerprintReader extends Application {

    private static FingerprintReader singleton;
    public String TYPE_IMAGE = "image";
    public TextView logView;
    public LinearLayout connectProgressBar;
    public LinearLayout captureProgressBar;
    public MaterialButton captureButton;

    public ScannerViewModel model;

    public String type;
    public int minQuality = 60;
    public int timeout = 10000;

    private byte[] lastCapFingerData;


    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public static FingerprintReader getInstance() {
        return singleton;
    }

    public void showLogs() {
        logView.setVisibility(View.VISIBLE);
    }

    public void hideLogs() {
        logView.setVisibility(View.GONE);
    }

    public void setLogs(final String logs, boolean isError) {
        logView.post(new Runnable() {
            @Override
            public void run() {
                if (isError) {
                    logView.setTextColor(Color.RED);
                    showLogs();
                } else {
                    logView.setTextColor(Color.BLACK);
                }
                logView.setText(logs + "\n" + logView.getText());
            }
        });
    }

    public void clearLogs() {
        logView.setText("");
    }

    public String getTemplate() {
        return bytesToHex(lastCapFingerData);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void setParameters(Intent intent) {
        type = intent.getStringExtra("type");
        if(type == null || type.isEmpty()) {
            type = TYPE_IMAGE;
        }
        String mq = intent.getStringExtra("quality");
        String to = intent.getStringExtra("timeout");

        try {
            minQuality = Integer.valueOf(mq);
        } catch (Exception e) {
            minQuality = 60;
        }
        try {
            timeout = Integer.valueOf(to);
        } catch (Exception e) {
            timeout = 10000;
        }
    }

}
