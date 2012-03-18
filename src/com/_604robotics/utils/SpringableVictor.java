package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Victor;

/**
 * Extender of a Victor providing an easier control flow.
 * 
 * When an output is set for the Victor, it is considered "sprung". When the
 * "reload" method is called, if the victor is sprung, it unsprings the Victor.
 * If the Victor is not sprung, then the output is set to zero. In this way,
 * the Victor will only be moving when you tell it to. Use this in a loop or
 * something, and call "reload" at the end. No more worries about code paths
 * that don't update the Victors!
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class SpringableVictor extends Victor {
    private boolean sprung = false;
    private PIDController controller = null;
    
    /**
     * Initializes a new SpringableVictor on the given PWM port.
     * 
     * @param   port    The PWM port the Victor is connected to.
     */
    public SpringableVictor (int port) {
        super(port);
    }
    
    /**
     * Initializes a new SpringableVictor on the given module slot and PWM port.
     * 
     * @param   slot    The module slot the Victor is connected to.
     * @param   port    The PWM port the Victor is connected to.
     */
    public SpringableVictor (int slot, int port) {
        super(slot, port);
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
     * Sets the power of the Victor.
     * 
     * @param   speed   The speed to set.
     */
    public void set (double speed) {
        super.set(speed);
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
        super.pidWrite(output);
        this.spring();
    }
    
    /**
     * If the Victor has been sprung, unspring it; if not, set the output to 0.
     */
    public void reload () {
        if (!this.sprung && (this.controller == null || !this.controller.isEnable()))
            super.set(0D);
        
        this.sprung = false;
    }
    
    /**
     * Sets the PIDController for this Victor, if there is one.
     * 
     * If the PIDController is enabled, reload will assume it's updating it, and
     * won't reset the output to 0.
     * 
     * @param   controller  The PIDController for this Victor. 
     */
    public void setController (PIDController controller) {
        this.controller = controller;
    }
}
