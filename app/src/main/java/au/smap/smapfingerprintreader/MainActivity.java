package au.smap.smapfingerprintreader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import au.smap.smapfingerprintreader.application.FingerprintReader;

public class MainActivity extends AppCompatActivity {

    FingerprintReader app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = FingerprintReader.getInstance();
        setContentView(R.layout.activity_main);

    }
}

