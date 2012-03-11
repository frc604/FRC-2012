package com._604robotics.utils;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * Class for controlling a motor's velocity, rather than its power directly.
 * 
 * Uses a PID loop to scale to said velocity, and a distance-calibrated encoder
 * for feedback.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class VelocityController {
    private final Encoder encoder;
    private final EncoderWrapper encoderWrapper;
    private final PIDOutput output;
    private final PIDController controller;
    
    private double P, I, D;
    private double pAngleGain, iAngleGain, dAngleGain; // gains based on balane gyro values
    private double angle;
    
    /**
     * Internal class that wraps around an Encoder object, implementing a
     * PIDSource that returns the value of its getRate().
     * 
     * We could just use setPIDSourceParameter(), but that risks messing up any
     * setting the user has configured for this, outside of this class.
     */
    private class EncoderWrapper implements PIDSource {
        private final Encoder encoder;
        
        public EncoderWrapper (Encoder encoder) {
            this.encoder = encoder;
        }
        
        public double pidGet () {
            return this.encoder.getRate();
        }
    }
    
    /**
     * Initializes a new VelocityController.
     * 
     * @param   p           The proportional term for the PIDController.
     * @param   i           The integral term for the PIDController.
     * @param   d           The derivative term for the PIDController.
     * @param   encoder     The encoder to use for feedback.
     * @param   output      The PIDOutput to control. Usually some sort of
     *                      motor.
     */
    public VelocityController (double p, double i, double d, Encoder encoder, PIDOutput output) {
        this.encoder = encoder;
        this.output = output;
        this.controller = new PIDController(p, i, d, encoderWrapper = new EncoderWrapper(encoder), output);
    }
    
    /**
     * Gets the current target velocity.
     * 
     * @return  The current target velocity.
     */
    public double getVelocity () {
        return this.controller.getSetpoint();
    }
    
    /**
     * Gets the actual, current velocity.
     * 
     * @return  The actual, current velocity.
     */
    public double getActualVelocity () {
        return this.encoder.getRate();
    }
    
    /**
     * Sets the target velocity.
     * 
     * @param   velocity    The target velocity to set.
     */
    public void setVelocity (double velocity) {
        this.controller.setSetpoint(velocity);
    }
    
    /**
     * Reconfigures the gains on the PIDController.
     * 
     * @param   p   The proportional term for the PIDController.
     * @param   i   The integral term for the PIDController.
     * @param   d   The derivative term for the PIDController.
     */
    public void setGains(double p, double i, double d) {
        P = p;
        I = i;
        D = d;
        
        updateGains();
    }
    
    private void updateGains() {
        double absAngle = Math.abs(angle);
        
        this.controller.setPID(P*(1+absAngle*pAngleGain),
                                I*(1+absAngle*iAngleGain),
                                D*(1+absAngle*dAngleGain));
    }
    /**
     * Sets the angle it is balancing at(?). Kevin has to look at this.
     * @param balAngle 
     */
    public void setBalanceAngle(double balAngle) {
        angle = balAngle;
    }
    
    /**
     * Enables the VelocityController.
     */
    public void enable () {
        this.controller.enable();
    }
    
    /**
     * Disables the VelocityController.
     */
    public void disable () {
        this.controller.disable();
    }
    
    /**
     * Is the VelocityController currently enabled?
     * 
     * @return  Whether or not the VelocityController is currently enabled.
     */
    public boolean isEnabled () {
        return this.controller.isEnable();
    }
}