package com._604robotics.robot2012.configuration;

/**
 * Autonomous mode configuration.
 * 
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface AutonomousConfiguration {
    public static final double FORWARD_DISTANCE = 1000D;
        // TODO: Calibrate this.
    public static final double BACKWARD_DISTANCE = -1000D;
        // TODO: Calibrate this.
    public static final double BACKWARD_DISTANCE_SIDES = -500D;
        // TODO: Calibrate this.
    
    public static final double FORWARD_DRIVE_POWER = 0.5;
    public static final double BACKWARD_DRIVE_POWER = -0.5;
}
