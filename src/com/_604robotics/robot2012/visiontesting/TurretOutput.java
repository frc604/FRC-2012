package com._604robotics.robot2012.visiontesting;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Victor;

public class TurretOutput implements PIDOutput {
    private Victor turret;
    private double lastOutput;
    
    public TurretOutput (Victor turret) {
        this.turret = turret;
    }
    
    public void pidWrite (double output) {
        output *= -1;
        this.lastOutput = output;
        turret.set(output);
    }
    
    public double getLastOutput () {
        return lastOutput;
    }
}
