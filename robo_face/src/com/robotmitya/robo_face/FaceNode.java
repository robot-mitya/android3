package com.robotmitya.robo_face;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import com.robotmitya.robo_common.Constants;

import std_msgs.String;

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
            public void onNewMessage(std_msgs.String string) {
                int resource;
                if (string.getData().contentEquals("happy")) {

                } else if (string.getData().contentEquals("blue")) {

                } else if (string.getData().contentEquals("angry")) {

                } else if (string.getData().contentEquals("ill")) {

                } else {

                }
                //mFaceHelper.setFace(resource);
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
