package com.tstudioz.fax.fme.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.tstudioz.fax.fme.R;

/**
 * Created by etino7 on 12/01/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_prefrences);
    }
}
