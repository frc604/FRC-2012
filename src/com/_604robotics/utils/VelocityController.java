package com._604robotics.utils;

import edu.wpi.first.wpilibj.*;

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
    private final Encoder encoderLeft;
    private final Encoder encoderRight;
    private final EncoderWrapper encoderWrapperLeft;
    private final EncoderWrapper encoderWrapperRight;
    private final RobotDrive robotDrive;
    private final PIDController controllerLeft;
    private final PIDController controllerRight;
    private final PIDOutput outputLeft;
    private final PIDOutput outputRight;
    private final Gyro gyro;
    
    private double P, I, D;
    private double pAngleGain, iAngleGain, dAngleGain; // gains based on balane gyro values
    
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
    
    private class DriveWrapper implements PIDOutput {
        public void pidWrite(double value) {
            
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
    public VelocityController (double p, double i, double d, Encoder encoderLeft, Encoder encoderRight, RobotDrive robotDrive, Gyro gyro) {
        this.encoderLeft = encoderLeft;
        this.encoderRight = encoderRight;
        this.robotDrive = robotDrive;
        this.outputLeft = null;
        this.outputRight = null;
        this.controllerLeft = new PIDController(p, i, d, encoderWrapperLeft = new EncoderWrapper(encoderLeft), null);
        this.controllerRight = new PIDController(p, i, d, encoderWrapperRight = new EncoderWrapper(encoderRight), null);
        this.gyro = gyro;
    }
    
    /**
     * Gets the current target velocity.
     * 
     * @return  The current target velocity.
     */
    public double getVelocity () {
        return this.controllerRight.getSetpoint(); // TODO - fix
    }
    
    /**
     * Gets the actual, current velocity.
     * 
     * @return  The actual, current velocity.
     */
    public double getActualVelocity () {
        return this.encoderLeft.getRate(); // TODO - fix
    }
    
    /**
     * Sets the target velocity.
     * 
     * @param   velocity    The target velocity to set.
     */
    public void setVelocity (double velocity) {
        this.controllerLeft.setSetpoint(velocity); // TODO - fix
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
    
    /**
     * Based on gyro angles
     * 
     * TODO - javadoc
     * 
     * @param   p   The
     * @param   i   The
     * @param   d   The
     */
    public void setAngleGains(double pAngle, double iAngle, double dAngle) {
        pAngleGain = pAngle;
        iAngleGain = iAngle;
        dAngleGain = dAngle;
        
        updateGains();
    }
    
    private void updateGains() {
        double absAngle = Math.abs(gyro.getAngle());
        
        this.controllerLeft.setPID(P*(1+absAngle*pAngleGain),
                                I*(1+absAngle*iAngleGain),
                                D*(1+absAngle*dAngleGain));
        this.controllerRight.setPID(P*(1+absAngle*pAngleGain),
                                I*(1+absAngle*iAngleGain),
                                D*(1+absAngle*dAngleGain));
    }
        
    /**
     * Enables the VelocityController.
     */
    public void enable () {
        this.controllerLeft.enable();
        this.controllerRight.enable();
    }
    
    /**
     * Disables the VelocityController.
     */
    public void disable () {
        this.controllerRight.disable();{
        this.controllerRight.disable();
    }
    
    /**
     * Is the VelocityController currently enabled?
     * 
     * @return  Whether or not the VelocityController is currently enabled.
     */
    public boolean isEnabled () {
        return this.controllerLeft.isEnable();
    }
}