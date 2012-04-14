package com._604robotics.robot2012;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.utils.SmarterDashboard;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class HybridControlMode extends ControlMode {
	
	int step = 1;
	
	double drivePower;
	double gyroAngle;
	
	boolean turnedAround = false;
	
	boolean kinect = false;
	boolean abort = false;

	Timer controlTimer;
	
	public void step() {
		kinect = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
		abort = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
		
		if (kinect || abort)
			break;
		
		theRobot.encoderShooter.sample();
		
		if (step > SmarterDashboard.getDouble("Auton: Max Step", AutonomousConfiguration.MAX_STEP) && step < 6) {
			SmartDashboard.putInt("STOPPED AT", step);
			this.resetMotors(true);
			
			continue;
		} else {
			SmartDashboard.putInt("STOPPED AT", -1);
		}
		
		/* Handle the main logic. */
		
		SmartDashboard.putInt("CURRENT STEP", step);
		SmartDashboard.putDouble("CONTROL TIMER", controlTimer.get());
		
		switch (step) {
		case 1:
			/* Put the elevator up. */
			
			theRobot.driveTrain.tankDrive(0D, 0D);
			
			if (controlTimer.get() < AutonomousConfiguration.STEP_1_ELEVATOR_TIME) {
				if (theRobot.elevatorMachine.crank(ElevatorState.HIGH)) {
					controlTimer.reset();
					step++;
				}
			} else {
				theRobot.elevatorMachine.crank(999);
				controlTimer.reset();
				step++;
			}
			
			break;
		case 2:
			/* Shoot! */
			
			theRobot.driveTrain.tankDrive(0D, 0D);
			theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
			
			if (controlTimer.get() < AutonomousConfiguration.STEP_2_SHOOT_TIME)
				theRobot.shooterMachine.crank(ShooterState.SHOOTING);
			else if (((String) theRobot.inTheMiddle.getSelected()).equals("Yes"))
				step++;
			else
				step = 6;
			
			break;
		case 3:
			/* 
			 * Turn around and face the bridge, and put the elevator
			 * down.
			 */
			
			if (controlTimer.get() <= SmarterDashboard.getDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_TURN_TIME)) {
				theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
				gyroAngle = theRobot.gyroHeading.getAngle();
				
				if (turnedAround || (gyroAngle > 179 && gyroAngle < 181)) {
					turnedAround = true;
					theRobot.driveTrain.tankDrive(0D, 0D);
				} else {
					drivePower = Math.max(0.2, 1 - gyroAngle / 180);
					theRobot.driveTrain.tankDrive(drivePower, drivePower * -1);
				}
			} else {
				theRobot.driveTrain.tankDrive(0D, 0D);
				
				controlTimer.reset();
				if(theRobot.elevatorMachine.crank(ElevatorState.MEDIUM))
					step++;
			}
			
			break;
		case 4:
			/* Drive forward and stop, then smash down the bridge. */
			
			if (controlTimer.get() <= AutonomousConfiguration.STEP_4_DRIVE_TIME) {
				SmartDashboard.putString("STAGE", "DRIVING");
				drivePower = Math.min(-0.2, (1 - controlTimer.get() / SmarterDashboard.getDouble("Auton: Step 4", AutonomousConfiguration.STEP_4_DRIVE_TIME)) * -1);
				SmartDashboard.putDouble("AUTON DRIVE POWER", drivePower);
				theRobot.driveTrain.tankDrive(drivePower, drivePower);
			} else {
				SmartDashboard.putString("STAGE", "SMASHING!");
				theRobot.driveTrain.tankDrive(0D, 0D);
				theRobot.pickupMachine.crank(PickupState.OUT);
				
				controlTimer.reset();
				step++;
			}
			
			break;
		case 5:
			/* Wait a bit. */
			
			theRobot.driveTrain.tankDrive(0D, 0D);
			
			if (controlTimer.get() >= SmarterDashboard.getDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_WAIT_TIME)) 
				step++;
			
			break;
		case 6:
			/* Pull in the pickup and put the elevator down. */
			
			if (theRobot.elevatorMachine.test(ElevatorState.PICKUP_OKAY)) {
				if (theRobot.pickupMachine.crank(PickupState.IN))
					theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
			} else {
				theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
			}
			
			break;
		}
		
		this.resetMotors();
	}
	
	public void init() {
		theRobot.driveTrain.setSafetyEnabled(true);
		
		DriverStation.getInstance().setDigitalOut(2, false);
		DriverStation.getInstance().setDigitalOut(5, false);
		
		theRobot.compressorPump.start();
		
		
		/* Reset stuff. */
		turnedAround = false;
		
		kinect = false;
		abort = false;
		
		
		theRobot.firingProvider.setAtFender(false);
		// TODO: Make this better.
		
		/* Set stuff up. */
		
		controlTimer = new Timer();
		controlTimer.start();
		
		
		
		theRobot.elevatorMotors.set(0D);
		
		theRobot.gyroHeading.reset();
		
		theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);	
	}
	
	public void disable() {

		theRobot.speedProvider.reset();
		
		theRobot.driveTrain.setSafetyEnabled(false);
		
		System.out.println("BROKEN OUT OF AUTON");
		System.out.println("isAutonomous(): " + isAutonomous() + ", isEnabled(): " + isEnabled() + ", abort: " + abort + ", kinect: " + kinect);
		
		while (isAutonomous() && isEnabled() && !abort && !kinect) {
			kinect = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
			abort = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
			
			System.out.println("WAITING FOR KINECT");
			
			this.resetMotors(true);
		}
		
		if (kinect)
			kinectMode();
		
		theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
		
		this.resetMotors();
		
		theRobot.pickupMotor.set(0D);
		theRobot.hopperMotor.set(0D);
		
		theRobot.compressorPump.stop();
	}
	

	/**
	 * Resets the motors.
	 * 
	 * @param   driveToo    Reset the drive train too?
	 */
	public void resetMotors (boolean driveToo) {
		if (driveToo)
			theRobot.driveTrain.tankDrive(0D, 0D);
		
		
		theRobot.elevatorMotors.reload();
		theRobot.shooterMotors.reload();
		theRobot.hopperMotor.reload();
		theRobot.ringLight.reload();
	}
	
	/**
	 * Resets the motors, but not the drive train.
	 */
	public void resetMotors() {
		this.resetMotors(false);
	}
	
	
	
	
	
	
	
	
	
	

	/**
	 * Kinect-controlled Hybrid mode.
	 */
	public void kinectMode() {
		
		
		while (isAutonomous() && isEnabled() && !abort) {

			this.resetMotors(false);
		}
	}
}
