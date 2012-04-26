package com._604robotics.robot2012.control.models;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Stinger {
    public static boolean down = false;
    
    public static void put (boolean down) {
        Stinger.down = down;
    }
    
    public static void putDown () {
        Stinger.put(true);
    }
    
    public static void putUp () {
        Stinger.put(false);
    }
}
