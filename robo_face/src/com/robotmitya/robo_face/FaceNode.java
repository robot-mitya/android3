package com.robotmitya.robo_face;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;

import geometry_msgs.Quaternion;
import std_msgs.Header;

/**
 *
 * Created by dmitrydzz on 14.04.18.
 */

public class FaceNode implements NodeMain {

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(Constants.NodeName.Controller);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
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
