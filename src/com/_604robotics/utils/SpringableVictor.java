package com._604robotics.utils;

import edu.wpi.first.wpilibj.Victor;

/**
 * TODO: Document.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class SpringableVictor extends Victor {
    private boolean sprung = false;
    
    public SpringableVictor (int port) {
        super(port);
    }
    
    public SpringableVictor (int slot, int port) {
        super(slot, port);
    }
    
    public boolean getSprung () {
        return this.sprung;
    }
    
    public void spring () {
        this.sprung = true;
    }
    
    public void set (double speed) {
        super.set(speed);
        this.spring();
    }
    
    public void pidWrite (double output) {
        super.pidWrite(output);
        this.spring();
    }
    
    public void reload () {
        if (!this.sprung)
            super.set(0D);
        
        this.sprung = false;
    }
}
