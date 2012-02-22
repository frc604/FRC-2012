package com._604robotics.robot2012.cameraservo;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Victor;

public class TurretOutput implements PIDOutput {
    private Victor turret;
    
    public TurretOutput (Victor turret) {
        this.turret = turret;
    }
    
    public void pidWrite (double output) {
        //output *= -1;
        if (output > -0.1 && output < 0.1)
            output = 0.0;
        
        System.out.println("PID Output: " + output);
        turret.set(output);
    }
}