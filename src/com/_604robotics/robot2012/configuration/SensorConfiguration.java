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
    
    public static final int ELEVATOR_UPPER_POSITION = 1550;
        // TODO: Check calibration.
    public static final int ELEVATOR_HOVER_POSITION = 822;
        // TODO: Check calibration.
}