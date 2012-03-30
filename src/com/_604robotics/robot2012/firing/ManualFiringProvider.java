package com._604robotics.robot2012.firing;

import com._604robotics.robot2012.configuration.FiringConfiguration;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ManualFiringProvider implements FiringProvider {
    private boolean atFender = false;
    
    public static double getDouble(String key, double def) {
        try {
            return SmartDashboard.getDouble(key, def);
        } catch (Exception ex) {
            return def;
        }
    }
    
    public ManualFiringProvider () {
        
    }
    
    public double getSpeed () {
        return (this.atFender)
                ? getDouble("Shooter Preset: Fender", FiringConfiguration.FENDER_FIRING_POWER)
                : getDouble("Shooter Preset: Key", FiringConfiguration.KEY_FIRING_POWER);
    }

    public boolean isAtFender () {
        return this.atFender;
    }
    
    public void setAtFender (boolean atFender) {
        this.atFender = atFender;
    }
}
