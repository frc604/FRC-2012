package com._604robotics.robot2012.speedcontrol;

import com._604robotics.utils.DualVictor;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

public class ProcessSpeedProvider implements SpeedProvider {
    private final PIDController controller;
    private final PIDSource source;
    private final ProcessSpeedProvider.DifferentialOutput output;
    
    private boolean loaded = false;
    
    private class DifferentialOutput implements PIDOutput {
        private final PIDOutput out;
        private double process = 0D;
        
        public DifferentialOutput (PIDOutput out) {
            this.out = out;
        }
        
        public void pidWrite (double output) {
            this.out.pidWrite(this.process += output);
        }
        
        public void setProcess (double process) {
            this.process = process;
        }
    }
    
    public ProcessSpeedProvider (double P, double I, double D, PIDSource source, PIDOutput output) {
        this.source = source;
        this.output = new ProcessSpeedProvider.DifferentialOutput(output);
        this.controller = new PIDController(P, I, D, this.source, this.output);
    }

    public double getMotorPower() {
        return this.controller.get();
    }

    public void setSetSpeed(double setSpeed) {
        this.controller.setSetpoint(setSpeed);
    }

    public double getSetSpeed() {
        return this.controller.getSetpoint();
    }

    public boolean isOnTarget(double tolerance) {
        return Math.abs(tolerance - this.source.pidGet()) < tolerance;
    }

    public double getP () {
        return this.controller.getP();
    }

    public double getI () {
        return this.controller.getI();
    }

    public double getD () {
        return this.controller.getD();
    }

    public void setPID (double P, double I, double D) {
        this.controller.setPID(P, I, D);
    }
    
    public void apply() {
        this.loaded = true;
        this.controller.enable();
    }

    public void reset() {
        if (!this.loaded) {
            this.controller.reset();
            this.output.setProcess(1D);
        }
    }
}
