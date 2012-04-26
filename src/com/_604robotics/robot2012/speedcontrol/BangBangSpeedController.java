package com._604robotics.robot2012.speedcontrol;

import com._604robotics.utils.DualVictor;
import edu.wpi.first.wpilibj.Encoder;

public class BangBangSpeedController implements SpeedProvider {
    private final DualVictor motor;
    private final Encoder encoder;
    
    private double targetSpeed = 0D;
    
    private boolean loaded = false;
    
    public double spinupSpeed = 0D;
    public double lowPower = 0.5D;
    
    public BangBangSpeedController (DualVictor motor, Encoder encoder) {
        this.motor = motor;
        this.encoder = encoder;
    }
    
    public double getMotorPower () {
        final double measuredRPM = this.encoder.getRate();
        
        if (measuredRPM >= this.targetSpeed)
            return 0D;
        else if (measuredRPM >= this.spinupSpeed)
            return 1D;
        else
            return this.lowPower;
    }

    public void setSetSpeed (double setSpeed) {
        this.targetSpeed = setSpeed;
    }

    public double getSetSpeed () {
        return this.targetSpeed;
    }
    
    public boolean isOnTarget(double tolerance) {
        return Math.abs(this.targetSpeed - this.encoder.getRate()) < tolerance;
    }
    
    public void apply() {
        this.loaded = true;
        this.motor.set(this.getMotorPower() * -1);
    }

    public void reset() {
        if (!this.loaded)
            this.motor.set(0D);
        this.loaded = false;
    }
}