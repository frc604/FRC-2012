package com._604robotics.robot2012.configuration;

public interface FiringConfiguration {
    public static final double FENDER_DISTANCE_THRESHOLD = 0D;
        // TODO: Configure this.
    
    public static final double SHOOTER_SLOPE = 0D;
        // TODO: Configure this.
    
    public static final double TOP_HOOP_HEIGHT = 0D;
        // TODO: Configure this.
    public static final double SHOOTER_HEIGHT = 0D;
        // TODO: Configure this.
    
    public static final boolean USING_SPEEDS = true;
    
    public static final double FENDER_FIRING_POWER = -0.32;
        // TODO: Configure this.
    public static final double KEY_FIRING_POWER = -1D;
        // TODO: Configure this.
    
    public static final double FENDER_FIRING_SPEED = 125D;
        // TODO: Configure this.
    public static final double KEY_FIRING_SPEED = 375D; // was 420D
        // TODO: Configure this.
    
    public static final boolean USE_HOPPER_THRESHOLD = true;
        // TODO: See what works best.
    public static final int CHARGE_TIME = 3000;
        // TODO: Configure this.
    public static final double SPEED_TOLERANCE = 15D;
        // TODO: Configure this.
    
    public static final boolean TELEOP_AUTO_HOPPER = false;
        // TODO: Figure out whether or not to use this.
    
    
    public static final double MAX_SPEED = 575;
}