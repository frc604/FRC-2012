package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DifferentialMotorOutput implements PIDOutput {
    private final DualVictor motor;
    private final Timer timeout = new Timer();
    
    private double lastOutput = 0D;
    
    public DifferentialMotorOutput (DualVictor motor) {
        this.motor = motor;
        this.timeout.start();
    }

    public void pidWrite(double output) {
        if (this.timeout.get() > 1)
            this.lastOutput = 0D;
        this.timeout.reset();
        this.lastOutput += output;
        this.motor.set(this.lastOutput);
    }
}
