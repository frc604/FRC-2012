package com._604robotics.robot2012;

import com._604robotics.utils.XboxController;
import com.sun.squawk.util.MathUtils;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot2012Orange extends SimpleRobot {

	/* Actuator polarity and speed configuration. */
	public   static final double ACCELEROMETER_DRIVE_POWER	=0.5;
	// TODO: Configure this.

	public static final Value SOLENOID_SHIFTER_HIGH_POWER_DIRECTION =DoubleSolenoid.Value.kForward; //Shifting to a high power gear ratio
	public static final Value SOLENOID_SHIFTER_LOW_POWER_DIRECTION  =DoubleSolenoid.Value.kReverse; //Shifting to a low power gear ratio
        public static final Value SOLENOID_PICKUP_RAISED_POSITION       =DoubleSolenoid.Value.kReverse; //Pickup device is raised
        public static final Value SOLENOID_PICKUP_LOWERED_POSITION      =DoubleSolenoid.Value.kForward; //Pickup device is lowered

	/* Sensor configuration. */
	public static final double GYRO_DRIFT			=0.0238095238;  //Calculating the gyro drift
	// TODO: Configure this.

	public static final double ACCELEROMETER_SENSITIVITY	=1; //setting the Accelerometer sensitivity for balancing (may or may not be used)
	// TODO: Configure this.
	public static final double ACCELEROMETER_UPPER_RADIANS	=0.7854;    //Setting the upper bound for the Accelerometer
	// TODO: Configure this.




	XboxController controller;  //Declaring the object "controller" as an XboxController

	RobotDrive driveTrain;  //Declaring the object "driveTrain" as the object determining Ddriving configuration

	Gyro gyroDriving;
	Accelerometer accelBalance;
        Encoder encoderDrive1;
        Encoder encoderDrive2;
        PIDController driveStraight;
        PIDController driveBackwards;
        
	Compressor compressorPump;

	DoubleSolenoid solenoidShifter;
        DoubleSolenoid solenoidPickup;
        
        Timer autonomousTimer;

	public Robot2012Orange() {
		controller = new XboxController(Ports.XBOX_CONTROLLER_PORT);
		driveTrain = new RobotDrive(new Victor(Ports.FRONT_LEFT_MOTOR_PORT),
				new Victor(Ports.REAR_LEFT_MOTOR_PORT),
				new Victor(Ports.FRONT_RIGHT_MOTOR_PORT),
				new Victor(Ports.REAR_RIGHT_MOTOR_PORT));
		gyroDriving = new Gyro(Ports.GYRO_PORT);
		accelBalance = new Accelerometer(Ports.ACCELEROMETER_PORT);
                encoderDrive1 = new Encoder(Ports.ENCODER_PORT1,Ports.ENCODER_PORT2);
                encoderDrive2 = new Encoder(Ports.ENCODER_PORT3,Ports.ENCODER_PORT4);
                driveStraight = new PIDController(0,0,0,new PIDDriveEncoderDifference(encoderDrive1,encoderDrive2),new PIDDriveEncoderOutput(driveTrain));
		driveBackwards = new PIDController (0,0,0,new PIDDriveEncoderDifference(encoderDrive1,encoderDrive2),new PIDEncoderOutputBackwards(driveTrain));
                solenoidShifter = new DoubleSolenoid(Ports.SHIFTER_SOLENOID_FORWARD_PORT, Ports.SHIFTER_SOLENOID_REVERSE_PORT);
		solenoidPickup = new DoubleSolenoid(Ports.PICKUP_SOLENOID_DROPPED_PORT,Ports.PICKUP_SOLENOID_RAISED_PORT);
                autonomousTimer = new Timer();
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

	public void autonomous() {  
		getWatchdog().setEnabled(false);

		compressorPump.start();
                encoderDrive1.start();
                encoderDrive2.start();  
                
		while(isAutonomous() && isEnabled()) {
                    if (encoderDrive1.get()!=1000){       //Encoder Drive: Drive for a certain amount of distance to the bridge. Use encoders to 
                        driveStraight.enable();
			// TODO: Write autonomous mode code                        
                    }
                    solenoidPickup.set(SOLENOID_PICKUP_LOWERED_POSITION);
                    solenoidPickup.set(SOLENOID_PICKUP_RAISED_POSITION);
                    driveStraight.disable();
                
                    solenoidPickup.set(SOLENOID_PICKUP_LOWERED_POSITION);
                    autonomousTimer.delay(0.1);
                    solenoidPickup.set(SOLENOID_PICKUP_RAISED_POSITION);
                    autonomousTimer.delay(0.2);
                    encoderDrive1.reset();
                    encoderDrive2.reset();
                    if (encoderDrive1.get()!=-1000){
                        driveBackwards.enable();
                    }
                    driveBackwards.disable();
                    //TODO: Turn 180, shoot
                }
		compressorPump.stop();
	}

	public void operatorControl() {
		getWatchdog().setEnabled(true);
		driveTrain.setSafetyEnabled(true);

		compressorPump.start();
		double accelPower = 0;

		// TODO: Move over gyro stuff from other project, once it's all hammered out.

		while (isOperatorControl() && isEnabled()) {
			getWatchdog().feed();

			if (controller.getAxis(3) > 0.2 || controller.getAxis(3) < -0.2) { // XBOX: Left XOR Right trigger
				solenoidShifter.set(SOLENOID_SHIFTER_HIGH_POWER_DIRECTION);
				SmartDashboard.putString("Gear", "High");
			} else {
				solenoidShifter.set(SOLENOID_SHIFTER_LOW_POWER_DIRECTION);
				SmartDashboard.putString("Gear", "Low");
			}

			if (controller.getButton(Ports.ACCEL_BALANCE_BUTTON)) { 
				// TODO: Make this better.
				accelPower = deadband(MathUtils.asin(accelBalance.getAcceleration())) / ACCELEROMETER_UPPER_RADIANS * ACCELEROMETER_DRIVE_POWER;
				driveTrain.tankDrive(accelPower, accelPower);
				SmartDashboard.putString("Drive Mode", "Balancing");
				SmartDashboard.putDouble("Accel Output", accelPower);
			} else {
				driveTrain.tankDrive(controller.getAxis(2), controller.getAxis(5)); // Tank drive with left and right sticks on Xbox controller.
				SmartDashboard.putString("Drive Mode", "Manual");
			}

			if (controller.getButton(Ports.AIM_TURRET_BUTTON)) {
				// TODO: Insert camera control, aiming components, when they're done, of course.
			}

			if (controller.getButton(Ports.FIRE_BUTTON)) {
				// TODO: Insert firing components, when they're done, of course.
			}
		}

		compressorPump.stop();
                
		driveTrain.setSafetyEnabled(false);
		getWatchdog().setEnabled(false);
	}

	public void disabled() {
		getWatchdog().setEnabled(false);
		compressorPump.stop();
	}
}
