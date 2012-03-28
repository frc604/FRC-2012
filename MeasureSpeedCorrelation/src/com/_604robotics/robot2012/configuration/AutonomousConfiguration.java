package com._604robotics.robot2012.configuration;

/**
 * Autonomous mode configuration.
 * 
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface AutonomousConfiguration {
    public static final int MAX_STEP = 0;
    
    public static final double STEP_1_FORWARD_TIME = 1D;
    public static final double STEP_2_WAIT_TIME = 1D;
    public static final double STEP_3_BACKWARD_TIME = 1D;
    public static final double STEP_4_TURN_TIME = 1D;
    public static final double STEP_5_FORWARD_TIME = 1D;
    public static final double STEP_5_FORWARD_TIME_SIDES = 1D;
}