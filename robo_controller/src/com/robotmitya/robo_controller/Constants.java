package com.robotmitya.robo_controller;

/**
 *
 * Created by dmitrydzz on 29.03.18.
 */

class Constants {
    static final String TAG = "RobotMitya";

    class NodeName {
        static final String Controller = "robot_mitya/controller";
    }

    class TopicName {
        static final String ArduinoInput = "robot_mitya/arduino_input";
        static final String HerkulexInput = "robot_mitya/herkulex_input";
        static final String ControllerImu = "robot_mitya/controller_imu";
    }
}
