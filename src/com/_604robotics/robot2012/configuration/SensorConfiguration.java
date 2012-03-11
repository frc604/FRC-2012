package com._604robotics.robot2012.configuration;

/**
 * Sensor configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface SensorConfiguration {
    public static final double GYRO_DRIFT = 0.0238095238;
        // TODO: Configure this.
    
    public static final double ACCELEROMETER_SENSITIVITY = 1;
        // TODO: Configure this.
    public static final double ACCELEROMETER_UPPER_RADIANS = 0.7854;
        // TODO: Configure this.
    
    public static final int TURRET_CALIBRATION_OFFSET = 0;
        // TODO: Configure this.
    
    public interface Encoders {
        public static final double TURRET_DEGREES_PER_CLICK = 0.172801106;
            // TODO: Make sure this is correct.x
        public static final double LEFT_DRIVE_INCHES_PER_CLICK = 1D;
            // TODO: Configure this.
        public static final double RIGHT_DRIVE_INCHES_PER_CLICK = 1D;
            // TODO: Configure this.
    }
}