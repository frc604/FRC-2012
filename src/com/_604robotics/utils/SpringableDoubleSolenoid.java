package com._604robotics.utils;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * Extender of a DoubleSolenoid providing an easier control flow.
 * 
 * When an output is set for the DoubleSolenoid, it is considered "sprung". When
 * the "reload" method is called, if the victor is sprung, it unsprings the
 * DoubleSolenoid. If the DoubleSolenoid is not sprung, then the output is set
 * to the default output. In this way, the DoubleSolenoid will only be moving
 * when you tell it to. Use this in a loop or something, and call "reload" at
 * the end. No more worries about code paths that don't update the
 * DoubleSolenoids!
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class SpringableDoubleSolenoid extends DoubleSolenoid {
    private final Value defaultDirection;
    
    private boolean sprung = false;
    
    /**
     * Initializes a new SpringableDoubleSolenoid with the specified forward
     * channel, reverse channel, and default direction.
     * 
     * @param   forwardChannel      The forward channel of the DoubleSolenoid.
     * @param   reverseChannel      The reverse channel of the DoubleSolenoid.
     * @param   defaultDirection    The default direction for reloads.
     */
    public SpringableDoubleSolenoid (int forwardChannel, int reverseChannel, Value defaultDirection) {
        super(forwardChannel, reverseChannel);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    /**
     * Initializes a new SpringableDoubleSolenoid with the specified forward
     * channel, reverse channel, and default direction.
     * 
     * @param   moduleNumber        The slot number of the solenoid module.
     * @param   forwardChannel      The forward channel of the DoubleSolenoid.
     * @param   reverseChannel      The reverse channel of the DoubleSolenoid.
     * @param   defaultDirection    The default direction for reloads.
     */
    public SpringableDoubleSolenoid (int moduleNumber, int forwardChannel, int reverseChannel, Value defaultDirection) {
        super(moduleNumber, forwardChannel, reverseChannel);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    /**
     * Has the DoubleSolenoid been sprung?
     * 
     * @return  Whether or not the DoubleSolenoid has been sprung.
     */
    public boolean getSprung () {
        return this.sprung;
    }
    
    /**
     * Springs the DoubleSolenoid.
     */
    public void spring () {
        this.sprung = true;
    }
    
    /** 
     * Sets the direction of the DoubleSolenoid.
     * 
     * @param   direction   The direction to set.
     */
    public void set (Value direction) {
        super.set(direction);
        this.spring();
    }
    
    /**
     * If the DoubleSolenoid has been sprung, unspring it; if not, set the
     * output to the default output.
     */
    public void reload () {
        if (!this.sprung)
            super.set(this.defaultDirection);
        
        this.sprung = false;
    }
}