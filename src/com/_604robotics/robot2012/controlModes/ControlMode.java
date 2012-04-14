package com._604robotics.robot2012.controlModes;

import com._604robotics.robot2012.TheRobot;


public abstract class ControlMode {

	public final TheRobot theRobot = TheRobot.theRobot;
	
	public abstract void step();
	

	public abstract void init();
	
	public abstract void disable();
}
