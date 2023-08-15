package au.smap.smapfingerprintreader.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;

import au.smap.smapfingerprintreader.R;

public class SettingsFragement extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Indicate here the XML resource you created above that holds the preferences
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }


}
