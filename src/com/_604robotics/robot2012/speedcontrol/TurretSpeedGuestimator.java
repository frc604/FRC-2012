package com._604robotics.robot2012.speedcontrol;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class TurretSpeedGuestimator {
    
    //TODO - calibrate
    static final double
            constantFactor = 7.0000E-02,
            linearFactor = 1.8879E-04,
            quadFactor = 1.3801E-06,
            cubicFactor = 7.2550E-09,
            
            originalCalibratedBattery = 12;//or 10 or whatever the battery falls down to under load
    
    public static double guestimatePow(double speed) {
        return constantFactor
                + linearFactor*speed
                + quadFactor*speed*speed
                + cubicFactor*speed*speed*speed;
    }
    public static double guestimateSpeed(double speed, double battery) {
        return (guestimatePow(speed)) * (battery / originalCalibratedBattery);
    }
}
