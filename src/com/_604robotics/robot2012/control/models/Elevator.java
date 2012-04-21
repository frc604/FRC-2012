package com._604robotics.robot2012.control.models;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Elevator {
    public static boolean recalibrating = false;
    public static boolean high = false;
    public static boolean disabled = false;
    
    public static void recalibrate (boolean recalibrating) {
        Elevator.recalibrating = recalibrating;
    }
    
    public static void recalibrate () {
        Elevator.recalibrate(true);
    }
    
    public static void go (boolean high) {
        Elevator.high = high;
    }
    
    public static void goUp () {
        Elevator.go(true);
    }
    
    public static void goDown () {
        Elevator.go(false);
    }
    
    public static void toggleDisabled (boolean really) {
        if (really)
            Elevator.disabled = !Elevator.disabled;
    }
    
    public static void toggleDisabled () {
        Elevator.toggleDisabled(true);
    }
}
