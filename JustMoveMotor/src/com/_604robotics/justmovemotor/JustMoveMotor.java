package com._604robotics.justmovemotor;

import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class JustMoveMotor extends SimpleRobot {
    private final Victor motor = new Victor(6);
    
    public JustMoveMotor () {
        SmartDashboard.putDouble("Motor Power", 0D);
    }
    
    public void autonomous () {
        
    }

    public void operatorControl () {
        while (this.isEnabled() && this.isOperatorControl())
            this.motor.set(SmartDashboard.getDouble("Motor Power", 0D));
    }
}
