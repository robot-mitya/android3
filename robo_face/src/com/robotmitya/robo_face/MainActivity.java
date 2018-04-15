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
import android.widget.ImageView;

import com.robotmitya.robo_common.RoboHelper;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 * @author DmitryDzz
 */
public class MainActivity extends RosActivity {

    private FaceNode mFaceNode;
    private ImageView mFaceImage;

    @SuppressWarnings("FieldCanBeLocal")
    private FaceHelper mFaceHelper;

    public MainActivity() {
        super("RoboFace", "RoboFace");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mFaceImage = (ImageView) findViewById(R.id.faceImage);
        mFaceHelper = new FaceHelper(this, mFaceImage);
        mFaceNode = new FaceNode(mFaceHelper);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
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

        nodeMainExecutor.execute(mFaceNode, nodeConfiguration);
    }

    @Override
    public void startMasterChooser() {
        Intent data = new Intent();
        data.putExtra("ROS_MASTER_URI", "http://192.168.100.3:11311");
        data.putExtra("NEW_MASTER", false);
        data.putExtra("ROS_MASTER_PRIVATE", false);
        onActivityResult(0, RESULT_OK, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mFaceImage.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
