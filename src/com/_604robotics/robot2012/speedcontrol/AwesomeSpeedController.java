package com._604robotics.robot2012.speedcontrol;

import com._604robotics.robot2012.configuration.FiringConfiguration;
import com._604robotics.robot2012.control.models.Shooter;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AwesomeSpeedController implements SpeedProvider {
    private final Timer targetTimer = new Timer();
    
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
        this.targetTimer.start();
        
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
        if (Shooter.fullPower) {
            this.controller.setSetpoint(999D);
            return;
        }
        
        if (System.currentTimeMillis() - this.lastApplied < 100)
            return;
        if (setSpeed != this.getSetSpeed())
            this.lastChanged = System.currentTimeMillis();
        this.controller.setSetpoint(setSpeed);
    }

    public double getSetSpeed() {
        return this.controller.getSetpoint();
    }

    public boolean isOnTarget(double tolerance) {
        if (FiringConfiguration.USE_HOPPER_THRESHOLD) {
            if (Math.abs(this.getSetSpeed() - this.source.pidGet()) < tolerance)
                targetTimer.reset();
            return targetTimer.get() < 0.5;
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
