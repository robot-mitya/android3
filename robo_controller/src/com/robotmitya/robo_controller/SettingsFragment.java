package com.robotmitya.robo_controller;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.robotmitya.robo_common.RoboHelper;
import com.robotmitya.robo_common.SettingsCommon;

/**
 *
 * Created by dmitrydzz on 08.05.18.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private View mView;

    private EditTextPreference mEditTextPreferenceLocalIp;
    private EditTextPreference mEditTextPreferenceMasterUri;

    public SettingsFragment() {
        mView = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = null;
        addPreferencesFromResource(R.xml.settings_fragment);

        final Context context = getActivity();
        String key;
        String title;

        key = getString(R.string.option_local_ip_key);
        mEditTextPreferenceLocalIp = (EditTextPreference) findPreference(key);
        title = getString(R.string.option_local_ip_title) + ": " + SettingsCommon.getLocalIp();
        mEditTextPreferenceLocalIp.setTitle(title);
        mEditTextPreferenceLocalIp.setDefaultValue(RoboHelper.wifiIpAddress(context));
        mEditTextPreferenceLocalIp.setOnPreferenceChangeListener(this);

        key = getString(R.string.option_master_uri_key);
        mEditTextPreferenceMasterUri = (EditTextPreference) findPreference(key);
        title = getString(R.string.option_master_uri_title) + ": " + SettingsCommon.getMasterUri();
        mEditTextPreferenceMasterUri.setTitle(title);
        mEditTextPreferenceMasterUri.setOnPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        setSettingsFullscreen();
        MainActivity.fragmentType = MainActivity.FragmentType.SETTINGS;
        return mView;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == null) {
            return false;
        }

        Context context = getActivity();

        boolean hasChanges = false;
        if (preference == mEditTextPreferenceLocalIp) {
            if (!((String) newValue).contentEquals(SettingsCommon.getLocalIp())) {
                Toast.makeText(context, getString(R.string.messageRestartApp), Toast.LENGTH_LONG).show();
            }
            SettingsCommon.setLocalIp((String) newValue);
            mEditTextPreferenceLocalIp.setTitle(
                    getString(R.string.option_local_ip_title) + ": " + newValue);
            hasChanges = true;
        } else if (preference == mEditTextPreferenceMasterUri) {
            if (!((String) newValue).contentEquals(SettingsCommon.getMasterUri())) {
                Toast.makeText(context, getString(R.string.messageRestartApp), Toast.LENGTH_LONG).show();
            }
            SettingsCommon.setMasterUri((String) newValue);
            mEditTextPreferenceMasterUri.setTitle(
                    getString(R.string.option_master_uri_title) + ": " + newValue);
            hasChanges = true;
        }

        if (hasChanges) {
            SettingsCommon.save(context);
        }
        return hasChanges;
    }

    public void setSettingsFullscreen() {
        if (mView != null)
            mView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
