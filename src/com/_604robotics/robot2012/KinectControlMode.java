package com._604robotics.robot2012;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;


public class KinectControlMode extends ControlMode {
	
	boolean abort = false;
	

	public void step() {
		abort = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
		
		if (abort) {
			break;
		}
		
		double kinectDrivePow = .8;
		
		if (theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.DRIVE_ENABLED)) {
			theRobot.driveTrain.tankDrive(theRobot.leftKinect.getRawAxis(2) * -kinectDrivePow, theRobot.rightKinect.getRawAxis(2) * -kinectDrivePow);
		} else {
			theRobot.driveTrain.tankDrive(0D, 0D);
		}
		
		if (theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.SHOOT) && theRobot.elevatorMachine.crank(ElevatorState.HIGH)) {
			theRobot.shooterMachine.crank(ShooterState.SHOOTING);
		} else {
			theRobot.speedProvider.reset();
			if (theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.PICKUP_IN)) {
				if (theRobot.elevatorMachine.crank(ElevatorState.MEDIUM)) {
					theRobot.pickupMachine.crank(PickupState.IN);
				}
			} else {
				if (theRobot.pickupMachine.crank(PickupState.OUT)) {
					theRobot.elevatorMachine.crank(ElevatorState.LOW);
				}
			}
		}
		
		if (theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.SUCK) && theRobot.pickupMachine.test(PickupState.OUT)) {
			theRobot.pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
			theRobot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
		} else {
			theRobot.pickupMotor.set(0D);
			theRobot.hopperMotor.set(0D);
		}
	}

	public void init() {
		abort = false;
		
		System.out.println("KINECT ON");
		
		theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
	}

	public void disable() {
		
	}
	
}
