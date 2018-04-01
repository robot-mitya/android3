package com.robotmitya.robo_controller;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import static com.robotmitya.robo_controller.Constants.TAG;

/**
 *
 * Created by dmitrydzz on 29.03.18.
 */

public class ControllerNode implements NodeMain {

    private String mArduinoInputTopicName;
    private Publisher<std_msgs.String> mArduinoInputPublisher;

    ControllerNode() {
        mArduinoInputTopicName = Constants.TopicName.ArduinoInput;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(Constants.NodeName.Controller);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mArduinoInputPublisher = connectedNode.newPublisher(mArduinoInputTopicName, "std_msgs/String");
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

    void switchLed1() {
        android.util.Log.d(TAG, "11");
        std_msgs.String str = mArduinoInputPublisher.newMessage();
        str.setData("L1 -1;");
        android.util.Log.d(TAG, "12");
        mArduinoInputPublisher.publish(str);
        android.util.Log.d(TAG, "13");
    }

    void switchLed2() {
        android.util.Log.d(TAG, "21");
        std_msgs.String str = mArduinoInputPublisher.newMessage();
        str.setData("L2 -1;");
        android.util.Log.d(TAG, "22");
        mArduinoInputPublisher.publish(str);
        android.util.Log.d(TAG, "23");
    }
}
