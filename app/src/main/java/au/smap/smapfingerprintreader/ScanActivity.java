package au.smap.smapfingerprintreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import au.smap.smapfingerprintreader.databinding.ActivityScanBinding;

public class ScanActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityScanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        /*
         * Get the Intent that started the scan activity
         * Get the parameters
         */
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_scan);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });

        /*
         * Return the results of the Scan
         */
        Intent returnIntent = new Intent();
        returnIntent.putExtra("value", "Hello from the scanner");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_scan);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}