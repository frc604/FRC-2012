package com._604robotics.robot2012.configuration;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay.Value;

/**
 * Actuator polarity and power configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface ActuatorConfiguration {
    public static final double HOPPER_POWER = 0.8;
    public static final double HOPPER_POWER_REVERSE = -0.5;
    public static final double PICKUP_POWER = -0.8;
    
    public static final double ELEVATOR_POWER_MIN = -0.8;
    public static final double ELEVATOR_POWER_MAX = 0.8;
    
    public static final double MAX_SLOW_SPEED = 0.7;
    
    public static final double DRIVE_BUMP_SPEED = 0.6;
    
    public static final double ELEVATOR_PICKUP_POWER = -0.32;
    
    public interface RING_LIGHT {
        public static final Value ON = Value.kOn;
        public static final Value OFF = Value.kOff;
    }
    
    public interface ELEVATOR {
        public static final int HIGH = 1520; // was 1094;
        public static final int MEDIUM = 663;
        public static final int LOW = 0;
        
        public static final int OKAY_TO_TURN = 1300;
        
        public interface TOLERANCE {
            public static final int HIGH = 1420; // was 1080;
            public static final int MEDIUM_UPPER = 680; // +8
            public static final int MEDIUM_LOWER = 625; // -32
            public static final int LOW = 25;
        }
        
        public interface DEADBAND {
            public static final int HIGH = 1070; // was 1400
            public static final int MEDIUM_UPPER = 710; // +28
            public static final int MEDIUM_LOWER = 600; // -52
            public static final int LOW = 64;
        }
    }
    
    public interface SOLENOID_SHIFTER {
        public static final DoubleSolenoid.Value LOW_GEAR  = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value HIGH_GEAR = DoubleSolenoid.Value.kReverse;
    }
    
    public interface SOLENOID_SHOOTER {
        public static final DoubleSolenoid.Value LOWER_ANGLE = DoubleSolenoid.Value.kReverse;
        public static final DoubleSolenoid.Value UPPER_ANGLE  = DoubleSolenoid.Value.kForward;
    }
    
    public interface SOLENOID_PICKUP {
        public static final DoubleSolenoid.Value IN = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value OUT  = DoubleSolenoid.Value.kReverse;
    }
    
    public interface SOLENOID_STINGER {
        public static final DoubleSolenoid.Value UP = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value DOWN  = DoubleSolenoid.Value.kReverse;
    }
}