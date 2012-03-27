/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012.physics;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class HystereticController {
    
    public HystereticController(double inertiaVsMotorForce) {
        this.inertiaVsMotorForce = inertiaVsMotorForce;
    }
    
    private double factor = 100;
    
    /*
     * position, velocity
     */
    private double p, v;
    
    /**
     * Ratio of Inertia of the system and the motor's force at 1.0 power
     * 
     * In a heavy system powered by a weak motor, this value would be large.
     * In a light system powered by a strong motor, this value would be small.
     * 
     * For the robot to automatically figure this value out on its own, it could
     * apply a brief but powerful pulse to the motor (such as .05 sec, 1.0 power)
     * and then capture the max velocity. Then the following math can be
     * performed to find this constant.
     * 
     * p = m*v
     *  J = F*t
     *  J = p2-p1
     *      Since p1 = 0
     *  p2 = p = F*t
     * m*v = F*t
     * m/F = t/v
     * 
     * inertiaVsMotorForce ~= time / max velocity
     * 
     * 
     * The units of this are    [time]^2/[length]
     * For example, seconds^2/tick
     * 
     * This is also the inverse of the motor's acceleration at 1.0 power
     * 
     */
    private double inertiaVsMotorForce;
    
    /**
     * 
     * @param fac   the factor
     */
    public void setFactor(double fac) {
        factor = fac;
    }
    
    /**
     * Where this HystereticController is trying to reach
     */
    private double targetPos;
    
    /**
     * 
     * @param pos   The current position to set
     */
    public void setCurrentPosition(double pos) {
        p = pos;
        // TODO - calculate v and a from pos
    }
    /**
     * 
     * @param pos   The current position to set
     */
    public void setCurrentVelocity(double vel) {
        v = vel;
        // TODO - calculate v and a from pos
    }
    
    /**
     * 
     * @param target    The target to set
     */
    public void setTarget(double target) {
        targetPos = target;
    }
    
    /**
     * 
     * @return  the motor power value
     */
    public double getOutput() {
        double delP = targetPos - p;
        
        // KE= .5*m*v^2
        // W = f*delX
        // f*delX = .5*m*v^2
        // delXStop = .5*(m/f)*v^2
        double delXStop = .5*inertiaVsMotorForce*v*v;
        
        double output = f((delP - delXStop*1.05) * factor * inertiaVsMotorForce);
        
        
        
        return output;
    }
    
    /**
     * This is a function that's cheap to compute whose goal is to look like
     * erf, the error function (which looks similar to atan)
     * 
     * In its current implementation, it is simply a clipping function to [-1,1]
     * 
     * @param x
     * @return 
     */
    private double f(double x) {
        //
        if(x < -1)
            return -1;
        if(x > 1)
            return 1;
        
        return x;
    }
    
}
