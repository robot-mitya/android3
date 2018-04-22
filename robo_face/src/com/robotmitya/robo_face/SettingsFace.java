package com.robotmitya.robo_face;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;

import com.robotmitya.robo_common.Constants;

import org.json.JSONException;

import java.util.ArrayList;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 22.04.18.
 */
class SettingsFace {

    private static CameraSizesSet mCameraSizesSet;
    private static int mCameraIndex;
    private static String mFrontCameraMode;
    private static String mBackCameraMode;

    static void load(Context context) {
        loadCameraSizesSet(context);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String key;
        String defaultValue;

        @SuppressWarnings("deprecation")
        final int numberOfCameras = Camera.getNumberOfCameras();

        key = context.getString(R.string.option_front_camera_mode_key);
        if (numberOfCameras == 0) {
            defaultValue = integerToHex(0xff, 0xff);
        } else {
            defaultValue = integerToHex(
                    mCameraSizesSet.get(mCameraSizesSet.length() - 1).getCameraIndex(),
                    0xff);
        }
        mFrontCameraMode = settings.getString(key, defaultValue);

        key = context.getString(R.string.option_back_camera_mode_key);
        if (numberOfCameras == 0) {
            defaultValue = integerToHex(0xff, 0xff);
        } else {
            defaultValue = integerToHex(
                    mCameraSizesSet.get(0).getCameraIndex(),
                    0xff);
        }
        mBackCameraMode = settings.getString(key, defaultValue);

        key = context.getString(R.string.option_camera_index_key);
        if (numberOfCameras == 0) {
            defaultValue = String.valueOf(Constants.Camera.Disabled);
        } else {
            defaultValue = String.valueOf(Constants.Camera.Front);
        }
        mCameraIndex = Integer.valueOf(settings.getString(key, defaultValue));
    }

    @SuppressWarnings("unused")
    static void save(Context context) {
    }

    static int getCameraIndex() {
        return mCameraIndex;
    }

    static String getFrontCameraMode() {
        return mFrontCameraMode;
    }

    static String getBackCameraMode() {
        return mBackCameraMode;
    }

    static void setCameraIndex(final Context context, final int cameraIndex) {
        mCameraIndex = cameraIndex;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.option_camera_index_key), String.valueOf(cameraIndex))
                .apply();
    }

    static void setFrontCameraMode(final Context context, final String frontCameraMode) {
        mFrontCameraMode = frontCameraMode;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.option_front_camera_mode_key), frontCameraMode)
                .apply();
    }

    static void setBackCameraMode(final Context context, final String backCameraMode) {
        mBackCameraMode = backCameraMode;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(R.string.option_back_camera_mode_key), backCameraMode)
                .apply();
    }

    static int cameraModeToCameraIndex(final int cameraMode) {
        final int hiByte = (cameraMode & 0xff00) >> 8;
        if (hiByte == 0xff) {
            return -1;
        }
        return mCameraSizesSet.get(hiByte).getCameraIndex();
    }

    static ArrayList<CharSequence> getCameraEntries(final Context context) {
        ArrayList<CharSequence> entries = new ArrayList<>();
        ArrayList<CharSequence> values = getCameraValues();
        for (CharSequence value : values) {
            entries.add(getCameraValueDescription(Integer.parseInt((String)value), context));
        }
        return entries;
    }

    static ArrayList<CharSequence> getCameraValues() {
        ArrayList<CharSequence> values = new ArrayList<>();
        values.add(String.valueOf(Constants.Camera.Disabled));
        values.add(String.valueOf(Constants.Camera.Front));
        values.add(String.valueOf(Constants.Camera.Back));
        return values;
    }

    static ArrayList<CharSequence> getCameraModeEntries(final Context context) {
        ArrayList<CharSequence> entries = new ArrayList<>();

        ArrayList<CharSequence> values = getCameraModeValues();
        for (CharSequence value : values) {
            entries.add(getCameraModeValueDescription(value.toString(), context));
        }

        return entries;
    }

    static ArrayList<CharSequence> getCameraModeValues() {
        ArrayList<CharSequence> values = new ArrayList<>();

        values.add("FFFF");
        for (int i = 0; i < mCameraSizesSet.length(); i++) {
            final CameraSizesSet.CameraSizes cameraSizes = mCameraSizesSet.get(i);
            final int cameraNum = cameraSizes.getCameraIndex();
            values.add(integerToHex(cameraNum, 0xff));
            for (int j = 0; j < cameraSizes.getSizesLength(); j++) {
                values.add(integerToHex(cameraNum, j));
            }
        }

        return values;
    }

    private static void loadCameraSizesSet(final Context context) {
        mCameraSizesSet = new CameraSizesSet();

        // Load preference jsonCameraSizesSet only once after app's first launch.
        // Next time we'll read it from preference value.
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonCameraSizesSet = settings.getString(context.getString(R.string.option_json_camera_sizes_set), "");
        if (jsonCameraSizesSet.equals("")) {
            Log.d(TAG, "CameraSizesSet: First load");
            mCameraSizesSet.load();
            try {
                jsonCameraSizesSet = mCameraSizesSet.toJson();
            } catch (JSONException e) {
                Log.e(TAG, "CameraSizesSet: " + e.getMessage());
            }
            settings.edit().putString(context.getString(R.string.option_json_camera_sizes_set), jsonCameraSizesSet).apply();
        } else {
            try {
                mCameraSizesSet.fromJson(jsonCameraSizesSet);
            } catch (JSONException e) {
                Log.e(TAG, "CameraSizesSet: " + e.getMessage());
            }
        }

        Log.d(TAG, "CameraSizesSet: jsonCameraSizesSet=" + jsonCameraSizesSet);
    }

    static String getCameraValueDescription(final int cameraIndex, final Context context) {
        switch (cameraIndex) {
            case Constants.Camera.Front:
                return context.getResources().getString(R.string.option_camera_front_entry);
            case Constants.Camera.Back:
                return context.getResources().getString(R.string.option_camera_back_entry);
            default:
                return context.getResources().getString(R.string.option_camera_disabled_entry);
        }
    }

    private static String getCameraModeValueDescription(int value, final Context context) {
        value &= 0xffff;
        if (value == 0xffff) {
            return context.getResources().getString(R.string.option_camera_disabled_entry);
        }
        // hiByte is the camera index in CameraSizesSet
        int hiByte = value & 0xff00;
        hiByte >>= 8;
        // loByte is the size index in CameraSizesSet for some cameraIndex
        final int loByte = value & 0x00ff;
        final int cameraIndex = mCameraSizesSet.get(hiByte).getCameraIndex() + 1;
        final Resources resources = context.getResources();
        if (loByte == 0xff) {
            return String.format(resources.getString(R.string.option_camera_mode_default_entry),
                    cameraIndex);
        } else {
            CameraSizesSet.Size size = mCameraSizesSet.get(hiByte).getSize(loByte);
            return String.format(resources.getString(R.string.option_camera_mode_size_entry),
                    cameraIndex, size.width, size.height);
        }
    }

    static String getCameraModeValueDescription(String textValue, final Context context) {
        int value = Integer.parseInt(textValue, 16);
        return getCameraModeValueDescription(value, context);
    }

    private static String integerToHex(int hiByte, int loByte) {
        hiByte = hiByte & 0xff;
        hiByte = hiByte << 8;
        loByte = loByte & 0xff;
        int value = hiByte + loByte;
        return Integer.toHexString(0x10000 | value).substring(1).toUpperCase();
    }
}
