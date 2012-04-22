package com._604robotics.robot2012.control.modes;

public interface ControlMode {
    public abstract void init ();
    public abstract boolean step ();
    public abstract void disable ();
    
    public abstract String getName ();
}
