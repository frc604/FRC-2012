package com._604robotics.robot2012.physics;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class GuestimatedSpeedProvider implements SpeedProvider {
    
    private double setSpeed;
    private double linearFactor;
    
    public double getLinearFactor() {
        return linearFactor;
    }
    
    public void setLinearFactor(double linFac) {
        linearFactor = linFac;
    }

    public double getMotorPower() {
        return setSpeed*linearFactor;
    }
    
    public void setSetSpeed(double setSpeed) {
        this.setSpeed = setSpeed;
    }
    
    public double getSetSpeed() {
        return setSpeed;
    }
}
