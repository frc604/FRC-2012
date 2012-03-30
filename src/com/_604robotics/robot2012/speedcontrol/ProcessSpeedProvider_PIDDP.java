package com._604robotics.robot2012.speedcontrol;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ProcessSpeedProvider_PIDDP implements SpeedProvider {
    private final Timer spinUp = new Timer();
    
    private final PIDDP controller;
    private final PIDSource source;
    private final PIDOutput output;
    private final ProcessSpeedProvider_PIDDP.DifferentialOutput diffOutput;
    
    private boolean loaded = false;
    private boolean ran = false;
    
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
    
    public ProcessSpeedProvider_PIDDP (double P, double I, double D, double DP, PIDSource source, PIDOutput output) {
        this.source = source;
        this.output = output;
        this.diffOutput = new ProcessSpeedProvider_PIDDP.DifferentialOutput(this.output);
        this.controller = new PIDDP(P, I, D, DP, this.source, this.diffOutput);
        this.spinUp.start();
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
    
    public double getDP () {
        return this.controller.getDP();
    }

    public void setPID (double P, double I, double D) {
        this.controller.setPID(P, I, D);
    }

    public void setPIDDP (double P, double I, double D, double DP) {
        this.controller.setPIDDP(P, I, D, DP);
    }
    
    public void apply() {
        if (!this.ran)
            this.spinUp.reset();
        this.ran = true;
        this.loaded = true;
        SmartDashboard.putDouble("spinUp", this.spinUp.get());
        if (this.spinUp.get() < 0.5)
            this.output.pidWrite(-0.7);
        else
            this.controller.enable();
    }

    public void reset() {
        if (!this.loaded) {
            this.ran = false;
            this.controller.reset();
            this.diffOutput.setProcess(-0.7);
            this.output.pidWrite(0D);
        }
        this.loaded = false;
    }
}