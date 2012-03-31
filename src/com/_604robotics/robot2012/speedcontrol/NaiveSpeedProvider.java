package com._604robotics.robot2012.speedcontrol;

import com._604robotics.robot2012.configuration.FiringConfiguration;
import com._604robotics.utils.DualVictor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class NaiveSpeedProvider implements SpeedProvider {
    private final DualVictor motor;
    private final Encoder encoder;
    private final Timer timer = new Timer();
    
    private boolean loaded = false;
    
    private double speed = 0D;
    
    public NaiveSpeedProvider (DualVictor motor, Encoder encoder) {
        this.motor = motor;
        this.encoder = encoder;
        this.timer.start();
    }
    
    public double getMotorPower () {
        return this.speed;
    }

    public void setSetSpeed (double setSpeed) {
        this.speed = setSpeed;
    }

    public double getSetSpeed () {  
        return this.speed;
    }

    public boolean isOnTarget (double tolerance) {
        if (this.getSetSpeed() < 0.5 && this.encoder.getRate() >= FiringConfiguration.FENDER_FIRING_SPEED)
            return true;
        else if (this.getSetSpeed() > 0.5 && this.encoder.getRate() >= FiringConfiguration.KEY_FIRING_SPEED)
            return true;
        else
            return false;
    }
    
    public void apply() {
        this.loaded = true;
        this.motor.set(this.getMotorPower());
    }

    public void reset() {
        if (!this.loaded) {
            this.motor.set(0D);
            this.timer.reset();
        }
        this.loaded = false;
    }
}