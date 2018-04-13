/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.robotmitya.robo_face;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import static com.robotmitya.robo_face.Constants.TAG;

/**
 * @author DmitryDzz
 */
public class MainActivity extends RosActivity {

    private ControllerNode mControllerNode;
    private Orientation mOrientation;
    private boolean mSendingOrientation = false;

    private CheckBox mCheckBoxSendOrientation;
    private VelocityJoystick mVelocityJoystick;
    private TextView mTextOutput;

    public MainActivity() {
        super("RoboFace", "RoboFace");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mTextOutput = (TextView) findViewById(R.id.textOutput);

        mControllerNode = new ControllerNode();

        mOrientation = new Orientation(this);
        mOrientation.setOnOrientationListener(new Orientation.OnOrientationListener() {
            public void onOrientation(long timestamp, float x, float y, float z, float w) {
                if (mSendingOrientation)
                    mControllerNode.sendOrientation(timestamp, x, y, z, w);
            }
        });

        Button buttonLed1 = (Button) findViewById(R.id.buttonLed1);
        buttonLed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControllerNode.switchLed1();
                Log.d(TAG, "LED 1 press");
            }
        });

        Button buttonLed2 = (Button) findViewById(R.id.buttonLed2);
        buttonLed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControllerNode.switchLed2();
                Log.d(TAG, "LED 2 press");
            }
        });

        mCheckBoxSendOrientation = (CheckBox) findViewById(R.id.checkboxSendOrientation);
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

        mVelocityJoystick = (VelocityJoystick) findViewById(R.id.velocityJoystick);
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
                        runOnUiThread(new Runnable() {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mOrientation.start();
    }

    @Override
    protected void onStop() {
        mOrientation.stop();
        super.onStop();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
// getHostAddress() не работает. Функция возвращает первый попавшийся IP4 - или WIFI- или GSM-адрес.
//        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
//                InetAddressFactory.newNonLoopback().getHostAddress());
        String ipAddress = RoboHelper.wifiIpAddress(this);
        Log.i(TAG, "IP address: " + ipAddress);
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(ipAddress);
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(mControllerNode, nodeConfiguration);
    }

    @Override
    public void startMasterChooser() {
        Intent data = new Intent();
        //Log.d(this, "++++++++++++ ROS_MASTER_URI=" + SettingsFragment.getMasterUri());
        data.putExtra("ROS_MASTER_URI", "http://192.168.100.3:11311");
        data.putExtra("NEW_MASTER", false);
        data.putExtra("ROS_MASTER_PRIVATE", false);
        onActivityResult(0, RESULT_OK, data);
    }
}
