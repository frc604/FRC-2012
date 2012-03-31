package com._604robotics.robot2012.speedcontrol;

import com._604robotics.utils.DualVictor;
import edu.wpi.first.wpilibj.Timer;

public class StupidSpeedProvider implements SpeedProvider {
    private final DualVictor motor;
    private final Timer timer = new Timer();
    
    private boolean loaded = false;
    
    private double speed = 0D;
    
    public StupidSpeedProvider (DualVictor motor) {
        this.motor = motor;
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
        return this.timer.get() >= 1;
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