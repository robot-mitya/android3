package com.robotmitya.robo_face;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.robotmitya.robo_common.Constants;
import com.robotmitya.robo_common.SettingsCommon;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private View mView;

    private EditTextPreference mEditTextPreferenceMasterUri;
    private ListPreference mListPreferenceCamera;
    private ListPreference mListPreferenceFrontCameraMode;
    private ListPreference mListPreferenceBackCameraMode;

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

        key = getString(R.string.option_master_uri_key);
        mEditTextPreferenceMasterUri = (EditTextPreference) findPreference(key);
        title = getString(R.string.option_master_uri_title) + ": " + SettingsCommon.getMasterUri();
        mEditTextPreferenceMasterUri.setTitle(title);
        mEditTextPreferenceMasterUri.setOnPreferenceChangeListener(this);

        ArrayList<CharSequence> cameraEntries = SettingsFace.getCameraEntries(context);
        ArrayList<CharSequence> cameraValues = SettingsFace.getCameraValues();

        key = getString(R.string.option_camera_index_key);
        mListPreferenceCamera = (ListPreference) findPreference(key);
        mListPreferenceCamera.setEntries(cameraEntries.toArray(new CharSequence[cameraEntries.size()]));
        mListPreferenceCamera.setEntryValues(cameraValues.toArray(new CharSequence[cameraValues.size()]));
        mListPreferenceCamera.setValue(String.valueOf(SettingsFace.getCameraIndex()));
        title = getString(R.string.option_camera_index_title) + ": " + SettingsFace.getCameraValueDescription(SettingsFace.getCameraIndex(), context);
        mListPreferenceCamera.setTitle(title);
        mListPreferenceCamera.setDialogTitle(R.string.option_camera_index_dialog_title);
        mListPreferenceCamera.setOnPreferenceChangeListener(this);

        ArrayList<CharSequence> cameraModeEntries = SettingsFace.getCameraModeEntries(context);
        ArrayList<CharSequence> cameraModeValues = SettingsFace.getCameraModeValues();

        key = getString(R.string.option_front_camera_mode_key);
        mListPreferenceFrontCameraMode = (ListPreference) findPreference(key);
        mListPreferenceFrontCameraMode.setEntries(cameraModeEntries.toArray(new CharSequence[cameraModeEntries.size()]));
        mListPreferenceFrontCameraMode.setEntryValues(cameraModeValues.toArray(new CharSequence[cameraModeValues.size()]));
        final String frontCameraMode = SettingsFace.getFrontCameraMode();
        mListPreferenceFrontCameraMode.setValue(frontCameraMode);
        title = getString(R.string.option_front_camera_mode_title) + ": " + SettingsFace.getCameraModeValueDescription(frontCameraMode, context);
        mListPreferenceFrontCameraMode.setTitle(title);
        mListPreferenceFrontCameraMode.setDialogTitle(R.string.option_front_camera_mode_dialog_title);
        mListPreferenceFrontCameraMode.setOnPreferenceChangeListener(this);

        key = getString(R.string.option_back_camera_mode_key);
        mListPreferenceBackCameraMode = (ListPreference) findPreference(key);
        mListPreferenceBackCameraMode.setEntries(cameraModeEntries.toArray(new CharSequence[cameraModeEntries.size()]));
        mListPreferenceBackCameraMode.setEntryValues(cameraModeValues.toArray(new CharSequence[cameraModeValues.size()]));
        final String backCameraMode = SettingsFace.getBackCameraMode();
        mListPreferenceBackCameraMode.setValue(backCameraMode);
        title = getString(R.string.option_back_camera_mode_title) + ": " + SettingsFace.getCameraModeValueDescription(backCameraMode, context);
        mListPreferenceBackCameraMode.setTitle(title);
        mListPreferenceBackCameraMode.setDialogTitle(R.string.option_back_camera_mode_dialog_title);
        mListPreferenceBackCameraMode.setOnPreferenceChangeListener(this);
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

        Context context = getActivity();

        boolean hasChanges = false;
        boolean cameraParamsWereChanged = false;
        if (preference == mEditTextPreferenceMasterUri) {
            if (!((String) newValue).contentEquals(SettingsCommon.getMasterUri())) {
                Toast.makeText(context, getString(R.string.messageRestartApp), Toast.LENGTH_LONG).show();
            }
            SettingsCommon.setMasterUri((String) newValue);
            mEditTextPreferenceMasterUri.setTitle(
                    getString(R.string.option_master_uri_title) + ": " + newValue);
            hasChanges = true;
        } else if (preference == mListPreferenceCamera) {
            final int cameraIndex = Integer.valueOf((String) newValue);
            if (cameraIndex != SettingsFace.getCameraIndex()) {
                cameraParamsWereChanged = true;
            }
            SettingsFace.setCameraIndex(context, cameraIndex);
            final String title = getString(R.string.option_camera_index_title) +
                    ": " + SettingsFace.getCameraValueDescription(cameraIndex, context);
            mListPreferenceCamera.setTitle(title);
            hasChanges = true;
        } else if (preference == mListPreferenceFrontCameraMode) {
            final String frontCameraMode = (String) newValue;
            if (SettingsFace.getCameraIndex() == Constants.Camera.Front &&
                    !frontCameraMode.contentEquals(SettingsFace.getFrontCameraMode())) {
                cameraParamsWereChanged = true;
            }
            SettingsFace.setFrontCameraMode(context, frontCameraMode);
            final String title = getString(R.string.option_front_camera_mode_title) +
                    ": " + SettingsFace.getCameraModeValueDescription(frontCameraMode, context);
            mListPreferenceFrontCameraMode.setTitle(title);
            hasChanges = true;
        } else if (preference == mListPreferenceBackCameraMode) {
            final String backCameraMode = (String) newValue;
            if (SettingsFace.getCameraIndex() == Constants.Camera.Back &&
                    !backCameraMode.contentEquals(SettingsFace.getBackCameraMode())) {
                cameraParamsWereChanged = true;
            }
            SettingsFace.setBackCameraMode(context, backCameraMode);
            final String title = getString(R.string.option_back_camera_mode_title) +
                    ": " + SettingsFace.getCameraModeValueDescription(backCameraMode, context);
            mListPreferenceBackCameraMode.setTitle(title);
            hasChanges = true;
        }

        if (hasChanges) {
            SettingsCommon.save(context);
            SettingsFace.save(context);
        }
        if (cameraParamsWereChanged) {
            sendCameraSettingsWereChangedBroadcast();
        }
        return hasChanges;
    }

    private void sendCameraSettingsWereChangedBroadcast() {
        if ((getActivity() == null) || (getActivity().getApplicationContext() == null))
            return;

        final int cameraIndex = SettingsFace.getCameraIndex();
        final String cameraMode = cameraIndex == Constants.Camera.Disabled ? "FFFF" :
                cameraIndex == Constants.Camera.Front ? SettingsFace.getFrontCameraMode() :
                        SettingsFace.getBackCameraMode();

        Intent intent = new Intent(EyePreview.Broadcast.CameraSettings.IntentName);
        intent.putExtra(EyePreview.Broadcast.CameraSettings.CameraIndexExtraParamName, cameraIndex);
        intent.putExtra(EyePreview.Broadcast.CameraSettings.CameraModeExtraParamName, cameraMode);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(intent);
    }
}
