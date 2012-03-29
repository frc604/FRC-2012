/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com._604robotics.robot2012.speedcontrol;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class TurretSpeedGuestimator {
    
    //TODO - calibrate
    static final double
            constantFactor = 0,
            linearFactor = 0,
            
            calBattery = 12;//or 10 or whatever the battery falls down to under load
    
    public static double guestimatePow(double speed) {
        return constantFactor + linearFactor*speed;
    }
    public static double guestimateSpeed(double speed, double battery) {
        return (guestimatePow(speed)) * (battery / calBattery);
    }
}
