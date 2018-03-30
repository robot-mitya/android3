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

package com.robotmitya.robo_controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
//import org.ros.rosjava_tutorial_pubsub.Talker;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity {

    private ArduinoInputPublisher mArduinoInputPublisher;

    public MainActivity() {
        super("RoboController", "RoboController");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button buttonLed1 = (Button) findViewById(R.id.buttonLed1);
        buttonLed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArduinoInputPublisher.switchLed1();
                Log.d("++++", "LED 1 press");
            }
        });

        Button buttonLed2 = (Button) findViewById(R.id.buttonLed2);
        buttonLed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mArduinoInputPublisher.switchLed2();
                Log.d("++++", "LED 2 press");
            }
        });
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        mArduinoInputPublisher = new ArduinoInputPublisher();

        // At this point, the user has already been prompted to either enter the URI
        // of a master to use or to start a master locally.

        // The user can easily use the selected ROS Hostname in the master chooser
        // activity.
        //NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
                InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeMainExecutor.execute(mArduinoInputPublisher, nodeConfiguration);

/*
    // The RosTextView is also a NodeMain that must be executed in order to
    // start displaying incoming messages.
    nodeMainExecutor.execute(rosTextView, nodeConfiguration);*/

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
