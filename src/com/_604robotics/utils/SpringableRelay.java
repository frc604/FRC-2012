package com._604robotics.utils;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;

/**
 * Extender of a Relay providing an easier control flow.
 * 
 * When an output is set for the Relay, it is considered "sprung". When the
 * "reload" method is called, if the victor is sprung, it unsprings the Relay.
 * If the Relay is not sprung, then the output is set to the default output. In
 * this way, the Relay will only be moving when you tell it to. Use this in a
 * loop or something, and call "reload" at the end. No more worries about code
 * paths that don't update the Relays!
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class SpringableRelay extends Relay {
    private final Value defaultDirection;
    private boolean sprung = false;
    
    /**
     * Initializes a new SpringableRelay.
     * 
     * @param   moduleNumber        The module slot the Relay is on.
     * @param   channel             The channel the Relay is on.
     * @param   direction           The direction the Relay should control.
     * @param   defaultDirection    The default direction for reloading.
     */
    public SpringableRelay (int moduleNumber, int channel, Direction direction, Value defaultDirection) {
        super(moduleNumber, channel, direction);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    /**
     * Initializes a new SpringableRelay.
     * 
     * @param   channel             The channel the Relay is on.
     * @param   direction           The direction the Relay should control.
     * @param   defaultDirection    The default direction for reloading.
     */
    public SpringableRelay (int channel, Direction direction, Value defaultDirection) {
        super(channel, direction);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    /**
     * Initializes a new SpringableRelay.
     * 
     * @param   moduleNumber        The module slot the Relay is on.
     * @param   channel             The channel the Relay is on.
     * @param   defaultDirection    The default direction for reloading.
     */
    public SpringableRelay (int moduleNumber, int channel, Value defaultDirection) {
        super(moduleNumber, channel);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    /**
     * Initializes a new SpringableRelay.
     * 
     * @param   channel             The channel the Relay is on.
     * @param   defaultDirection    The default direction for reloading.
     */
    public SpringableRelay (int channel, Value defaultDirection) {
        super(channel);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    /**
     * Has the Relay been sprung?
     * 
     * @return  Whether or not the Relay has been sprung.
     */
    public boolean getSprung () {
        return this.sprung;
    }
    
    /**
     * Springs the Relay.
     */
    public void spring () {
        this.sprung = true;
    }
    
    /** 
     * Sets the direction of the Relay.
     * 
     * @param   direction   The direction to set.
     */
    public void set (Value direction) {
        super.set(direction);
        this.spring();
    }
    
    /**
     * If the Relay has been sprung, unspring it; if not, set the output to the
     * default output.
     */
    public void reload () {
        if (!this.sprung)
            super.set(this.defaultDirection);
        
        this.sprung = false;
    }
}