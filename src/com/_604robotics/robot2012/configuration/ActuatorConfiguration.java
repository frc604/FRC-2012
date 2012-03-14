/**
 * Actuator polarity and power configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
package com._604robotics.robot2012.configuration;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Relay.Value;

public interface ActuatorConfiguration {
    public static final double ACCELEROMETER_DRIVE_POWER = 0.5;
        // TODO: Configure this.
    
    public static final double HOPPER_POWER = 0.8;
    public static final double HOPPER_POWER_REVERSE = -0.5;
    public static final double PICKUP_POWER = 0.8;
    
    public static final double ELEVATOR_POWER_MIN = -0.8;
    public static final double ELEVATOR_POWER_MAX = 0.8;
    
    public static final double TURRET_ROTATION_POWER_MIN = -0.4;
    public static final double TURRET_ROTATION_POWER_MAX = 0.4;
    
    public interface TURRET_POSITION {
        public static final double FORWARD = 0D;
        public static final double LEFT = -45D;
        public static final double RIGHT = 45D;
        
        public static final double TOLERANCE = 1D;
    }
    
    public interface RING_LIGHT {
        public static final Value ON = Value.kOn;
        public static final Value OFF = Value.kOff;
    }
    
    public interface ELEVATOR {
        public static final int HIGH = 1540;
        public static final int MEDIUM = 663;
        public static final int LOW = 0;
        
        public static final int OKAY_TO_TURN = 1300;
        
        public interface TOLERANCE {
            public static final int HIGH = 1505;
            public static final int MEDIUM_UPPER = 671; // +8
            public static final int MEDIUM_LOWER = 631; // -32
            public static final int LOW = 25;
        }
        
        public interface DEADBAND {
            public static final int HIGH = 1490;
            public static final int MEDIUM_UPPER = 691; // +28
            public static final int MEDIUM_LOWER = 611; // -52
            public static final int LOW = 35;
        }
    }
    
    public interface SOLENOID_SHIFTER {
        public static final DoubleSolenoid.Value LOW_GEAR  = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value HIGH_GEAR = DoubleSolenoid.Value.kReverse;
    }
    
    public interface SOLENOID_SHOOTER {
        public static final DoubleSolenoid.Value LOWER_ANGLE = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value UPPER_ANGLE  = DoubleSolenoid.Value.kReverse;
    }
    
    public interface SOLENOID_PICKUP {
        public static final DoubleSolenoid.Value IN = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value OUT  = DoubleSolenoid.Value.kReverse;
    }
    
    public interface SOLENOID_HOPPER {
        public static final DoubleSolenoid.Value REGULAR = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value PUSH  = DoubleSolenoid.Value.kReverse;
    }
}