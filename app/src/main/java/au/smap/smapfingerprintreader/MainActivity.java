package au.smap.smapfingerprintreader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.mantra.morfinauth.MorfinAuth;
import com.mantra.morfinauth.MorfinAuth_Callback;
import com.mantra.morfinauth.enums.DeviceDetection;

import org.w3c.dom.Text;

import au.smap.smapfingerprintreader.application.FingerprintReader;

public class MainActivity extends AppCompatActivity {

    FingerprintReader app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}