package com.robotmitya.robo_face;

/**
 *
 * Created by dmitrydzz on 05.04.18.
 */

public class Madgwick {
    // Gets or sets the algorithm gain beta.
    private float beta_;

    /// Gets or sets the quaternion_ output (local to world).
    private float[] quaternion_;

    public float getBeta() {
        return beta_;
    }

    public void setBeta(float value) {
        beta_ = value;
    }

    public float[] getQuaternion() {
        return quaternion_;
    }

    public void setQuaternion(float w, float x, float y, float z) {
        quaternion_[0] = w;
        quaternion_[1] = x;
        quaternion_[2] = y;
        quaternion_[3] = z;
    }

    /// Initializes a new instance of the Madgwick class.
    /// beta: Algorithm gain beta.
    public Madgwick(float beta)
    {
        beta_ = beta;
        quaternion_ = new float[] { 1f, 0f, 0f, 0f };
    }

    /// Algorithm IMU update method. Requires only gyroscope and accelerometer data.
    /// gx: Gyroscope x axis measurement in radians/s.
    /// gy: Gyroscope y axis measurement in radians/s.
    /// gz: Gyroscope z axis measurement in radians/s.
    /// ax: Accelerometer x axis measurement in any calibrated units.
    /// ay: Accelerometer y axis measurement in any calibrated units.
    /// az: Accelerometer z axis measurement in any calibrated units.
    public void update(float deltaTime, float gx, float gy, float gz, float ax, float ay, float az)
    {
        float q1 = quaternion_[0], q2 = quaternion_[1], q3 = quaternion_[2], q4 = quaternion_[3];   // short name local variable for readability
        float norm;
        float s1, s2, s3, s4;
        float qDot1, qDot2, qDot3, qDot4;

        // Auxiliary variables to avoid repeated arithmetic
        float _2q1 = 2f * q1;
        float _2q2 = 2f * q2;
        float _2q3 = 2f * q3;
        float _2q4 = 2f * q4;
        float _4q1 = 4f * q1;
        float _4q2 = 4f * q2;
        float _4q3 = 4f * q3;
        float _8q2 = 8f * q2;
        float _8q3 = 8f * q3;
        float q1q1 = q1 * q1;
        float q2q2 = q2 * q2;
        float q3q3 = q3 * q3;
        float q4q4 = q4 * q4;

        // Normalise accelerometer measurement
        norm = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f) return; // handle NaN
        norm = 1 / norm;        // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Gradient decent algorithm corrective step
        s1 = _4q1 * q3q3 + _2q3 * ax + _4q1 * q2q2 - _2q2 * ay;
        s2 = _4q2 * q4q4 - _2q4 * ax + 4f * q1q1 * q2 - _2q1 * ay - _4q2 + _8q2 * q2q2 + _8q2 * q3q3 + _4q2 * az;
        s3 = 4f * q1q1 * q3 + _2q1 * ax + _4q3 * q4q4 - _2q4 * ay - _4q3 + _8q3 * q2q2 + _8q3 * q3q3 + _4q3 * az;
        s4 = 4f * q2q2 * q4 - _2q2 * ax + 4f * q3q3 * q4 - _2q3 * ay;
        norm = 1f / (float) Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4);    // normalise step magnitude
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;

        // Compute rate of change of quaternion
        qDot1 = 0.5f * (-q2 * gx - q3 * gy - q4 * gz) - beta_ * s1;
        qDot2 = 0.5f * (q1 * gx + q3 * gz - q4 * gy) - beta_ * s2;
        qDot3 = 0.5f * (q1 * gy - q2 * gz + q4 * gx) - beta_ * s3;
        qDot4 = 0.5f * (q1 * gz + q2 * gy - q3 * gx) - beta_ * s4;

        // Integrate to yield quaternion
        q1 += qDot1 * deltaTime;
        q2 += qDot2 * deltaTime;
        q3 += qDot3 * deltaTime;
        q4 += qDot4 * deltaTime;
        norm = 1f / (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);    // normalise quaternion
        quaternion_[0] = q1 * norm;
        quaternion_[1] = q2 * norm;
        quaternion_[2] = q3 * norm;
        quaternion_[3] = q4 * norm;
    }
}
