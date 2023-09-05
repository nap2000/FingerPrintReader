package au.smap.smapfingerprintreader;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;

import au.smap.smapfingerprintreader.application.FingerprintReader;
import au.smap.smapfingerprintreader.model.ScannerViewModel;
import au.smap.smapfingerprintreader.scanners.Scanner;
import au.smap.smapfingerprintreader.scanners.ScannerFactory;

public class ScanActivity extends AppCompatActivity {

    AppBarConfiguration appBarConfiguration;
    FingerprintReader app;
    public ScannerViewModel model;
    Scanner scanner;
    String currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = FingerprintReader.getInstance();

        setContentView(R.layout.activity_scan);
        app.connectProgressBar = (LinearLayout) findViewById(R.id.connect_progress_bar);
        app.captureProgressBar = (LinearLayout) findViewById(R.id.capture_progress_bar);
        app.captureButton = (MaterialButton) findViewById(R.id.capture_button);

        app.logView = (TextView) findViewById(R.id.log);
        app.clearLogs();
        currentState = ScannerViewModel.NOSTATE;

        app.setLogs("Create", false);
        /*
         * Get the Intent that started the scan activity
         * Get the parameters
         */
        app.setParameters(getIntent());

        /*
         * Create Observers
         *
         */
        app.model = new ViewModelProvider(this).get(ScannerViewModel.class);

        // Observe scanner state
        app.model.getScannerState().observe(this, state -> {

            if(currentState.equals(state)) {
                app.setLogs("Event " + state + " received but already in this state", false);
                return;
            } else {
                currentState = state;

                if (state.equals(ScannerViewModel.DISCONNECTED)) {
                    app.connectProgressBar.setVisibility(View.VISIBLE);
                    app.captureButton.setVisibility(View.GONE);
                    app.captureProgressBar.setVisibility(View.GONE);

                    app.setLogs("Disconnected. Connect the device. ", false);
                } else if (state.equals(ScannerViewModel.CONNECTED)) {
                    app.connectProgressBar.setVisibility(View.VISIBLE);
                    app.captureButton.setVisibility(View.GONE);
                    app.captureProgressBar.setVisibility(View.GONE);

                    app.setLogs("Connected: ", false);
                    scanner.initialise();
                } else if (state.equals(ScannerViewModel.SCANNING)) {
                    app.connectProgressBar.setVisibility(View.GONE);
                    app.captureButton.setVisibility(View.GONE);
                    app.captureProgressBar.setVisibility(View.VISIBLE);

                    app.setLogs("Scanning: ", false);
                } else if (state.equals(ScannerViewModel.ERROR)) {
                    app.connectProgressBar.setVisibility(View.GONE);
                    app.captureButton.setVisibility(View.GONE);
                    app.captureProgressBar.setVisibility(View.GONE);
                    app.showLogs();

                    app.setLogs("Scanning: ", false);
                } else {
                    app.setLogs("Unknown scanner state: " + state, true);
                }
            }

        });

        // Observe the image live data
        app.model.getImage().observe(this, uri -> {
            app.setLogs("Image changed called: " + uri.toString(), false);

            // Return the results
            Intent returnIntent = new Intent();
            returnIntent.setClipData(ClipData.newRawUri("fpr.png", uri));
            returnIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });

        /*
         * Set up the scanner
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String scannerName = sharedPreferences.getString("scanner", "Demo");
        scanner = ScannerFactory.getScanner(scannerName, getApplicationContext());
        app.setLogs("Connecting scanner: " + scannerName, false);

        scanner.isConnected();
        currentState = ScannerViewModel.DISCONNECTED;
        app.connectProgressBar.setVisibility(View.VISIBLE);
        app.captureButton.setVisibility(View.GONE);
        app.captureProgressBar.setVisibility(View.GONE);


    }

    @Override
    protected void onStart(){
        super.onStart();
        app.setLogs("Start", false);
    }

    @Override
    protected void onResume(){
        super.onResume();
        app.setLogs("Pause", false);
    }
    @Override
    protected void onPause() {
        app.setLogs("Pause", false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        app.setLogs("Destroy", false);
        super.onDestroy();
        scanner.destroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
         return super.onSupportNavigateUp();
    }



}