package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Victor;

/**
 * Control two Victors like they're one.
 * 
 * Useful for PID controllers. Also, it's springable (see SpringableVictor).
 */
public class DualVictor implements PIDOutput {
    private boolean sprung = false;
    private PIDController controller = null;
    
    private final Victor leftVictor;
    private final Victor rightVictor;
    
    private boolean leftInversion = false;
    private boolean rightInversion = false;
    
    private double lowerDeadband = 0D;
    private double upperDeadband = 0D;
    
    /**
     * Initialize a DualVictor with a left and a right PWM port.
     * 
     * @param   leftPort    The PWM port of the "left" Victor.
     * @param   rightPort   The PWM port of the "right" Victor.
     */
    public DualVictor (int leftPort, int rightPort) {
        this.leftVictor = new Victor(leftPort);
        this.rightVictor = new Victor(rightPort);
    }
    
    /**
     * Initializes a DualVictor with left and right slot and PWM port.
     * 
     * @param   leftSlot    The slot the "left" Victor is plugged into.
     * @param   leftPort    The PWM port of the "left" Victor.
     * @param   rightSlot   The slot the "right" Victor is plugged into.
     * @param   rightPort   The PWM port of the "right" Victor.
     */
    public DualVictor (int leftSlot, int leftPort, int rightSlot, int rightPort) {
        this.leftVictor = new Victor(leftSlot, leftPort);
        this.rightVictor = new Victor(rightSlot, rightSlot);
    }
    
    /**
     * Initializes a DualVictor with left and right slot and PWM port.
     * 
     * @param   leftVictor      The "left" Victor.
     * @param   rightVictor     The "right" Victor.
     */
    public DualVictor (Victor leftVictor, Victor rightVictor) {
        this.leftVictor = leftVictor;
        this.rightVictor = rightVictor;
    }
    
    /**
     * Has the victor been sprung?
     * 
     * @return  Whether or not the victor has been sprung.
     */
    public boolean getSprung () {
        return this.sprung;
    }
    
    /**
     * Springs the victor.
     */
    public void spring () {
        this.sprung = true;
    }
    
    /**
     * Sets the inversion for the "left" Victor.
     * 
     * @param   inversion   Is it inverted?
     */
    public void setLeftInversion (boolean inversion) {
        this.leftInversion = inversion;
    }
    
    /**
     * Sets the inversion for the "right" Victor.
     * 
     * @param   inversion   Is it inverted?
     */
    public void setRightInversion (boolean inversion) {
        this.rightInversion = inversion;
    }
    
    /**
     * Checks the current power the Victors are set to.
     * 
     * @return  The current power the Victors are set to.
     */
    public double get () {
        return this.leftVictor.get();
    }
    
    /** 
     * Sets the power of the Victors.
     * 
     * @param   speed   The speed to set.
     */
    public void set (double speed) {
        if (speed > this.lowerDeadband && speed < this.upperDeadband)
            speed = 0D;
        
        this.leftVictor.set((this.leftInversion) ? speed * -1 : speed);
        this.rightVictor.set((this.rightInversion) ? speed * -1 : speed);
        
        System.out.println(speed);
        
        this.spring();
    }
    
    /**
     * Function to hook into the PIDController.
     * 
     * Sets the power of the Victors.
     * 
     * @param   output  The speed to set.
     */
    public void pidWrite (double output) {
        this.set(output);
    }
    
    /**
     * Sets the deadband for the DualVictor.
     * 
     * The default is no deadband.
     * 
     * @param   lowerDeadband       The lower bound of the deadband.
     * @param   upperDeadband       The upper bound of the deadband.
     */
    public void setDeadband(double lowerDeadband, double upperDeadband) {
        this.lowerDeadband = lowerDeadband;
        this.upperDeadband = upperDeadband;
    }
    
    /**
     * Sets whether or not safety is enabled.
     * 
     * @param   enabled     Whether or not safety is enabled.
     */
    public void setSafetyEnabled (boolean enabled) {
        this.leftVictor.setSafetyEnabled(enabled);
        this.rightVictor.setSafetyEnabled(enabled);
    }
    
    /**
     * If the Victor has been sprung, unspring it; if not, set the output to 0.
     */
    public void reload () {
        if (!this.sprung && (this.controller == null || !this.controller.isEnable())) {
            this.leftVictor.set(0D);
            this.rightVictor.set(0D);
        }
        
        this.sprung = false;
    }
    
    /**
     * Sets the PIDController for this DualVictor, if there is one.
     * 
     * If the PIDController is enabled, reload will assume it's updating it, and
     * won't reset the output to 0.
     * 
     * @param   controller  The PIDController for this DualVictor. 
     */
    public void setController (PIDController controller) {
        this.controller = controller;
    }
}