package com._604robotics.utils;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

public class VelocityController {
    private final Encoder encoder;
    private final PIDOutput output;
    private final PIDController controller;
    
    private class EncoderWrapper implements PIDSource {
        private final Encoder encoder;
        
        public EncoderWrapper (Encoder encoder) {
            this.encoder = encoder;
        }
        
        public double pidGet () {
            return this.encoder.getRate();
        }
    }
    
    public VelocityController (double p, double i, double d, Encoder encoder, PIDOutput output) {
        this.encoder = encoder;
        this.output = output;
        this.controller = new PIDController(p, i, d, new EncoderWrapper(encoder), output);
    }
    
    public double getVelocity () {
        return this.controller.getSetpoint();
    }
    
    public void setVelocity (double velocity) {
        this.controller.setSetpoint(velocity);
    }
    
    public void setGains(double p, double i, double d) {
        this.controller.setPID(p, i, d);
    }
    
    public void enable () {
        this.controller.enable();
    }
    
    public void disable () {
        this.controller.disable();
    }
    
    public boolean isEnabled () {
        return this.controller.isEnable();
    }
}