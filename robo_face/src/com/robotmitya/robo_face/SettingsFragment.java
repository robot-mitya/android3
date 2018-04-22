package com.robotmitya.robo_face;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robotmitya.robo_common.SettingsCommon;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private View mView;

    private EditTextPreference mEditTextPreferenceMasterUri;

    public SettingsFragment() {
        mView = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = null;
        addPreferencesFromResource(R.xml.settings_fragment);

        String key;
        String title;

        key = getString(R.string.option_master_uri_key);
        mEditTextPreferenceMasterUri = (EditTextPreference) this.findPreference(key);
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == null) {
            return false;
        }

        //Context context = getActivity();

        boolean result = false;
        if (preference == mEditTextPreferenceMasterUri) {
            SettingsCommon.setMasterUri((String) newValue);
            mEditTextPreferenceMasterUri.setTitle(
                    getString(R.string.option_master_uri_title) + ": " + newValue);
            result = true;
        }

        if (result) {
            final Context context = getActivity();
            SettingsCommon.save(context);
            SettingsFace.save(context);
        }
        return result;
    }
}
