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
import android.view.WindowManager;

import com.robotmitya.robo_common.Constants;
import com.robotmitya.robo_common.SettingsCommon;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 * @author DmitryDzz
 */
public class MainActivity extends RosActivity {

    private SettingsFragment mSettingsFragment;
    private FaceFragment mFaceFragment;
    private EyePreview mEyePreview;

    public enum FragmentType { FACE, SETTINGS }
    public static FragmentType fragmentType;

    public MainActivity() {
        super("RoboFace", "RoboFace");

        FaceHelper faceHelper = new FaceHelper(this);
        FaceNode faceNode = new FaceNode(faceHelper);

        mSettingsFragment = new SettingsFragment();
        mFaceFragment = new FaceFragment();
        mFaceFragment.setFaceNode(faceNode);
        mFaceFragment.setSettingsFragment(mSettingsFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SettingsCommon.load(this);
        SettingsFace.load(this);

        if (findViewById(R.id.fragment_container) == null)
            return;

        if (savedInstanceState != null)
            return;

        fragmentType = FragmentType.FACE;
        getFragmentManager().beginTransaction().add(R.id.fragment_container, mFaceFragment).commit();

        mEyePreview = (EyePreview) findViewById(R.id.eye_preview);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final short selectedCamera = (short) SettingsFace.getCameraIndex();
        if (selectedCamera == Constants.Camera.Front) {
            mEyePreview.startVideoStreaming(SettingsFace.getFrontCameraMode());
        } else if (selectedCamera == Constants.Camera.Back) {
            mEyePreview.startVideoStreaming(SettingsFace.getBackCameraMode());
        } else {
            mEyePreview.stopVideoStreaming();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
// 1. getHostAddress() не работает. Функция возвращает первый попавшийся IP4 - или WIFI- или GSM-адрес.
//        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
//                InetAddressFactory.newNonLoopback().getHostAddress());
// 2. RoboHelper.wifiIpAddress() тоже не работает. При использовании VPN возвращает не тот адрес.
//    Пришлось делать опцию.
        String ipAddress = SettingsCommon.getLocalIp();
        Log.i(TAG, "IP address: " + ipAddress);
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(ipAddress);
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(mFaceFragment.getFaceNode(), nodeConfiguration);
        nodeMainExecutor.execute(mEyePreview, nodeConfiguration);
    }

    @Override
    public void startMasterChooser() {
        Intent data = new Intent();
        //data.putExtra("ROS_MASTER_URI", "http://192.168.100.3:11311");
        data.putExtra("ROS_MASTER_URI", SettingsCommon.getMasterUri());
        data.putExtra("NEW_MASTER", false);
        data.putExtra("ROS_MASTER_PRIVATE", false);
        onActivityResult(0, RESULT_OK, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (fragmentType == FragmentType.FACE)
                mFaceFragment.setFaceFullscreen();
            else if (fragmentType == FragmentType.SETTINGS)
                mSettingsFragment.setSettingsFullscreen();
        }
    }
}
