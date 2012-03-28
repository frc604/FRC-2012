package com._604robotics.robot2012.speedcontrol;

import com._604robotics.utils.DualVictor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class EncoderSpeedProvider implements SpeedProvider {
    private final DualVictor motor;
    private final Encoder encoder;
    private final Timer timer = new Timer();
    
    private boolean loaded = false;
    
    private double setSpeed;
    private double P, I;
    private double integral;
    
    private static final double maxTimePerIntegral = .05; // 50 ms max integration time
    
    public EncoderSpeedProvider(DualVictor motor, Encoder encoder) {
        this.motor = motor;
        this.encoder = encoder;
        this.timer.start();
    }
    
    public double getP() {
        return P;
    }
    
    public void setP(double p) {
        P = p;
    }
    
    public double getI() {
        return I;
    }
    
    public void setI(double i) {
        I = i;
    }
    
    public double getMotorPower() {
        double currentSpeed = this.encoder.getRate();
        double cTime = timer.get();
        
        if(cTime > maxTimePerIntegral)
            cTime = maxTimePerIntegral;
        
        integral += cTime*currentSpeed;
        
        return (setSpeed-currentSpeed)*P + integral*I;
    }
    
    public void setSetSpeed(double setSpeed) {
        this.setSpeed = setSpeed;
    }
    
    public double getSetSpeed() {
        return setSpeed;
    }
    
    public boolean isOnTarget(double tolerance) {
        return Math.abs(tolerance - this.encoder.getRate()) < tolerance;
    }
    
    public void apply() {
        this.loaded = true;
        this.motor.set(this.getMotorPower());
    }
    
    public void reset() {
        if (!this.loaded) {
            integral = 0;
            timer.reset();
            this.motor.set(0D);
        }
        
        this.loaded = false;
    }
}
