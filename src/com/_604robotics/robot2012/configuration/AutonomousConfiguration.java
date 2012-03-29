package com._604robotics.robot2012.configuration;

/**
 * Autonomous mode configuration.
 * 
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface AutonomousConfiguration {
    public static final int MAX_STEP = 2;
    
    public static final double STEP_2_SHOOT_TIME = 6D;
    public static final double STEP_3_TURN_TIME = 1D;
    public static final double STEP_4_DRIVE_TIME = 1D;
    public static final double STEP_5_WAIT_TIME = 1D;
}