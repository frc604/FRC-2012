package com._604robotics.robot2012.ai;

import java.util.Vector;

public interface Tutor {
    public class Bounds {
        public double distance = 0D;
        public double max = 0D;
        public double min = 0D;
        
        public Bounds (double distance, double max, double min) {
            this.distance = distance;
            this.max = max;
            this.min = min;
        }
    }
    
    public abstract void configure (double distance);
    public abstract void record ();
    public abstract double shoot ();
    public abstract void feedback (int term);
    public abstract Vector getData ();
}