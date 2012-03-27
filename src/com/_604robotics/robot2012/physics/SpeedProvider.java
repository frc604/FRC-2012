/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012.physics;

/**
 *
 * @author kevin
 */
public interface SpeedProvider {
    
    /**
     * Calculates the power to set the motor to
     * @return  the power for the motor
     */
    public double getMotorPower();
    
    /**
     * Sets the target speed to the given value
     * @param setSpeed  the value to set the target speed to
     */
    public void setSetSpeed(double setSpeed);
    
    /**
     * Returns the target speed
     * @return  the target speed
     */
    public double getSetSpeed();
}
