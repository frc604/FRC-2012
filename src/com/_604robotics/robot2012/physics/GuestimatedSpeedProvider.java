package com._604robotics.robot2012.physics;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class GuestimatedSpeedProvider implements SpeedProvider {
    
    private double setSpeed;
    private double setPow;
    private double constantFactor;
    private double linearFactor;
    private double quadraticFactor;
    
    private Timer timer = new Timer();
    private double deltaV;
    private double spikeTime = .25;
    
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
}
