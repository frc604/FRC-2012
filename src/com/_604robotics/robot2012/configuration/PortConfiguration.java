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
    
    public interface Motors {
        public static final int LEFT_DRIVE = 1;
        public static final int RIGHT_DRIVE = 9;
        
        public static final int ELEVATOR_LEFT = 7;
        public static final int ELEVATOR_RIGHT = 8;
        
        public static final int SHOOTER_LEFT = 2;
        public static final int SHOOTER_RIGHT = 3;
        public static final int HOPPER = 4;
        public static final int PICKUP = 6;
        
        public static final int TURRET_ROTATION = 5;
    }
    
    public interface Relays {
        public static final int RING_LIGHT_PORT = 4;
        public static final Direction RING_LIGHT_DIRECTION = Direction.kForward;
    }
    
    public interface Sensors {
        public static final int GYRO_HEADING = 2;
        public static final int GYRO_BALANCE = 1;
        public static final int ACCELEROMETER = 3;
        
        public static final int ELEVATOR_LIMIT_SWITCH = 1;
    }
    
    public interface Encoders {
        public interface Drive {
            public static final int LEFT_A = 13;
            public static final int LEFT_B = 14;
            
            public static final int RIGHT_A = 12;
            public static final int RIGHT_B = 11;
        }
        
        public static final int ELEVATOR_A = 8;
        public static final int ELEVATOR_B = 9;
        
        public static final int TURRET_ROTATION_A = 7;
        public static final int TURRET_ROTATION_B = 6;
    }
    
    public interface Pneumatics {
        public static final int COMPRESSOR = 6;
        public static final int PRESSURE_SWITCH = 5;

        public interface SHIFTER_SOLENOID {
            public static final int LOW_GEAR = 7; // FORWARD
            public static final int HIGH_GEAR = 8; // REVERSE
        }
        
        public interface SHOOTER_SOLENOID {
            public static final int LOWER_ANGLE = 6; // FORWARD
            public static final int UPPER_ANGLE = 5; // REVERSE
        }
        
        public interface PICKUP_SOLENOID {
            public static final int IN = 2; // FORWARD
            public static final int OUT = 1; // REVERSE
        }
        
        public interface HOPPER_SOLENOID {
            public static final int FORWARD = 4; // REGULAR
            public static final int REVERSE = 3; // PUSH
        }
    }
}