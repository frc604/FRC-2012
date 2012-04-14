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


/**
 *
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class HybridControlMode extends ControlMode {
	
	boolean kinect = false;
	boolean abort = false;
	boolean kinectInitted = false;

	AutonControlMode auton = new ShootAndDoABunchOfOtherCrapAutonControlMode();
	KinectControlMode kinectMode = new KinectControlMode(this);
	
	
	public void step() {
		kinect = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
		abort = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
		
		if (abort) {

			theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
			return;	//TODO - should break somehow...
		}
		
		if(!kinect) {
			
			theRobot.encoderShooter.sample();
			
			if (auton.step > SmarterDashboard.getDouble("Auton: Max Step", AutonomousConfiguration.MAX_STEP) && auton.step < 6) {
				SmartDashboard.putInt("STOPPED AT", auton.step);
				this.resetMotors(true);

				System.out.println("WAITING FOR KINECT");
				
				return; // TODO - isn't there a better way to "wait"?
			} else {
				SmartDashboard.putInt("STOPPED AT", -1);
			}
			
			/* Handle the main logic. */
			
			SmartDashboard.putInt("CURRENT STEP", auton.step);
			SmartDashboard.putDouble("CONTROL TIMER", auton.controlTimer.get());
			
			
			auton.step();
			
			
			this.resetMotors();
		} else {
			if(!kinectInitted) {
				theRobot.speedProvider.reset();
				
				theRobot.driveTrain.setSafetyEnabled(false);
				/* TODO - XXX
				System.out.println("BROKEN OUT OF AUTON");
				
				/*"isAutonomous(): " + isAutonomous() + ", isEnabled(): " + isEnabled() + ", "+ * /
				System.out.println("abort: " + abort + ", kinect: " + kinect);
				*/
				
				
				this.resetMotors();
				
				theRobot.pickupMotor.set(0D);
				theRobot.hopperMotor.set(0D);
				
				theRobot.compressorPump.stop();
				
				kinectMode.init();
				
				kinectInitted = true;
			}
			
			kinectMode.step();
		}
	}
	
	public void init() {
		theRobot.driveTrain.setSafetyEnabled(true);
		
		DriverStation.getInstance().setDigitalOut(2, false);
		DriverStation.getInstance().setDigitalOut(5, false);
		
		theRobot.compressorPump.start();
		
		
		/* Reset stuff. */
		
		kinect = false;
		abort = false;
		
		
		theRobot.firingProvider.setAtFender(false);
		// TODO: Make this better.
		
		/* Set stuff up. */
		
		
		
		theRobot.elevatorMotors.set(0D);
		
		theRobot.gyroHeading.reset();
		
		theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);	
		auton.init();
	}
	
	public void disable() {
		kinectMode.disable();
		auton.disable();
		
		kinectInitted = false;
		
		theRobot.speedProvider.reset();
		
		theRobot.driveTrain.setSafetyEnabled(false);
		
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
}
