package com.robotmitya.robo_controller;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

/**
 *
 * Created by dmitrydzz on 29.03.18.
 */

public class ArduinoInputPublisher extends AbstractNodeMain {

    private String mTopicName;
    private Publisher<std_msgs.String> mPublisher;

    ArduinoInputPublisher() {
        mTopicName = Constants.TopicName.ArduinoInput;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of(Constants.NodeName.Controller);
    }

    public void onStart(ConnectedNode connectedNode) {
        mPublisher = connectedNode.newPublisher(mTopicName, "std_msgs/String");
//        connectedNode.executeCancellableLoop(new CancellableLoop() {
//            protected void setup() {
//            }
//
//            protected void loop() throws InterruptedException {
//                Thread.sleep(20L);
//            }
//        });
    }

    void switchLed1() {
        std_msgs.String str = mPublisher.newMessage();
        str.setData("L1 -1;");
        mPublisher.publish(str);
    }

    void switchLed2() {
        std_msgs.String str = mPublisher.newMessage();
        str.setData("L2 -1;");
        mPublisher.publish(str);
    }
}
