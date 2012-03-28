package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDOutput;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DifferentialMotorOutput implements PIDOutput {
    private final DualVictor motor;
    private double lastOutput = 0D;
    
    public DifferentialMotorOutput (DualVictor motor) {
        this.motor = motor;
    }

    public void pidWrite(double output) {
        this.lastOutput += output;
        this.motor.set(this.lastOutput);
    }
}
