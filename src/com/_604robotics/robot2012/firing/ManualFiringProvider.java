package com._604robotics.robot2012.firing;

import com._604robotics.robot2012.configuration.FiringConfiguration;

public class ManualFiringProvider implements FiringProvider {
    private boolean atFender = false;
    
    public ManualFiringProvider () {
        
    }
    
    public double getSpeed () {
        return (this.atFender)
                ? FiringConfiguration.FENDER_FIRING_SPEED
                : FiringConfiguration.KEY_FIRING_SPEED;
    }

    public boolean isAtFender () {
        return this.atFender;
    }
    
    public void setAtFender (boolean atFender) {
        this.atFender = atFender;
    }
}
