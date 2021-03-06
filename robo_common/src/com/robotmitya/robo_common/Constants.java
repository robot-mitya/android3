package com.robotmitya.robo_common;

/**
 *
 * Created by dmitrydzz on 29.03.18.
 */

public class Constants {
    public static final String TAG = "RobotMitya";

    public class NodeName {
        public static final String Controller = "robot_mitya/controller_node";
        public static final String Face = "robot_mitya/face_node";
        public static final String Eye = "robot_mitya/eye_node";
    }

    public class TopicName {
        public static final String ArduinoInput = "robot_mitya/arduino_input";
        public static final String HerkulexInput = "robot_mitya/herkulex_input";
        public static final String DriveTowards = "robot_mitya/drive_towards";
        public static final String ControllerImu = "robot_mitya/controller_imu";
        public static final String Face = "robot_mitya/face";
        public static final String Camera = "/camera/image/compressed";
    }

    public final class Camera {
        public static final int Disabled = 0;
        public static final int Front = 1;
        public static final int Back = 2;
    }
}
