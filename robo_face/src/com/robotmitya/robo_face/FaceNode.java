package com.robotmitya.robo_face;

import android.icu.text.LocaleDisplayNames;
import android.util.Log;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import com.robotmitya.robo_common.Constants;

import static com.robotmitya.robo_common.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 14.04.18.
 */
class FaceNode implements NodeMain {

    private final FaceHelper mFaceHelper;

    FaceNode(final FaceHelper faceHelper) {
        mFaceHelper = faceHelper;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(Constants.NodeName.Face);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber(
                Constants.TopicName.Face, std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                //Log.d(TAG, "+++++++++++++ " + message.getData());
                FaceType faceType;
                if (message.getData().contentEquals("angry")) {
                    faceType = FaceType.ftAngry;
                } else if (message.getData().contentEquals("blue")) {
                    faceType = FaceType.ftBlue;
                } else if (message.getData().contentEquals("happy")) {
                        faceType = FaceType.ftHappy;
                } else if (message.getData().contentEquals("ill")) {
                    faceType = FaceType.ftIll;
                } else if (message.getData().contentEquals("ok")) {
                    faceType = FaceType.ftOk;
                } else {
                    Log.e(TAG, String.format("Wrong command in the \'face\' topic: \'%s\'", message));
                    return;
                }
                mFaceHelper.setFace(faceType);
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
    }

    @Override
    public void onShutdownComplete(Node node) {
    }

    @Override
    public void onError(Node node, Throwable throwable) {
    }
}
