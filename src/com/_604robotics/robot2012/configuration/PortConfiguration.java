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
        
        public static final int SHOOTER = 4;
        public static final int HOPPER = 5;
        public static final int PICKUP = 6;
        
        public static final int TURRET_ROTATION = 2;
    }
    
    public interface Relays {
        public static final int RING_LIGHT_PORT = 4;
        public static final Direction RING_LIGHT_DIRECTION = Direction.kForward;
    }
    
    public interface Sensors {
        public static final int GYRO_HEADING = 1;
        public static final int GYRO_BALANCE = 2;
        public static final int ACCELEROMETER = 3;
    }
    
    public interface Encoders {
        public interface Drive {
            public static final int LEFT_A = 10;
            public static final int LEFT_B = 11;
            
            public static final int RIGHT_A = 3;
            public static final int RIGHT_B = 4;
        }
        
        public static final int ELEVATOR_A = 1;
        public static final int ELEVATOR_B = 2;
        
        public static final int TURRET_ROTATION_A = 12;
        public static final int TURRET_ROTATION_B = 13;
    }
    
    public interface Pneumatics {
        public static final int COMPRESSOR = 6;
        public static final int PRESSURE_SWITCH = 5;

        public interface SHIFTER_SOLENOID {
            public static final int FORWARD = 8; // LOW GEAR
            public static final int REVERSE = 7; // HIGH GEAR
        }
        
        public interface SHOOTER_SOLENOID {
            public static final int FORWARD = 5; // LOWER ANGLE
            public static final int REVERSE = 6; // UPPER ANGLE
        }
        
        public interface PICKUP_SOLENOID {
            public static final int FORWARD = 2; // IN
            public static final int REVERSE = 1; // OUT
        }
        
        public interface HOPPER_SOLENOID {
            public static final int FORWARD = 3; // REGULAR
            public static final int REVERSE = 4; // PUSH
        }
    }
}