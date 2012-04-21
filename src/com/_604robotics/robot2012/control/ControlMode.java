package com._604robotics.robot2012.control;

import com._604robotics.robot2012.TheRobot;

public abstract class ControlMode {
    public final TheRobot theRobot = TheRobot.theRobot;

    public abstract boolean step();
    public abstract void init();
    public abstract void disable();
}
