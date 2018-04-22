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

import com.robotmitya.robo_common.RoboHelper;
import com.robotmitya.robo_common.SettingsCommon;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 * @author DmitryDzz
 */
public class MainActivity extends RosActivity {

    private FaceHelper mFaceHelper;
    private FaceNode mFaceNode;

    private SettingsFragment mSettingsFragment;
    private FaceFragment mFaceFragment;

    public enum FragmentType { FACE, SETTINGS }
    public static FragmentType fragmentType;

    public MainActivity() {
        super("RoboFace", "RoboFace");

        mFaceHelper = new FaceHelper(this);
        mFaceNode = new FaceNode(mFaceHelper);

        mSettingsFragment = new SettingsFragment();
        mFaceFragment = new FaceFragment();
        mFaceFragment.setFaceNode(mFaceNode);
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

        nodeMainExecutor.execute(mFaceFragment.getFaceNode(), nodeConfiguration);
    }

    @Override
    public void startMasterChooser() {
        Intent data = new Intent();
        //Log.d(TAG, "+++++++++++++++++++++++++++++ " + SettingsCommon.getMasterUri());
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
