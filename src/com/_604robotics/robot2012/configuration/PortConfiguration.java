package com._604robotics.robot2012.configuration;

import edu.wpi.first.wpilibj.Relay.Direction;

/**
 * Port configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface PortConfiguration {
    public interface Controllers {
        public static final int DRIVE = 1;
        public static final int MANIPULATOR = 2;
    }
    
    public interface Kinect {
        public static final int LEFT = 1;
        public static final int RIGHT = 2;
    }
    
    public interface Motors {
        public static final int LEFT_DRIVE = 1;
        public static final int RIGHT_DRIVE = 9;
        
        public static final int ELEVATOR_LEFT = 7;
        public static final int ELEVATOR_RIGHT = 8;
        
        public static final int SHOOTER_LEFT = 2;
        public static final int SHOOTER_RIGHT = 3;
        public static final int HOPPER = 6;
        public static final int PICKUP = 4;
    }
    
    public interface Relays {
        public static final int RING_LIGHT_PORT = 4;
        public static final Direction RING_LIGHT_DIRECTION = Direction.kForward;
    }
    
    public interface Sensors {
        public static final int GYRO_HEADING = 2;
        
        public static final int ELEVATOR_LIMIT_SWITCH = 1;
    }
    
    public interface Encoders {
        public static final int SHOOTER_A = 3;
        public static final int SHOOTER_B = 4;
        
        public static final int ELEVATOR_A = 12;
        public static final int ELEVATOR_B = 11 ;
    }
    
    public interface Pneumatics {
        public static final int COMPRESSOR = 6;
        public static final int PRESSURE_SWITCH = 5;

        public interface SHIFTER_SOLENOID {
            public static final int LOW_GEAR = 8; // FORWARD
            public static final int HIGH_GEAR = 7; // REVERSE
        }
        
        public interface SHOOTER_SOLENOID {
            public static final int LOWER_ANGLE = 5; // FORWARD
            public static final int UPPER_ANGLE = 6; // REVERSE
        }
        
        public interface PICKUP_SOLENOID {
            public static final int IN = 4; // FORWARD
            public static final int OUT = 3; // REVERSE
        }
        
        public interface HOPPER_SOLENOID {
            public static final int FORWARD = 2; // REGULAR
            public static final int REVERSE = 1; // PUSH
        }
    }
}