package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;

public class DeadbandedSource implements PIDSource {
    private final PIDSource source;
    
    private PIDController controller;
    
    private double upperDeadband = 0D;
    private double lowerDeadband = 0D;

    public DeadbandedSource (PIDSource source) {
        this.source = source;
    }
    
    public void setController (PIDController controller) {
        this.controller = controller;
    }

    public void setDeadband (double lowerDeadband, double upperDeadband) {
        this.lowerDeadband = lowerDeadband;
        this.upperDeadband = upperDeadband;
    }

    public double pidGet () {
        double val = this.source.pidGet();
        if (val > this.lowerDeadband && val < this.upperDeadband)
            val = this.controller.getSetpoint();
        return val;
    }
}
