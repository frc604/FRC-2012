package com._604robotics.robot2012;

import edu.wpi.first.wpilibj.Timer;


public abstract class AutonControlMode extends ControlMode {

	int step = 1;
	Timer controlTimer;
	double drivePower;
	double gyroAngle;
	boolean turnedAround = false;
	
}
