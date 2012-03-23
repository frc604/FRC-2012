package com._604robotics.robot2012.configuration;

/**
 * Sensor configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface SensorConfiguration {
    public static final double GYRO_DRIFT = 0.0238095238;
    
    public static final int TURRET_CALIBRATION_OFFSET = -614;
    
    public interface Encoders {
        public static final double TURRET_DEGREES_PER_CLICK = 0.172801106;
    }
}