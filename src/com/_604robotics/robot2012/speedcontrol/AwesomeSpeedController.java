/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com._604robotics.robot2012.speedcontrol;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AwesomeSpeedController {
    
    public double maxSpeed = 1;
    public double fac = .9;
    
    private final PIDDP controller;
    private final PIDSource source;
    private final PIDOutput output;
    private final DifferentialOutput diffOutput;
    
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
    
    public AwesomeSpeedController (double P, double I, double D, double DP, PIDSource source, PIDOutput output) {
        this.source = source;
        this.output = output;
        this.diffOutput = new DifferentialOutput(this.output);
        this.controller = new PIDDP(P, I, D, DP, this.source, this.diffOutput);
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
        this.ran = true;
        this.loaded = true;
        if (this.source.pidGet() < fac * controller.getSetpoint())
            this.output.pidWrite(-maxSpeed);
        else
            this.controller.enable();
    }

    public void reset() {
        if (!this.loaded) {
            this.ran = false;
            this.controller.reset();
            this.diffOutput.setProcess(0);
            this.output.pidWrite(0D);
        }
        this.loaded = false;
    }
}
