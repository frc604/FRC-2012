package com._604robotics.robot2012.speedcontrol;

import com._604robotics.robot2012.configuration.FiringConfiguration;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AwesomeSpeedController implements SpeedProvider {
    public double maxSpeed = 1;
    public double fac = .9;
    
    private double currentValue = 0;
    
    private final PIDDP controller;
    private final PIDSource source;
    private final PIDOutput output;
    private final AddableDifferentialOutput diffOutput;
    
    private boolean loaded = false;
    
    private long lastApplied = 0;
    private long lastChanged = 0;
    
    private class AddableDifferentialOutput implements PIDOutput {
        private double process = 0D;
        private double add = 0;
        
        public void pidWrite (double output) {
            this.process += output;
            
            if(this.process > maxSpeed)
                this.process = maxSpeed;
            else if(this.process < -maxSpeed)
                this.process = -maxSpeed;
            
            currentValue = this.process + add;
        }
        
        public void setProcess (double process) {
            this.process = process;
        }
        
        public double getCurrent () {
            return process + add;
        }
    }
    
    public AwesomeSpeedController(double P, double I, double D, double DP, PIDSource source, PIDOutput output) {
        this.source = source;
        this.output = output;
        this.diffOutput = new AddableDifferentialOutput();
        this.controller = new PIDDP(P, I, D, DP, this.source, this.diffOutput);
        
        //this.controller.setMaximumInput(0);
        //this.controller.setMinimumInput(-540);
    }

    public double getMotorPower() {
        return this.diffOutput.getCurrent();
    }

    public void setSetSpeed(double setSpeed) {
        if (setSpeed != this.getSetSpeed())
            this.lastChanged = System.currentTimeMillis();
        this.controller.setSetpoint(setSpeed);
    }

    public double getSetSpeed() {
        return this.controller.getSetpoint();
    }

    public boolean isOnTarget(double tolerance) {
        if (FiringConfiguration.USE_HOPPER_THRESHOLD) {
            return Math.abs(this.getSetSpeed() - this.source.pidGet()) < tolerance;
        } else {
            final long now = System.currentTimeMillis();
            System.out.println(now - this.lastChanged);
            return now - this.lastChanged >= FiringConfiguration.CHARGE_TIME && now - this.lastApplied < 50;
        }
    }
    
    public PIDDP getController () {
        return controller;
    }
    
    public void apply() {
        this.loaded = true;
        this.lastApplied = System.currentTimeMillis();
        
        final double setpoint = this.getSetSpeed();
        
        if (Math.abs(this.source.pidGet()) < fac * setpoint) {
            this.output.pidWrite(-maxSpeed);
        } else {
            diffOutput.add = -TurretSpeedGuestimator.guestimatePow(setpoint);
            this.controller.enable();
            
            output.pidWrite(currentValue);
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
