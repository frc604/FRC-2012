package com._604robotics.robot2012.speedcontrol;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 * @author Michael Smith <mdsmtp@gmail.com>
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
    
    
    /**
     * 
     * @param tolerance How close the current speed must be to the setPoint
     * @return  if it's within the tolerance of target speed
     */
    public boolean isOnTarget(double tolerance);
    
    /**
     * Applies the calculated power to the motor.
     */
    public void apply();
    
    /**
     * Disables/resets the SpeedProvider.
     */
    public void reset();
}
