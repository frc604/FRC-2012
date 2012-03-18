package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * An extender of a PIDController that converts between units when getting and
 * setting a setpoint.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class ConvertingPIDController extends PIDController {
    private double conversionFactor = 1D;
    
    /**
     * Allocate a PID object with the given constants for P, I, D, using a 50ms period.
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output value
     */
    public ConvertingPIDController (double Kp, double Ki, double Kd, PIDSource source, PIDOutput output) {
        super(Kp, Ki, Kd, source, output);
    }

    /**
     * Allocate a PID object with the given constants for P, I, D
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output value
     * @param period the loop time for doing calculations. This particularly effects calculations of the
     * integral and differential terms. The default is 50ms.
     */
    public ConvertingPIDController (double Kp, double Ki, double Kd, PIDSource source, PIDOutput output, double period) {
        super(Kp, Ki, Kd, source, output, period);
    }
    
    /**
     * Gets the "real" setpoint of the PIDController.
     * 
     * @return  The "real" setpoint of the PIDController.
     */
    public double getRealSetpoint () {
        return super.getSetpoint();
    }
    
    public double getSetpoint () {
        return super.getSetpoint() / this.conversionFactor;
    }
    
    /**
     * Sets the "real" setpoint of the PIDController.
     * 
     * @param   setpoint    The "real" setpoint to set.
     */
    public void setRealSetpoint (double setpoint) {
        super.setSetpoint(setpoint);
    }
    
    public void setSetpoint (double setpoint) {
        super.setSetpoint(setpoint * this.conversionFactor);
    }
    
    /**
     * Sets the factor to use when doing conversion on setSetpoint and
     * getSetpoint.
     * 
     * @param   conversionFactor    The conversion factor to use.
     */
    public void setConversionFactor (double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }
}
