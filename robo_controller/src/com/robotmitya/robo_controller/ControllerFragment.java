package com.robotmitya.robo_controller;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.robotmitya.robo_common.Constants;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;

import sensor_msgs.CompressedImage;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 06.05.18.
 */

public class ControllerFragment extends Fragment {

    private ControllerNode mControllerNode;
    private Orientation mOrientation;
    private boolean mSendingOrientation = false;

    private RosImageView<CompressedImage> mVideoView;
    private CheckBox mCheckBoxSendOrientation;
    private VelocityJoystick mVelocityJoystick;
    private TextView mTextOutput;

    public ControllerFragment() {
    }

    public RosImageView<CompressedImage> getVideoView() {
        return mVideoView;
    }

    public Orientation getOrientation() {
        return mOrientation;
    }

    public ControllerNode getControllerNode() {
        return mControllerNode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.controller_fragment, container, false);
        if (result == null)
            return null;

        final Context context = getActivity();

        //noinspection unchecked
        mVideoView = (RosImageView<CompressedImage>) result.findViewById(R.id.imageViewVideo);
        mVideoView.setTopicName(Constants.TopicName.Camera);
        mVideoView.setMessageType(sensor_msgs.CompressedImage._TYPE);
        mVideoView.setMessageToBitmapCallable(new BitmapFromCompressedImage());

        mTextOutput = (TextView) result.findViewById(R.id.textOutput);

        mControllerNode = new ControllerNode();

        mOrientation = new Orientation(context);
        mOrientation.setOnOrientationListener(new Orientation.OnOrientationListener() {
            public void onOrientation(long timestamp, float x, float y, float z, float w) {
                if (mSendingOrientation)
                    mControllerNode.sendOrientation(timestamp, x, y, z, w);
            }
        });

        Button buttonLed1 = (Button) result.findViewById(R.id.buttonLed1);
        buttonLed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControllerNode.switchLed1();
                Log.d(TAG, "LED 1 press");
            }
        });

        Button buttonLed2 = (Button) result.findViewById(R.id.buttonLed2);
        buttonLed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControllerNode.switchLed2();
                Log.d(TAG, "LED 2 press");
            }
        });

        mCheckBoxSendOrientation = (CheckBox) result.findViewById(R.id.checkboxSendOrientation);
        mCheckBoxSendOrientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mControllerNode.setPointingMode(isChecked);
                mSendingOrientation = isChecked;
                mVelocityJoystick.setEnabled(isChecked);
                if (isChecked) {
                    mOrientation.center();
                    mControllerNode.centerHead();
                }
            }
        });

        mVelocityJoystick = (VelocityJoystick) result.findViewById(R.id.velocityJoystick);
        mVelocityJoystick.setEnabled(false);
        mVelocityJoystick.setOnChangeVelocityListener(new VelocityJoystick.OnChangeVelocityListener() {
            @Override
            public void onChangeVelocity(byte velocity) {
                Log.d(TAG, "Velocity = " + velocity);
                mControllerNode.sendDriveTowards(velocity);
            }
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(20);
                        Activity activity = getActivity();
                        if (activity == null)
                            return;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String text1 = mCheckBoxSendOrientation.isChecked() ?
                                        mOrientation.getFrameSensorText() : "";
                                mTextOutput.setText(text1);
                            }
                        });
                    }
                } catch (InterruptedException ignored) {
                }
            }
        }.start();

//        setControllerFullscreen();

        return result;
    }

    public void setFullscreen() {
        if (mVideoView != null)
            mVideoView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
