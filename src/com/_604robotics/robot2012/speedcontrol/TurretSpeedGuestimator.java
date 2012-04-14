package com._604robotics.robot2012.speedcontrol;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class TurretSpeedGuestimator {
    
    //TODO - calibrate
    static final double
            constantFactor = .0002,
            linearFactor = .0028,
            quadFactor = -1e-5,
            cubicFactor = 2e-8,
            
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
