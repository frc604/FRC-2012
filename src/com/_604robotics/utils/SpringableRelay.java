package com._604robotics.utils;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;

public class SpringableRelay extends Relay {
    private final Value defaultDirection;
    private boolean sprung = false;
    
    public SpringableRelay (int moduleNumber, int channel, Direction direction, Value defaultDirection) {
        super(moduleNumber, channel, direction);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    public SpringableRelay (int channel, Direction direction, Value defaultDirection) {
        super(channel, direction);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    public SpringableRelay (int moduleNumber, int channel, Value defaultDirection) {
        super(moduleNumber, channel);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    public SpringableRelay (int channel, Value defaultDirection) {
        super(channel);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    public boolean getSprung () {
        return this.sprung;
    }
    
    public void spring () {
        this.sprung = true;
    }
    
    public void set (Value direction) {
        super.set(direction);
        this.spring();
    }
    
    public void reload () {
        if (!this.sprung)
            super.set(this.defaultDirection);
        
        this.sprung = false;
    }
}