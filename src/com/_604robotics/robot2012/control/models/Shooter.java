package com._604robotics.robot2012.control.models;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Shooter {
    public static boolean shooting = false;
    public static boolean fender = true;
    public static boolean hoodUp = false;
    public static double hopperPower = 0D;
    
    public static void setAtFender () {
        Shooter.fender = true;
    }
    
    public static void setAtKey () {
        Shooter.fender = false;
    }
    
    public static void shoot (boolean shooting) {
        Shooter.shooting = shooting;
    }
    
    public static void shoot () {
        Shooter.shoot(true);
    }
    
    public static void setHood (boolean hoodUp) {
        Shooter.hoodUp = hoodUp;
    }
    
    public static void hoodUp () {
        Shooter.setHood(true);
    }
    
    public static void hoodDown () {
        Shooter.setHood(false);
    }
    
    public static void toggleHood (boolean really) {
        if (really)
            Shooter.hoodUp = !Shooter.hoodUp;
    }
    
    public static void toggleHood () {
        Shooter.toggleHood(true);
    }
    
    public static void driveHopper (double hopperPower) {
        Shooter.hopperPower = hopperPower;
    }
}
