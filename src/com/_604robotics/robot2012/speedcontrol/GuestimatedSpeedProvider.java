package com._604robotics.robot2012.speedcontrol;

import com._604robotics.utils.DualVictor;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class GuestimatedSpeedProvider implements SpeedProvider {
    private final DualVictor motor;
    private final Timer timer = new Timer();
    
    private boolean loaded = false;
    
    private double setSpeed;
    private double setPow;
    private double constantFactor;
    private double linearFactor;
    private double quadraticFactor;
    
    private double deltaV;
    private double spikeTime = .25;
    
    public GuestimatedSpeedProvider(DualVictor motor) {
        this.motor = motor;
        this.timer.start();
    }
    
    public double getLinearFactor() {
        return linearFactor;
    }
    
    public double getQuadraticFactor() {
        return quadraticFactor;
    }
    public double getConstantFactor() {
        return constantFactor;
    }
    
    public void setLinearFactor(double linFac) {
        linearFactor = linFac;
    }
    public void setQuadraticFactor(double quadFac) {
        quadraticFactor = quadFac;
    }
    public void setConstantFactor(double constFac) {
        constantFactor = constFac;
    }

    public double getMotorPower() {
        return setPow - MathUtils.exp(-timer.get()/spikeTime);
    }
    
    public void setSetSpeed(double setSpeed) {
        this.setSpeed = setSpeed;
        
        if(setSpeed == 0)
            setPow = 0;
        
        double absSpeed = Math.abs(setSpeed);
        double absPow = absSpeed*linearFactor + absSpeed*absSpeed*quadraticFactor + constantFactor;
        
        this.setPow = absPow* (setSpeed<0?-1:1);
        //TODO - fix
    }
    
    public double getSetSpeed() {
        return setSpeed;
    }
    
    public boolean isOnTarget(double tolerance) {
        return timer.get() > deltaV*spikeTime;
    }

    public void apply() {
        this.loaded = true;
        this.motor.set(this.getMotorPower());
    }

    public void reset() {
        if (!this.loaded)
            this.motor.set(0D);
        this.loaded = false;
    }
}
