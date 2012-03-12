package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

public class ConvertingPIDController extends PIDController {
    private double conversionFactor = 1D;
    
    public ConvertingPIDController (double Kp, double Ki, double Kd, PIDSource source, PIDOutput output) {
        super(Kp, Ki, Kd, source, output);
    }

    public ConvertingPIDController (double Kp, double Ki, double Kd, PIDSource source, PIDOutput output, double period) {
        super(Kp, Ki, Kd, source, output, period);
    }
    
    public double getRealSetpoint () {
        return super.getSetpoint();
    }
    
    public double getSetpoint () {
        return super.getSetpoint() / this.conversionFactor;
    }
    
    public void setRealSetpoint (double setpoint) {
        super.setSetpoint(setpoint);
    }
    
    public void setSetpoint (double setpoint) {
        super.setSetpoint(setpoint * this.conversionFactor);
    }
    
    public void setConversionFactor (double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }
}
