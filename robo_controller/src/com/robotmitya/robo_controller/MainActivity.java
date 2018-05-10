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
import android.view.WindowManager;

import com.robotmitya.robo_common.SettingsCommon;

import junit.framework.Assert;

import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 * @author DmitryDzz
 */
public class MainActivity extends RosActivity {

    private volatile NodeConfiguration mNodeConfiguration = null;
    private volatile NodeMainExecutor mNodeMainExecutor = null;
    private boolean mNodesStarted = false;

    private SettingsFragment mSettingsFragment;
    private ControllerFragment mControllerFragment;

    private enum FragmentType { CONTROLLER, SETTINGS }
    private FragmentType mFragmentType;

    public MainActivity() {
        super("RoboController", "RoboController");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SettingsCommon.load(this);

        if (findViewById(R.id.fragment_container) == null)
            return;

        if (savedInstanceState != null)
            return;

        mSettingsFragment = new SettingsFragment();
        mSettingsFragment.setOnStartFragmentListener(new SettingsFragment.OnStartFragmentListener() {
            @Override
            public void OnStartFragment() {
                mFragmentType = FragmentType.SETTINGS;
            }
        });
        mSettingsFragment.setOnStopFragmentListener(new SettingsFragment.OnStopFragmentListener() {
            @Override
            public void OnStopFragment() {
            }
        });

        mControllerFragment = new ControllerFragment();
        mControllerFragment.setSettingsFragment(mSettingsFragment);
        mControllerFragment.setOnStartFragmentListener(new ControllerFragment.OnStartFragmentListener() {
            @Override
            public void OnStartFragment() {
                mFragmentType = FragmentType.CONTROLLER;
                startNodes();
            }
        });
        mControllerFragment.setOnStopFragmentListener(new ControllerFragment.OnStopFragmentListener() {
            @Override
            public void OnStopFragment() {
                stopNodes();
            }
        });

        mFragmentType = FragmentType.CONTROLLER;
        getFragmentManager().beginTransaction().add(R.id.fragment_container, mControllerFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Assert.assertNotNull(mControllerFragment.getOrientation());
        mControllerFragment.getOrientation().start();
    }

    @Override
    protected void onStop() {
        Assert.assertNotNull(mControllerFragment.getOrientation());
        mControllerFragment.getOrientation().stop();
        super.onStop();
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
// 1. getHostAddress() не работает. Функция возвращает первый попавшийся IP4 - или WIFI- или GSM-адрес.
//        NodeConfiguration mNodeConfiguration = NodeConfiguration.newPublic(
//                InetAddressFactory.newNonLoopback().getHostAddress());
// 2. RoboHelper.wifiIpAddress() тоже не работает. При использовании VPN возвращает не тот адрес.
//    Пришлось делать опцию.
        //String ipAddress = "10.8.0.4";
        String ipAddress = SettingsCommon.getLocalIp();
        Log.i(TAG, "Environment variable ROS_IP=" + ipAddress);
        mNodeConfiguration = NodeConfiguration.newPublic(ipAddress);
        mNodeConfiguration.setMasterUri(getMasterUri());

        mNodeMainExecutor = nodeMainExecutor;
        startNodes();
    }

    private void startNodes() {
        if (mNodesStarted || mNodeMainExecutor == null || mNodeConfiguration == null) return;
        //Log.d(TAG, "Starting nodes");

        Assert.assertNotNull(mControllerFragment.getVideoView());
        mNodeMainExecutor.execute(mControllerFragment.getVideoView(), mNodeConfiguration);

        Assert.assertNotNull(mControllerFragment.getControllerNode());
        mNodeMainExecutor.execute(mControllerFragment.getControllerNode(), mNodeConfiguration);

        mNodesStarted = true;
    }

    private void stopNodes() {
        if (!mNodesStarted || mNodeMainExecutor == null || mNodeConfiguration == null) return;
        //Log.d(TAG, "Stopping nodes");

        mNodeMainExecutor.shutdownNodeMain(mControllerFragment.getVideoView());
        mNodeMainExecutor.shutdownNodeMain(mControllerFragment.getControllerNode());

        mNodesStarted = false;
    }

    @Override
    public void startMasterChooser() {
        Intent data = new Intent();
        //data.putExtra("ROS_MASTER_URI", "http://192.168.100.3:11311");
        //data.putExtra("ROS_MASTER_URI", "http://10.8.0.2:11311");
        final String masterURI = SettingsCommon.getMasterUri();
        Log.i(TAG, "Environment variable ROS_MASTER_URI=" + masterURI);
        data.putExtra("ROS_MASTER_URI", masterURI);
        data.putExtra("NEW_MASTER", false);
        data.putExtra("ROS_MASTER_PRIVATE", false);
        onActivityResult(0, RESULT_OK, data);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (mFragmentType == FragmentType.CONTROLLER) {
                mControllerFragment.setFullscreen();
            } else if (mFragmentType == FragmentType.SETTINGS) {
                mSettingsFragment.setSettingsFullscreen();
            }
        }
    }
}
