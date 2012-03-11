package com._604robotics.utils;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class SpringableDoubleSolenoid extends DoubleSolenoid {
    private final Value defaultDirection;
    private boolean sprung = false;
    
    public SpringableDoubleSolenoid (int forwardChannel, int reverseChannel, Value defaultDirection) {
        super(forwardChannel, reverseChannel);
        super.set(this.defaultDirection = defaultDirection);
    }
    
    public SpringableDoubleSolenoid (int moduleNumber, int forwardChannel, int reverseChannel, Value defaultDirection) {
        super(moduleNumber, forwardChannel, reverseChannel);
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