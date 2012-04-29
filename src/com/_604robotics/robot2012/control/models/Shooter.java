package com._604robotics.robot2012.control.models;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Shooter {
    public static boolean shooting = false;
    public static boolean fender = true;
    public static boolean hoodUp = true;
    
    public static double hopperPower = 0D;
    public static long lastCharged = 0;
    
    public static boolean manual = false;
    public static double manualSpeed = 0D;
    
    public static boolean vision = false;
    
    public static boolean fullPower = false;
    
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
    
    public static void driveHopper (boolean really) {
        if (really)
            Shooter.driveHopper(ActuatorConfiguration.HOPPER_POWER);
        else
            Shooter.hopperPower = 0D;
    }
    
    public static void driveHopper () {
        Shooter.driveHopper(true);
    }
    
    public static boolean isCharged () {
        return Shooter.shooting && System.currentTimeMillis() - Shooter.lastCharged < 50;
    }
    
    public static void setCharged (boolean really) {
        if (really)
            Shooter.lastCharged = System.currentTimeMillis();
        else
            Shooter.lastCharged = 0;
    }
    
    public static void setCharged () {
        Shooter.setCharged(true);
    }
    
    public static void setManual (boolean manual) {
        Shooter.manual = manual;
        if (!manual)
            Shooter.manualSpeed = 0D;
    }
    
    public static void setManualSpeed (double manualSpeed) {
        Shooter.manualSpeed = manualSpeed;
    }
    
    public static void setVisionEnabled (boolean vision) {
        Shooter.vision = vision;
    }
    
    public static void setFullPower (boolean fullPower) {
        Shooter.fullPower = fullPower;
    }
}
