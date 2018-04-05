package com.robotmitya.robo_controller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

import static com.robotmitya.robo_controller.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 01.04.18.
 */

public class Orientation implements SensorEventListener {
    // Gyroscope measurement error in deg/s:
    private final float gyroMeasError = 5.0f;
    // Gyroscope measurement error in rad/s:
    private final float gyroMeasErrorInRadians = (float) Math.PI * gyroMeasError / 180.0f;
    // Compute beta:
    private final float BETA = 0.8660254f * gyroMeasErrorInRadians; // Mathf.Sqrt(3.0f / 4.0f) * gyroMeasErrorInRadians;
    // Nanoseconds to seconds:
    private final float NS2S = 1f / 1000000000f;

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mGyroscope;

    private final Madgwick madgwick_ = new Madgwick(BETA);

    private OnOrientationListener mOnOrientationListener;

    private final Quaternionf mQuaternionSensor = new Quaternionf();
    private final Quaternionf mQuaternionCenter = new Quaternionf();
    private final Quaternionf mQuaternion = new Quaternionf();

    private final Vector3f mTestWorldX = new Vector3f(1, 0, 0);
    private final Vector3f mTestWorldY = new Vector3f(0, 1, 0);
    private final Vector3f mTestWorldZ = new Vector3f(0, 0, 1);
    private final Vector3f mX = new Vector3f();
    private final Vector3f mY = new Vector3f();
    private final Vector3f mZ = new Vector3f();
    private final Vector3f mTempX1 = new Vector3f();
    private final Vector3f mTempY1 = new Vector3f();
    private final Vector3f mTempZ1 = new Vector3f();
    private final Vector3f mTempX2 = new Vector3f();
    private final Vector3f mTempY2 = new Vector3f();
    private final Vector3f mTempZ2 = new Vector3f();

    Orientation(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager == null)
            throw new NullPointerException("SensorManager is not initialized.");
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        mOnOrientationListener = onOrientationListener;
    }

    void start() {
        final int samplingPeriod = SensorManager.SENSOR_DELAY_GAME;
        mSensorManager.registerListener(this, mAccelerometer, samplingPeriod);
        mSensorManager.registerListener(this, mGyroscope, samplingPeriod);
    }

    void stop() {
        mSensorManager.unregisterListener(this);
    }

    private Quaternionf mW2L = new Quaternionf();
    private Quaternionf mL2W = new Quaternionf();
    private Vector3f x_ = new Vector3f();
    private Vector3f y_ = new Vector3f();
    private Vector3f z_ = new Vector3f();
    private Matrix3f m_ = new Matrix3f();

    void center() {
//        mQuaternionSensor.conjugate(mQuaternionCenter);

        mW2L.set(mQuaternionSensor);
        mL2W.set(mQuaternionSensor).invert();
        y_.set(0, 1, 0);
        mL2W.transform(y_);
//Log.d(TAG, "y_=" + getVectorText(y_));
        x_.set(y_);
        z_.set(0, 0, 1);
        x_.cross(z_);
        x_.normalize();
        z_.set(x_);
        z_.cross(y_);
        m_.m00 = x_.x;  m_.m01 = y_.x;  m_.m02 = z_.x;
        m_.m10 = x_.y;  m_.m11 = y_.y;  m_.m12 = z_.y;
        m_.m20 = x_.z;  m_.m21 = y_.z;  m_.m22 = z_.z;
        mQuaternionCenter.setFromNormalized(m_);
        mQuaternionCenter.invert();
    }

    private String getVectorText(final Vector3f v) {
        return String.format(Locale.ENGLISH, "%+5.3f %+5.3f %+5.3f", v.x, v.y, v.z);
    }

//    private String getQuaternionSensorText() {
//        mTempX2.set(1, 0, 0);
//        mTempY2.set(0, 1, 0);
//        mTempZ2.set(0, 0, 1);
//        mQuaternionSensor.transform(mTempX2);
//        mQuaternionSensor.transform(mTempY2);
//        mQuaternionSensor.transform(mTempZ2);
//        return String.format(Locale.ENGLISH,
//                "x:(%+5.3f %+5.3f %+5.3f)    y:(%+5.3f %+5.3f %+5.3f)    z:(%+5.3f %+5.3f %+5.3f)",
//                mTempX2.x, mTempX2.y, mTempX2.z,
//                mTempY2.x, mTempY2.y, mTempY2.z,
//                mTempZ2.x, mTempZ2.y, mTempZ2.z);
//    }

    String getMatrixAfterCenter() {
        return String.format(Locale.ENGLISH,
                "%+5.3f %+5.3f %+5.3f\n" +
                "%+5.3f %+5.3f %+5.3f\n" +
                "%+5.3f %+5.3f %+5.3f",
                m_.m00, m_.m01, m_.m02,
                m_.m10, m_.m11, m_.m12,
                m_.m20, m_.m21, m_.m22);
    }

    String getFrameSensorText() {
        mTempX1.set(1, 0, 0);
        mTempY1.set(0, 1, 0);
        mTempZ1.set(0, 0, 1);
        mQuaternionSensor.transform(mTempX1);
        mQuaternionSensor.transform(mTempY1);
        mQuaternionSensor.transform(mTempZ1);
        return String.format(Locale.ENGLISH,
                "%+5.3f %+5.3f %+5.3f\n" +
                        "%+5.3f %+5.3f %+5.3f\n" +
                        "%+5.3f %+5.3f %+5.3f",
                mTempX1.x, mTempY1.x, mTempZ1.x,
                mTempX1.y, mTempY1.y, mTempZ1.y,
                mTempX1.z, mTempY1.z, mTempZ1.z);
    }

    String getFrameResultText() {
        mTempX1.set(1, 0, 0);
        mTempY1.set(0, 1, 0);
        mTempZ1.set(0, 0, 1);
        mQuaternion.transform(mTempX1);
        mQuaternion.transform(mTempY1);
        mQuaternion.transform(mTempZ1);
        return String.format(Locale.ENGLISH,
                "%+5.3f %+5.3f %+5.3f\n" +
                "%+5.3f %+5.3f %+5.3f\n" +
                "%+5.3f %+5.3f %+5.3f",
                mTempX1.x, mTempY1.x, mTempZ1.x,
                mTempX1.y, mTempY1.y, mTempZ1.y,
                mTempX1.z, mTempY1.z, mTempZ1.z);
    }

    private Vector3f acc_ = new Vector3f();
    private Vector3f gyro_ = new Vector3f();
    private float prevTimestamp_ = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            acc_.set(event.values[0], event.values[1], event.values[2]);
            return;
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyro_.set(event.values[0], event.values[1], event.values[2]);
        } else {
            return;
        }

        if (prevTimestamp_ == 0) {
            prevTimestamp_ = event.timestamp;
            return;
        }

        madgwick_.update((event.timestamp - prevTimestamp_) * NS2S,
                gyro_.x, gyro_.y, gyro_.z, acc_.x, acc_.y, acc_.z);
        prevTimestamp_ = event.timestamp;

        float[] madgwickQuaternion = madgwick_.getQuaternion();
        mQuaternionSensor.set(madgwickQuaternion[1], madgwickQuaternion[2], madgwickQuaternion[3], madgwickQuaternion[0]);
//        mQuaternionSensor.set(madgwickQuaternion[0], madgwickQuaternion[1], madgwickQuaternion[2], madgwickQuaternion[3]);

//Log.d(TAG, getQuaternionSensorText());

        mQuaternion.set(mQuaternionCenter);
        mQuaternion.mul(mQuaternionSensor);

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
