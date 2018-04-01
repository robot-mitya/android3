package com.robotmitya.robo_controller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 *
 * Created by dmitrydzz on 01.04.18.
 */

public class Orientation implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private OnOrientationListener mOnOrientationListener;

    private Quaternionf mQuaternionSensor = new Quaternionf();
    private Quaternionf mQuaternionCenter = new Quaternionf();
    private Quaternionf mQuaternion = new Quaternionf();

    private Vector3f mTestWorldX = new Vector3f(1, 0, 0);
    private Vector3f mTestWorldY = new Vector3f(0, 1, 0);
    private Vector3f mTestWorldZ = new Vector3f(0, 0, 1);
    private Vector3f mX = new Vector3f();
    private Vector3f mY = new Vector3f();
    private Vector3f mZ = new Vector3f();

    Orientation(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager == null)
            throw new NullPointerException("SensorManager is not initialized.");
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        mOnOrientationListener = onOrientationListener;
    }

    void start() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    void stop() {
        mSensorManager.unregisterListener(this);
    }

    void center() {
        mQuaternionSensor.conjugate(mQuaternionCenter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR)
            return;

        if (event.values.length > 3)
            mQuaternionSensor.set(event.values[0], event.values[1], event.values[2], event.values[3]);
        else
            mQuaternionSensor.set(event.values[0], event.values[1], event.values[2]);
        mQuaternion.set(mQuaternionSensor);
        mQuaternion.mul(mQuaternionCenter);

        mX.set(mTestWorldX);
        mY.set(mTestWorldY);
        mZ.set(mTestWorldZ);
        mQuaternion.transform(mX);
        mQuaternion.transform(mY);
        mQuaternion.transform(mZ);
        if (mOnOrientationListener != null)
            mOnOrientationListener.onOrientation(event.timestamp,
                    mQuaternion.x, mQuaternion.y, mQuaternion.z, mQuaternion.w);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface OnOrientationListener {
        void onOrientation(long timestamp, float x, float y, float z, float w);
    }
}
