package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

public class UpDownPIDController extends PIDController {
    private final PIDSource source;
    
    private Gains upGains;
    private Gains downGains;
    
    private boolean goingUp = false;
    
    public static class Gains {
        public double P;
        public double I;
        public double D;
        
        public Gains (double P, double I, double D) {
            this.P = P;
            this.I = I;
            this.D = D;
        }
    }
    
    public UpDownPIDController (Gains upGains, Gains downGains, PIDSource source, PIDOutput output) {
        super(upGains.P, upGains.I, upGains.D, source, output);
        
        this.source = source;

        this.upGains = upGains;
        this.downGains = downGains;
    }
    
    public Gains getUpGains () {
        return this.upGains;
    }
    
    public Gains getDownGains () {
        return this.downGains;
    }
    
    public void refreshGains () {
        if (this.goingUp)
            super.setPID(this.upGains.P, this.upGains.I, this.upGains.D);
        else
            super.setPID(this.downGains.P, this.downGains.I, this.downGains.D);
        
        System.out.println(this.goingUp);
        System.out.println("P: " + super.getP() + " I: " + super.getI() + " D: " + super.getD());
    }
    
    public void setUpGains (Gains upGains) {
        this.upGains = upGains;
        this.refreshGains();
    }
    
    public void setDownGains (Gains downGains) {
        this.downGains = downGains;
        this.refreshGains();
    }
    
    public synchronized void setSetpoint(double setpoint) {
        super.setSetpoint(setpoint);
        this.goingUp = setpoint > this.source.pidGet();
    }
}