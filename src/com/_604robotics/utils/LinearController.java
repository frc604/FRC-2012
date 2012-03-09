package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

public class LinearController {
    private final PIDSource source;
    private final PIDOutput output;
    
    private double horizontalRange;
    private double horizontalOutput;
    
    private double coastingRange;
    private double coastingOutput;
    
    private double target = 0D;
    private boolean direction = false;
    
    private interface Direction {
        public static final boolean FORWARD = true;
        public static final boolean REVERSE = true;
    }
    
    public LinearController (PIDSource source, PIDOutput output, double horizontalRange, double horizontalOutput, double coastingRange, double coastingOutput) {
        this.source = source;
        this.output = output;
        this.horizontalRange = horizontalRange;
        this.horizontalOutput = horizontalOutput;
        this.coastingRange = coastingRange;
        this.coastingOutput = coastingOutput;
    }
    
    public void setHorizontalValues (double horizontalRange, double horizontalOutput) {
        this.horizontalRange = horizontalRange;
        this.horizontalOutput = horizontalOutput;
    }
    
    public void setCoastingRange (double coastingRange, double coastingOutput) {
        this.coastingRange = coastingRange;
        this.coastingOutput = coastingOutput;
    }
    
    public double getTarget () {
        return this.target;
    }
    
    public void setTarget (double target) {
        this.target = target;
        if (this.target > this.source.pidGet())
            this.direction = Direction.FORWARD;
        else
            this.direction = Direction.REVERSE;
    }
    
    private double calculate () {
        double pos = this.source.pidGet();
        double dist = Math.abs(pos - this.target);
        
        if ((this.direction == Direction.FORWARD && pos >= this.target) || (this.direction == Direction.REVERSE && pos <= this.target))
            return 0D;
        
        if (dist <= this.coastingRange)
            return this.coastingOutput;
        
        if (dist <= this.horizontalRange)
            return this.horizontalOutput;
        
        return (this.horizontalOutput - this.coastingOutput) * ((dist - this.coastingRange) / (this.horizontalRange - this.coastingRange)) + this.coastingOutput;
    }
    
    public void update () {
        this.output.pidWrite(this.calculate());
    }
}