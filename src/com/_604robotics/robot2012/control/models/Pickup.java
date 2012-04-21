package com._604robotics.robot2012.control.models;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Pickup {
    public static boolean up = true;
    public static boolean sucking = false;
    public static double speed = 0D;
    
    public static void toggleFlip (boolean really) {
        if (really)
            Pickup.up = !Pickup.up;
    }
    
    public static void toggleFlip () {
        Pickup.toggleFlip(true);
    }
    
    public static void flip (boolean up) {
        Pickup.up = up;
    }
    
    public static void flipUp () {
        Pickup.flip(true);
    }
    
    public static void flipDown () {
        Pickup.flip(false);
    }
    
    public static void suckIn (boolean sucking) {
        Pickup.sucking = sucking;
        if (sucking)
            Pickup.speed = 0D;
    }
    
    public static void suckIn () {
        Pickup.suckIn(true);
    }
    
    public static void setSpeed (double speed) {
        Pickup.speed = speed;
    }
}
