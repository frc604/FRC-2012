package com._604robotics.robot2012;

import com.sun.squawk.util.MathUtils;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot2012Orange extends SimpleRobot {

	/* Actuator polarity and speed configuration. */
	public static final double ACCELEROMETER_DRIVE_POWER	=0.5;
	// TODO: Configure this.

	public static final Value SOLENOID_SHIFTER_HIGH_POWER_DIRECTION =DoubleSolenoid.Value.kForward;
	public static final Value SOLENOID_SHIFTER_LOW_POWER_DIRECTION  =DoubleSolenoid.Value.kReverse;

	/* Sensor configuration. */
	public static final double GYRO_DRIFT					=0.0238095238;
	// TODO: Configure this.

	public static final double ACCELEROMETER_SENSITIVITY	=1;
	// TODO: Configure this.
	public static final double ACCELEROMETER_UPPER_RADIANS	=0.7854;
	// TODO: Configure this.




	Joystick controller;

	RobotDrive driveTrain;

	Gyro gyroDriving;
	Accelerometer accelBalance;

	Compressor compressorPump;

	DoubleSolenoid solenoidShifter;

	public Robot2012Orange() {
		controller = new Joystick(Ports.XBOX_CONTROLLER_PORT);
		driveTrain = new RobotDrive(new Victor(Ports.FRONT_LEFT_MOTOR_PORT),
				new Victor(Ports.REAR_LEFT_MOTOR_PORT),
				new Victor(Ports.FRONT_RIGHT_MOTOR_PORT),
				new Victor(Ports.REAR_RIGHT_MOTOR_PORT));
		gyroDriving = new Gyro(Ports.GYRO_PORT);
		accelBalance = new Accelerometer(Ports.ACCELEROMETER_PORT);
		solenoidShifter = new DoubleSolenoid(Ports.SHIFTER_SOLENOID_FORWARD_PORT, Ports.SHIFTER_SOLENOID_REVERSE_PORT);
		getWatchdog().setEnabled(false);
		driveTrain.setSafetyEnabled(false);

		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		driveTrain.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);

		accelBalance.setSensitivity(ACCELEROMETER_SENSITIVITY);

		compressorPump = new Compressor(Ports.PRESSURE_SWITCH_PORT, Ports.COMPRESSOR_PORT);
	}

	public static boolean isInRange(double xValue, double upperRange, double lowerRange) { // Self-explanatory.
		return xValue <= upperRange && xValue >= lowerRange;
	}

	public static double deadband(double xValue, double upperBand, double lowerBand, double correctedValue) { // The antithesis of the BindToRange function
		return (isInRange(xValue, upperBand, lowerBand))
		? correctedValue 
				: xValue;
	}

	public static double deadband(double xValue) { // The antithesis of the BindToRange function
		return deadband(xValue, .1745, -.1745, 0.0);
	}

	public void Autonomous() {
		getWatchdog().setEnabled(false);

		compressorPump.start();

		while(isAutonomous() && isEnabled()) {
			// TODO: Write autonomous mode code
		}

		compressorPump.stop();
	}

	public void OperatorControl() {
		getWatchdog().setEnabled(true);
		driveTrain.setSafetyEnabled(true);

		compressorPump.start();

		double accelPower = 0;

		// TODO: Move over gyro stuff from other project, once it's all hammered out.

		while (isOperatorControl() && isEnabled()) {
			getWatchdog().feed();

			if (controller.getRawAxis(3) > 0.2 || controller.getRawAxis(3) < -0.2) { // XBOX: Left XOR Right trigger
				solenoidShifter.set(SOLENOID_SHIFTER_HIGH_POWER_DIRECTION);
				SmartDashboard.putString("Gear", "High");
			} else {
				solenoidShifter.set(SOLENOID_SHIFTER_LOW_POWER_DIRECTION);
				SmartDashboard.putString("Gear", "Low");
			}

			if (controller.getRawButton(Ports.ACCEL_BALANCE_BUTTON)) { 
				// TODO: Make this better.
				accelPower = deadband(MathUtils.asin(accelBalance.getAcceleration())) / ACCELEROMETER_UPPER_RADIANS * ACCELEROMETER_DRIVE_POWER;
				driveTrain.tankDrive(accelPower, accelPower);
				SmartDashboard.putString("Drive Mode", "Balancing");
				SmartDashboard.putDouble("Accel Output", accelPower);
			} else {
				driveTrain.tankDrive(controller.getRawAxis(2), controller.getRawAxis(5)); // Tank drive with left and right sticks on Xbox controller.
				SmartDashboard.putString("Drive Mode", "Manual");
			}

			if (controller.getRawButton(Ports.AIM_TURRET_BUTTON)) {
				// TODO: Insert camera control, aiming components, when they're done, of course.
			}

			if (controller.getRawButton(Ports.FIRE_BUTTON)) {
				// TODO: Insert firing components, when they're done, of course.
			}
		}

		compressorPump.stop();

		driveTrain.setSafetyEnabled(false);
		getWatchdog().setEnabled(false);
	}

	public void Disabled() {
		getWatchdog().setEnabled(false);
		compressorPump.stop();
	}
}

//START_ROBOT_CLASS(Robot2012Orange);