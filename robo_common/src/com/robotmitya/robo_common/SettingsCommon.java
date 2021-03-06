package com.robotmitya.robo_common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 *
 * Created by dmitrydzz on 21.04.18.
 */
public class SettingsCommon {

    private static String mLocalIp;
    private static String mMasterUri;

    public static void load(final Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        mLocalIp = settings.getString(
                context.getResources().getString(R.string.option_local_ip_key),
                RoboHelper.wifiIpAddress(context));
        mMasterUri = settings.getString(
                context.getResources().getString(R.string.option_master_uri_key),
                context.getResources().getString(R.string.option_master_uri_default_value));
    }

    public static void save(final Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        final SharedPreferences.Editor editor = settings.edit();
        editor.putString(context.getResources().getString(R.string.option_local_ip_key), mLocalIp);
        editor.putString(context.getResources().getString(R.string.option_master_uri_key), mMasterUri);
        editor.apply();
    }

    public static String getLocalIp() {
        return mLocalIp;
    }

    public static  void setLocalIp(final String localIp) {
        mLocalIp = localIp;
    }

    public static String getMasterUri() {
        return mMasterUri;
    }

    public static void setMasterUri(final String masterUri) {
        mMasterUri = masterUri;
    }
}
