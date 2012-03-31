/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com._604robotics.robot2012.speedcontrol;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AwesomeSpeedController implements SpeedProvider {
    
    public double maxSpeed = 1;
    public double fac = .9;
    
    private final PIDDP controller;
    private final PIDSource source;
    private final PIDOutput output;
    private final AddableDifferentialOutput diffOutput;
    
    private boolean loaded = false;
    
    private class AddableDifferentialOutput implements PIDOutput {
        private final PIDOutput out;
        private double process = 0D;
        private double add = 0;
        
        public AddableDifferentialOutput (PIDOutput out) {
            this.out = out;
        }
        
        public void pidWrite (double output) {
            this.out.pidWrite((this.process += output) + add);
            
            if(this.process > maxSpeed)
                this.process = maxSpeed;
            else if(this.process < -maxSpeed)
                this.process = -maxSpeed;
        }
        
        public void setProcess (double process) {
            this.process = process;
        }
    }
    
    public AwesomeSpeedController (double P, double I, double D, double DP, PIDSource source, PIDOutput output) {
        this.source = source;
        this.output = output;
        this.diffOutput = new AddableDifferentialOutput(this.output);
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
        System.out.println(this.getSetSpeed());
        
        this.loaded = true;
        double setpoint = this.getSetSpeed();
        
        if (this.source.pidGet() < fac * setpoint) {
            this.output.pidWrite(-maxSpeed);
        } else {
            diffOutput.add = TurretSpeedGuestimator.guestimatePow(setpoint);
            this.controller.enable();
        }
    }

    public void reset() {
        if (!this.loaded) {
            this.controller.reset();
            this.diffOutput.setProcess(0);
            this.output.pidWrite(0D);
        }
        this.loaded = false;
    }
}
