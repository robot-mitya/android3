package com.robotmitya.robo_face;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import com.robotmitya.robo_common.Constants;

import org.ros.android.view.camera.RosCameraPreviewView;
import org.ros.namespace.GraphName;
import org.ros.node.Node;

/**
 *
 * Created by dmitrydzz on 21.04.18.
 */
public class EyePreview extends RosCameraPreviewView {
    private int mSelectedCameraMode;

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
    public void onShutdown(Node node) {
        super.onShutdown(node);
        stopVideoStreaming();
    }

    public void startVideoStreaming(final String cameraMode) {
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
                setCamera(Camera.open(cameraIndex));
            }
        };
        h.postDelayed(r, 1000);

//        Message message = new Message();
//        message.arg1 = VIDEO_STARTED;
//        message.arg2 = cameraIndex;
//        mHandler.sendMessage(message);
    }

    public void stopVideoStreaming() {
        releaseCamera();

//        Message message = new Message();
//        message.arg1 = VIDEO_STOPPED;
//        message.arg2 = -1;
//        mHandler.sendMessage(message);
    }
}
