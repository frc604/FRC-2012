package com._604robotics.robot2012.control.models;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Drive {
    public static boolean shifted = false;
    public static boolean slowed = false;
    
    public static double leftPower = 0D;
    public static double rightPower = 0D;
    
    public static void shift (boolean shifted) {
        Drive.shifted = shifted;
    }
    
    public static void setSlow (boolean slowed) {
        Drive.slowed = slowed;
    }
    
    public static void drive (double leftPower, double rightPower, boolean inverted) {
        Drive.leftPower = leftPower * ((inverted) ? -1 : 1);
        Drive.rightPower = rightPower * ((inverted) ? -1 : 1);
    }
    
    public static void drive (double leftPower, double rightPower) {
        Drive.drive(leftPower, rightPower, false);
    }
    
    public static void drive (double power) {
        Drive.drive(power, power, false);
    }
    
    public static void bump (int polarity) {
        Drive.drive(ActuatorConfiguration.DRIVE_BUMP_SPEED * polarity);
    }
}
