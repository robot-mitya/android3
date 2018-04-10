package com.robotmitya.robo_controller;

import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import geometry_msgs.Quaternion;
import std_msgs.Header;

/**
 *
 * Created by dmitrydzz on 29.03.18.
 */

public class ControllerNode implements NodeMain {

    private Publisher<std_msgs.String> mArduinoInputPublisher;
    private Publisher<std_msgs.String> mHerkulexInputPublisher;
    private Publisher<sensor_msgs.Imu> mControllerImuPublisher;
    private Publisher<std_msgs.Int8> mDriveTowardsPublisher;

    private int mImuSeq = 0;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(Constants.NodeName.Controller);
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        mArduinoInputPublisher = connectedNode.newPublisher(Constants.TopicName.ArduinoInput, "std_msgs/String");
        mHerkulexInputPublisher = connectedNode.newPublisher(Constants.TopicName.HerkulexInput, "std_msgs/String");
        mControllerImuPublisher = connectedNode.newPublisher(Constants.TopicName.ControllerImu, "sensor_msgs/Imu");
        mDriveTowardsPublisher = connectedNode.newPublisher(Constants.TopicName.DriveTowards, "std_msgs/Int8");
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
        std_msgs.String message = mArduinoInputPublisher.newMessage();
        message.setData("L1 -1;");
        mArduinoInputPublisher.publish(message);
    }

    void switchLed2() {
        std_msgs.String message = mArduinoInputPublisher.newMessage();
        message.setData("L2 -1;");
        mArduinoInputPublisher.publish(message);
    }

    void sendOrientation(long timestamp, float x, float y, float z, float w) {
        sensor_msgs.Imu message = mControllerImuPublisher.newMessage();
        Header header = message.getHeader();
        header.setSeq(mImuSeq++);
        header.setFrameId("c");
        header.setStamp(Time.fromNano(timestamp));
        Quaternion quaternion = message.getOrientation();
        quaternion.setX(x);
        quaternion.setY(y);
        quaternion.setZ(z);
        quaternion.setW(w);
        mControllerImuPublisher.publish(message);
    }

    void sendDriveTowards(byte velocity) {
        std_msgs.Int8 message = mDriveTowardsPublisher.newMessage();
        message.setData(velocity);
        mDriveTowardsPublisher.publish(message);
    }

    void setPointingMode(boolean enabled) {
        String command = enabled ? "{n: pointing, v: 0x01}" : "{n: pointing, v: 0x00}";
        std_msgs.String message = mHerkulexInputPublisher.newMessage();
        message.setData(command);
        mHerkulexInputPublisher.publish(message);
    }

    void centerHead() {
        String command = "{n: center, a: 0xFE}";
        std_msgs.String message = mHerkulexInputPublisher.newMessage();
        message.setData(command);
        mHerkulexInputPublisher.publish(message);
    }
}
