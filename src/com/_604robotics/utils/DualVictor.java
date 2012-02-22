package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Victor;

/**
 * Control two Victors like they're one.
 * 
 * Useful for PID controllers.
 */
public class DualVictor implements PIDOutput {
    private boolean sprung = false;
    
    private final Victor leftVictor;
    private final Victor rightVictor;
    
    private boolean leftInversion = false;
    private boolean rightInversion = false;
    
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
    
    public boolean getSprung () {
        return this.sprung;
    }
    
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
        this.leftVictor.set((this.leftInversion) ? speed * -1 : speed);
        this.rightVictor.set((this.rightInversion) ? speed * -1 : speed);
        
        this.spring();
    }
    
    /* 
     * Function to hook into the PIDController.
     * 
     * Sets the power of the Victors.
     * 
     * @param   output  The speed to set.
     */
    public void pidWrite (double output) {
        this.set(output);
    }
    
    public void setSafetyEnabled (boolean enabled) {
        this.leftVictor.setSafetyEnabled(enabled);
        this.rightVictor.setSafetyEnabled(enabled);
    }
    
    public void reload () {
        if (!this.sprung) {
            this.leftVictor.set(0D);
            this.rightVictor.set(0D);
        }
        
        this.sprung = false;
    }
}