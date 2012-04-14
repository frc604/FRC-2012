package com._604robotics.robot2012.learning;

public interface Tutor {
    public class Bounds {
        public double distance = 0D;
        public double max = 0D;
        public double min = 0D;
    }
    
    public abstract void configure (double distance);
    public abstract double shoot ();
    public abstract void feedback (int term);
    public abstract Bounds[] getData ();
}