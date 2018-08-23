package com.example.krishna.bukie.home.profile.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.krishna.bukie.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
