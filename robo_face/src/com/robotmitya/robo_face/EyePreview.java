package com.robotmitya.robo_face;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;

import com.robotmitya.robo_common.Constants;

import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;

import java.util.List;

/**
 *
 * Created by dmitrydzz on 21.04.18.
 */
public final class EyePreview extends RosCameraPreviewView {
    final class Broadcast {
        final class CameraSettings {
            static final String IntentName = "com.robotmitya.robo_face.CAMERA_SETTINGS";
            static final String CameraIndexExtraParamName = "I";
            static final String CameraModeExtraParamName = "M";
        }
    }

    private int mSelectedCameraMode;

    private BroadcastReceiver mCameraSettingsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int cameraIndex = intent.getIntExtra(
                    Broadcast.CameraSettings.CameraIndexExtraParamName, 0);
            if (cameraIndex == Constants.Camera.Disabled) {
                stopVideoStreaming();
            } else {
                final String cameraMode = intent.getStringExtra(
                        Broadcast.CameraSettings.CameraModeExtraParamName);
                startVideoStreaming(cameraMode);
            }
        }
    };

    public EyePreview(Context context) {
        super(context);
    }

    public EyePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EyePreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(Constants.NodeName.Eye);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        super.onStart(connectedNode);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                mCameraSettingsBroadcastReceiver, new IntentFilter(Broadcast.CameraSettings.IntentName));
    }

    @Override
    public void onShutdown(Node node) {
        super.onShutdown(node);
        stopVideoStreaming();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mCameraSettingsBroadcastReceiver);
    }

    void startVideoStreaming(final String cameraMode) {
        mSelectedCameraMode = Integer.parseInt(cameraMode, 16);
        if (mSelectedCameraMode == 0xffff) {
            stopVideoStreaming();
            return;
        }

        final int cameraIndex = SettingsFace.cameraModeToCameraIndex(mSelectedCameraMode);

        // Start of video streaming is delayed.
        final Handler h = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                releaseCamera();
                //noinspection deprecation
                setCamera(Camera.open(cameraIndex));
            }
        };
        h.postDelayed(r, 1000);

//        Message message = new Message();
//        message.arg1 = VIDEO_STARTED;
//        message.arg2 = cameraIndex;
//        mHandler.sendMessage(message);
    }

    void stopVideoStreaming() {
        releaseCamera();

//        Message message = new Message();
//        message.arg1 = VIDEO_STOPPED;
//        message.arg2 = -1;
//        mHandler.sendMessage(message);
    }

    @SuppressWarnings("deprecation")
    protected Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        final int sizeIndex = mSelectedCameraMode & 0xff;
        if (sizeIndex == 0xff) {
            return super.getOptimalPreviewSize(sizes, width, height);
        }
        return sizes.get(sizeIndex);
    }
}
