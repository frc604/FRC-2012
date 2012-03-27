package com._604robotics.robot2012.firing;

public interface FiringProvider {
    public abstract double getSpeed ();
    public abstract boolean isAtFender ();
    public abstract void setAtFender (boolean atFender);
}
