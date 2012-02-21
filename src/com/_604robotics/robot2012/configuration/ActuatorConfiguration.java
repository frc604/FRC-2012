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
    public static final double PICKUP_POWER = 0.8;
    
    public static final double ELEVATOR_POWER_MIN = -0.8D;
    public static final double ELEVATOR_POWER_MAX = 0.8D;
    
    public static final double TURRET_ROTATION_POWER_MIN = -0.8D;
    public static final double TURRET_ROTATION_POWER_MAX = 0.8D;
    
    public interface RING_LIGHT {
        public static final Value ON = Value.kOn;
        public static final Value OFF = Value.kOff;
    }
    
    public interface SOLENOID_SHIFTER {
        public static final DoubleSolenoid.Value LOW_POWER  = DoubleSolenoid.Value.kForward;
        public static final DoubleSolenoid.Value HIGH_POWER = DoubleSolenoid.Value.kReverse;
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