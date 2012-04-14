package com._604robotics.robot2012;

import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.SmarterDashboard;
import com._604robotics.utils.UpDownPIDController.Gains;
import com._604robotics.utils.XboxController.Axis;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * Main class for the 2012 robot code codenamed Orange.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * @author  Aaron Wang <aaronw94@gmail.com>
 * @author  Colin Aitken <cacolinerd@gmail.com>
 * @author  Alan Li <alanpusongli@gmail.com>
 */
public class Robot2012Orange extends SimpleRobot {
	
	// local reference to theRobot
	TheRobot theRobot = TheRobot.theRobot;
	

	//teleop
	boolean upHigh = false;
	boolean pickupIn = true;
	
	/**
	 * Constructor.
	 * 
	 * Disables the built-in watchdog, since it's not really needed anymore.
	 */
	public Robot2012Orange() {
		DriverStation.getInstance().setDigitalOut(2, false);
		DriverStation.getInstance().setDigitalOut(5, false);
		
		this.getWatchdog().setEnabled(false);
	}
	
	/**
	 * Initializes the robot on startup.
	 * 
	 * Sets up all the controllers, sensors, actuators, etc.
	 */
	public void robotInit () {
		/* Set up the controllers. */
		TheRobot.init();
		
		
		/* Done booting! */
		System.out.println("All done booting!");
	}
	
	/**
	 * Resets the motors.
	 * 
	 * @param   driveToo    Reset the drive train too?
	 */
	public void resetMotors (boolean driveToo) {
		if (driveToo)
			theRobot.driveTrain.tankDrive(0D, 0D);
		
		theRobot.speedProvider.reset();
		
		theRobot.elevatorMotors.reload();
		theRobot.shooterMotors.reload();
		theRobot.hopperMotor.reload();
		theRobot.ringLight.reload();
	}
	
	/**
	 * Resets the motors, but not the drive train.
	 */
	public void resetMotors () {
		this.resetMotors(false);
	}
	
	/**
	 * Automated drive for autonomous mode.
	 * 
	 * If in middle, drive forward, knock down bridge, turn around.
	 * 
	 * Else, or then, go ahead and try to score.
	 */
	public void autonomous() {
		DriverStation.getInstance().setDigitalOut(2, false);
		DriverStation.getInstance().setDigitalOut(5, false);
		
		theRobot.compressorPump.start();
		
		int step = 1;
		
		double drivePower;
		double gyroAngle;
		
		boolean turnedAround = false;
		
		boolean kinect = false;
		boolean abort = false;
		
		/* Reset stuff. */
		
		upHigh = false;
		pickupIn = true;
		
		theRobot.firingProvider.setAtFender(false);
		// TODO: Make this better.
		
		/* Set stuff up. */
		
		Timer controlTimer = new Timer();
		controlTimer.start();
		
		theRobot.elevatorMotors.set(0D);
		
		theRobot.gyroHeading.reset();
		
		theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
		
		while (isAutonomous() && isEnabled()) {
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
	 * Kinect-controlled Hybrid mode.
	 */
	public void kinectMode() {
		boolean abort = false;
		
		System.out.println("KINECT ON");
		
		theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
		
		while (isAutonomous() && isEnabled() && !abort) {
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
			
			this.resetMotors();
		}
	}
	
	/**
	 * Operator-controlled drive for Teleop mode.
	 * 
	 * Handles robot driving, automated balancing for the bridge, ball pickup,
	 * turret aiming, firing, angle adjustments, light control, elevator
	 * control - both automated and manual - pneumatics, shifting, and various
	 * other things.
	 */
	public void operatorControl() {
		DriverStation.getInstance().setDigitalOut(2, false);
		DriverStation.getInstance().setDigitalOut(5, false);
		
		theRobot.driveTrain.setSafetyEnabled(true);
		theRobot.compressorPump.start();
		
		int settleState = 2;
		Timer settleTimer = new Timer();
		
		Target[] targets;
		Target target;
		
		AwesomeSpeedController ctrl = (theRobot.speedProvider instanceof AwesomeSpeedController)
		? ((AwesomeSpeedController) theRobot.speedProvider)
				: null;
		
		theRobot.manipulatorController.resetToggles();
		theRobot.driveController.resetToggles();
		
		theRobot.pidElevator.reset();
		
		while (isOperatorControl() && isEnabled()) {
			theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
			theRobot.encoderShooter.sample();
			
			if (ctrl != null) {
				ctrl.setPIDDP(SmarterDashboard.getDouble("Shooter P", ctrl.getP()), SmarterDashboard.getDouble("Shooter I", ctrl.getI()), SmarterDashboard.getDouble("Shooter D", ctrl.getD()), SmarterDashboard.getDouble("Shooter DP", ctrl.getDP()));
				ctrl.fac = SmarterDashboard.getDouble("Shooter fac", ctrl.fac);
				ctrl.maxSpeed = SmarterDashboard.getDouble("Shooter maxSpeed", ctrl.maxSpeed);
			}
			
			theRobot.pidElevator.setUpGains(new Gains(SmarterDashboard.getDouble("Elevator Up P", 0.0085), SmarterDashboard.getDouble("Elevator Up I", 0D), SmarterDashboard.getDouble("Elevator Up D", 0.018)));
			theRobot.pidElevator.setDownGains(new Gains(SmarterDashboard.getDouble("Elevator Down P", 0.0029), SmarterDashboard.getDouble("Elevator Down I", 0.000003), SmarterDashboard.getDouble("Elevator Down P", 0.007)));
			
			if (theRobot.driveController.getToggle(ButtonConfiguration.Driver.DISABLE_ELEVATOR))
				theRobot.elevatorMotors.setDisabled(!theRobot.elevatorMotors.getDisabled());
			
			if (!theRobot.elevatorLimitSwitch.get())
				theRobot.encoderElevator.reset();
			
			if (Math.abs(theRobot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y)) > 0)
				theRobot.hopperMotor.set(theRobot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
			
			if (!theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP) && Math.abs(theRobot.manipulatorController.getAxis(Axis.LEFT_STICK_Y)) > 0)
				theRobot.pickupMotor.set(theRobot.manipulatorController.getAxis(Axis.LEFT_STICK_Y));
			
			/* Controls the gear shift. */
			
			if (theRobot.driveController.getButton(ButtonConfiguration.Driver.SHIFT))
				theRobot.solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR);
			
			/* Drive train controls. */
			
			if (theRobot.driveController.getButton(ButtonConfiguration.Driver.TINY_FORWARD)) {
				theRobot.driveTrain.tankDrive(ActuatorConfiguration.TINY_FORWARD_SPEED, ActuatorConfiguration.TINY_FORWARD_SPEED);
				SmartDashboard.putString("Drive Mode", "Tiny (Forward)");
			} else if (theRobot.driveController.getButton(ButtonConfiguration.Driver.TINY_REVERSE)) {
				theRobot.driveTrain.tankDrive(ActuatorConfiguration.TINY_REVERSE_SPEED, ActuatorConfiguration.TINY_REVERSE_SPEED);
				SmartDashboard.putString("Drive Mode", "Tiny (Reverse)");
			} else if (theRobot.driveController.getButton(ButtonConfiguration.Driver.SLOW_BUTTON)) {
				theRobot.driveTrain.tankDrive(theRobot.driveController.getAxis(Axis.LEFT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED * -1, theRobot.driveController.getAxis(Axis.RIGHT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED * -1);
				SmartDashboard.putString("Drive Mode", "Manual (Slow)");
			} else {
				theRobot.driveTrain.tankDrive(theRobot.driveController.getAxis(Axis.LEFT_STICK_Y) * -1, theRobot.driveController.getAxis(Axis.RIGHT_STICK_Y) * -1);
				SmartDashboard.putString("Drive Mode", "Manual");
			}
			
			/* Manually set whether or not we're at the fender. */
			
			if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_FENDER)) {
				theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
				if (theRobot.elevatorMachine.test(ElevatorState.HIGH))
					theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
				theRobot.firingProvider.setAtFender(true);
			} else if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_KEY)) {
				theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
				if (theRobot.elevatorMachine.test(ElevatorState.HIGH))
					theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
				theRobot.firingProvider.setAtFender(false);
			}
			
			/* Toggle the "default" height between "up high" and "down low". */
			
			if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.UP)) {
				upHigh = true;
				pickupIn = true;
			} else if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.DOWN)) {
				upHigh = false;
			}
			
			/* Toggle the pickup state between "up" and "down". */
			
			if (theRobot.driveController.getToggle(ButtonConfiguration.Driver.TOGGLE_PICKUP)) {
				pickupIn = !pickupIn;
				if (pickupIn == false)
					upHigh = false;
			}
			
			
			SmartDashboard.putBoolean("upHigh", upHigh);
			SmartDashboard.putBoolean("pickupIn", pickupIn);
			
			/*
			 * If upHigh is true, then raise the elevator. If pickupIn is true,
			 * then make sure the elevator is up high enough, and lift up the
			 * pickup.
			 * 
			 * Else, check the "default" position. If it is up high, then lower
			 * the pickup and raise the elevator simultaneously. If it is down
			 * low, then make sure the pickup is down first, and then lower the
			 * elevator.
			 */
			
			if (theRobot.driveController.getButton(ButtonConfiguration.Driver.CALIBRATE)) {
				if (theRobot.pickupMachine.crank(PickupState.OUT)) {
					theRobot.elevatorMotors.setDisabled(false);
					theRobot.elevatorMotors.set(-0.4);
				}
			} else {
				if (upHigh) {
					if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT) || theRobot.elevatorMachine.crank(ElevatorState.HIGH)) {
						SmartDashboard.putString("Ready to Shoot", "Yes");
						
						/*
						 * Toggles the shooter angle.
						 */
						 
						if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
							if (theRobot.solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE) {
								theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
							} else {
								theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
							}
						}
						
						if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT)) {
							System.out.println("GET READY U GUIZE");
							theRobot.shooterMachine.crank(ShooterState.SHOOTING);
						}
					}
				} else {
					if (pickupIn)
						theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
					else if (theRobot.pickupMachine.test(PickupState.OUT))
						theRobot.elevatorMachine.crank(ElevatorState.LOW);
				}
				
				if (pickupIn) {
					if (theRobot.elevatorMachine.test(ElevatorState.PICKUP_OKAY) || theRobot.elevatorMotors.getDisabled())
						theRobot.pickupMachine.crank(PickupState.IN);
				} else {
					if (theRobot.pickupMachine.crank(PickupState.OUT)) {
						/*
						 * If the pickup is down and the elevator is at rest,
						 * then allow the user to trigger the pickup mechanism.
						 */
						
						if (theRobot.elevatorMachine.test(ElevatorState.LOW)) {
							/* Controls the pickup mechanism. */
							
							if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP)) {
								theRobot.pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
								theRobot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
								theRobot.elevatorMotors.set(ActuatorConfiguration.ELEVATOR_PICKUP_POWER);
								
								settleState = 0;
								settleTimer.stop();
							}
						}
					}
				}
			}
			
			/*
			 * Settle the balls back in a bit after picking up.
			 */
			
			if (!theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP) && settleState != 2) {
				if (settleState == 0) {
					settleTimer.reset();
					settleTimer.start();
					
					settleState = 1;
				}
				
				if (settleTimer.get() < 0.3) {
					theRobot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER_REVERSE);
				} else {
					settleTimer.stop();
					
					settleState = 2;
				}
			}
			
			/* Toggles the light. */
			
			if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.TOGGLE_LIGHT))
				theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
			
			/* Reload the springs. */
			
			theRobot.speedProvider.reset();
			
			theRobot.elevatorMotors.reload();
			theRobot.shooterMotors.reload();
			theRobot.hopperMotor.reload();
			theRobot.pickupMotor.reload();
			
			theRobot.ringLight.reload();
			
			theRobot.solenoidShifter.reload();
			theRobot.solenoidHopper.reload();
			
			/* Driver assist. */
			
			targets = theRobot.cameraInterface.getTargets();
			target = null;
			
			for (int i = 0; i < targets.length; i++) {
				if (target == null || targets[i].y < target.y) {
					target = targets[i];
				}
			}
			
			if (target == null)
				SmartDashboard.putDouble("Raw X Pos", 999999.999);
			else
				SmartDashboard.putDouble("Raw X Pos", target.x);
			
			/* Debug output. */
			
			SmartDashboard.putDouble("encoderShooter", theRobot.encoderShooter.get());
			SmartDashboard.putDouble("Current Encoder Rate", theRobot.encoderShooter.getRate());
			SmartDashboard.putDouble("Current Shooter Output", theRobot.shooterMotors.get());
			
			SmartDashboard.putDouble("Shooter Speed", theRobot.shooterMachine.getShooterSpeed());
			SmartDashboard.putBoolean("Using Targets?", theRobot.firingProvider.usingTargets());
			SmartDashboard.putBoolean("At the Fender?", theRobot.firingProvider.isAtFender());
			
			SmartDashboard.putDouble("gyroHeading", theRobot.gyroHeading.getAngle());
			
			SmartDashboard.putInt("ups", ((RemoteCameraTCP) theRobot.cameraInterface).getUPS());
			
			SmartDashboard.putDouble("encoderElevator", theRobot.encoderElevator.get());
			SmartDashboard.putDouble("Current Elevator Setpoint", theRobot.pidElevator.getSetpoint());
			SmartDashboard.putDouble("Elevator Output", theRobot.pidElevator.get());
		}
		
		theRobot.pidElevator.disable();
		
		theRobot.compressorPump.stop();
		
		theRobot.driveTrain.setSafetyEnabled(false);
	}
	
	/**
	 * The robot is disabled.
	 * 
	 * Like ze goggles, zees does nothing.
	 */
	public void disabled() {
		theRobot.compressorPump.stop();
		theRobot.driveTrain.setSafetyEnabled(false);
		
		boolean didIJustRecalibrateElevator = false;
		
		Timer lastRecalibrated = new Timer();
		
		lastRecalibrated.start();
		
		while (!isEnabled()) {
			if (!theRobot.elevatorLimitSwitch.get()) {
				if(!didIJustRecalibrateElevator)
					System.out.println("CALIBRATED ELEVATOR");
				didIJustRecalibrateElevator = true;
				DriverStation.getInstance().setDigitalOut(5, true);
				SmartDashboard.getBoolean("Elevator Calibrated", true);
				if (!didIJustRecalibrateElevator || lastRecalibrated.get() >= 1)
					theRobot.encoderElevator.reset();
				else
					lastRecalibrated.reset();
			} else {
				didIJustRecalibrateElevator = false;
			}
		}
	}
}