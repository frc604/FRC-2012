package com._604robotics.robot2012.physics;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class EncoderSpeedProvider implements SpeedProvider {
    
    private Timer timer = new Timer();
    private double setSpeed;
    private double P, I;
    private double integral;
    private double currentSpeed;
    
    private static final double maxTimePerIntegral = .05; // 50 ms max integration time
    
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
    
    public void resetIntegral() {
        integral = 0;
        timer.reset();
    }
    
    
    /**
     * 
     * @param current   The speed as given from the encoder
     */
    public void setCurrentSpeed(double current) {
        currentSpeed = current;
        
        double cTime = timer.get();
        
        if(cTime > maxTimePerIntegral)
            cTime = maxTimePerIntegral;
        
        integral += cTime*currentSpeed;
    }
    

    public double getMotorPower() {
        return (setSpeed-currentSpeed)*P + integral*I;
    }
    
    public void setSetSpeed(double setSpeed) {
        this.setSpeed = setSpeed;
    }
    
    public double getSetSpeed() {
        return setSpeed;
    }
    
    
    public boolean isOnTarget(double tolerance) {
        return Math.abs(tolerance - currentSpeed) < tolerance;
    }
}
