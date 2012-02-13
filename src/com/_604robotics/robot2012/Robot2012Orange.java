package com._604robotics.robot2012;

public class Robot2012Orange {

	/* Actuator polarity and speed configuration. */
	public static final double ACCELEROMETER_DRIVE_POWER	=0.5;
	// TODO: Configure this.

	public static final >> SOLENOID_SHIFTER_HIGH_POWER_DIRECTION DoubleSolenoid.kForward;
	public static final >> SOLENOID_SHIFTER_LOW_POWER_DIRECTION DoubleSolenoid.kReverse;

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

	Solenoid solenoidShifter;
	
	public Robot2012Orange() {
			controller(XBOX_CONTROLLER_PORT),
			driveTrain(new Victor(FRONT_LEFT_MOTOR_PORT), new Victor(REAR_LEFT_MOTOR_PORT), new Victor(FRONT_RIGHT_MOTOR_PORT), new Victor(REAR_RIGHT_MOTOR_PORT)),
			gyroDriving(GYRO_PORT),
			accelBalance(ACCELEROMETER_PORT),
			solenoidShifter(SHIFTER_SOLENOID_PORT)
			//{
			GetWatchdog().SetEnabled(false);
			driveTrain.SetSafetyEnabled(false);

			driveTrain.SetInvertedMotor(RobotDrive.kFrontLeftMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive.kFrontRightMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive.kRearLeftMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive.kRearRightMotor, true);

			accelBalance.SetSensitivity(ACCELEROMETER_SENSITIVITY);

			compressorPump = new Compressor(PRESSURE_SWITCH_PORT, COMPRESSOR_PORT);
			}

		public boolean IsInRange(float xValue, float upperRange, float lowerRange) { // Self-explanatory.
			return xValue <= upperRange && xValue >= lowerRange;
		}

		public float Deadband(float xValue, float upperBand = 0.1745, float lowerBand = -0.1745, float correctedValue = 0.0) { // The antithesis of the BindToRange function
			return (IsInRange(xValue, upperBand, lowerBand))
			? correctedValue 
					: xValue;
		}

		public void Autonomous() {
			GetWatchdog().SetEnabled(false);

			compressorPump.Start();

			while(IsAutonomous() && IsEnabled()) {
				// TODO: Write autonomous mode code
			}

			compressorPump.Stop();
		}

		public void OperatorControl() {
			GetWatchdog().SetEnabled(true);
			driveTrain.SetSafetyEnabled(true);

			compressorPump.Start();

			SmartDashboard dash = SmartDashboard.GetInstance();

			float accelPower = 0;

			// TODO: Move over gyro stuff from other project, once it's all hammered out.

			while (IsOperatorControl() && IsEnabled()) {
				GetWatchdog().Feed();

				if (controller.GetRawAxis(3) > 0.2 || controller.GetRawAxis(3) < -0.2) { // XBOX: Left XOR Right trigger
					solenoidShifter.Set(SOLENOID_SHIFTER_HIGH_POWER_DIRECTION);
					dash.PutString("Gear", "High");
				} else {
					solenoidShifter.Set(SOLENOID_SHIFTER_LOW_POWER_DIRECTION);
					dash.PutString("Gear", "Low");
				}

				if (controller.GetRawButton(ACCEL_BALANCE_BUTTON)) {
					// TODO: Make this better.
					accelPower = Deadband(asin(accelBalance.GetAcceleration())) / ACCELEROMETER_UPPER_RADIANS * ACCELEROMETER_DRIVE_POWER;
					driveTrain.TankDrive(accelPower, accelPower);
					dash.PutString("Drive Mode", "Balancing");
					dash.PutDouble("Accel Output", accelPower);
				} else {
					driveTrain.TankDrive(controller.GetRawAxis(2), controller.GetRawAxis(5)); // Tank drive with left and right sticks on Xbox controller.
					dash.PutString("Drive Mode", "Manual");
				}

				if (controller.GetRawButton(AIM_TURRET_BUTTON)) {
					// TODO: Insert camera control, aiming components, when they're done, of course.
				}

				if (controller.GetRawButton(FIRE_BUTTON)) {
					// TODO: Insert firing components, when they're done, of course.
				}
			}

			compressorPump.Stop();

			driveTrain.SetSafetyEnabled(false);
			GetWatchdog().SetEnabled(false);
		}

		public void Disabled() {
			GetWatchdog().SetEnabled(false);
			compressorPump.Stop();
		}
}
}

START_ROBOT_CLASS(Robot2012Orange);