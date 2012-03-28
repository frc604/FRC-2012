#include "WPILib.h"
#include <cmath>

/* Port configuration for sensors and actuators. */
	#define XBOX_CONTROLLER_PORT	1

	#define FRONT_LEFT_MOTOR_PORT	3
	#define FRONT_RIGHT_MOTOR_PORT	2
	#define REAR_LEFT_MOTOR_PORT	4
	#define REAR_RIGHT_MOTOR_PORT	1

	#define GYRO_PORT				1
	#define ACCELEROMETER_PORT		2

	#define COMPRESSOR_PORT			3
	#define PRESSURE_SWITCH_PORT	4

	#define SHIFTER_SOLENOID_PORT	4

/* Button configuration. */
	#define AIM_TURRET_BUTTON		1
		// XBOX: B button
	#define FIRE_BUTTON				0
		// XBOX: A button

	#define ACCEL_BALANCE_BUTTON	2
		// XBOX: X button
	#define GYRO_RESET_BUTTON		7
		// XBOX: START button

/* Actuator polarity and speed configuration. */
	#define ACCELEROMETER_DRIVE_POWER	0.5
		// TODO: Configure this.

	#define SOLENOID_SHIFTER_HIGH_POWER_DIRECTION DoubleSolenoid::kForward
	#define SOLENOID_SHIFTER_LOW_POWER_DIRECTION DoubleSolenoid::kReverse

/* Sensor configuration. */
	#define GYRO_DRIFT					0.0238095238
		// TODO: Configure this.

	#define ACCELEROMETER_SENSITIVITY	1
		// TODO: Configure this.
	#define ACCELEROMETER_UPPER_RADIANS	0.7854
		// TODO: Configure this.

class Robot2012Orange : public SimpleRobot {
	Joystick controller;

	RobotDrive driveTrain;

	Gyro gyroDriving;
	Accelerometer accelBalance;

	Compressor* compressorPump;

	Solenoid solenoidShifter;
	
	public:
		Robot2012Orange(void):
			controller(XBOX_CONTROLLER_PORT),
			driveTrain(new Victor(FRONT_LEFT_MOTOR_PORT), new Victor(REAR_LEFT_MOTOR_PORT), new Victor(FRONT_RIGHT_MOTOR_PORT), new Victor(REAR_RIGHT_MOTOR_PORT)),
			gyroDriving(GYRO_PORT),
			accelBalance(ACCELEROMETER_PORT),
			solenoidShifter(SHIFTER_SOLENOID_PORT)
		{
			GetWatchdog().SetEnabled(false);
			driveTrain.SetSafetyEnabled(false);

			driveTrain.SetInvertedMotor(RobotDrive::kFrontLeftMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive::kFrontRightMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive::kRearLeftMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive::kRearRightMotor, true);

			accelBalance.SetSensitivity(ACCELEROMETER_SENSITIVITY);

			compressorPump = new Compressor(PRESSURE_SWITCH_PORT, COMPRESSOR_PORT);
		}

		bool IsInRange(float xValue, float upperRange, float lowerRange) { // Self-explanatory.
			return xValue <= upperRange && xValue >= lowerRange;
		}

		float Deadband(float xValue, float upperBand = 0.1745, float lowerBand = -0.1745, float correctedValue = 0.0) { // The antithesis of the BindToRange function
			return (IsInRange(xValue, upperBand, lowerBand))
					? correctedValue 
					: xValue;
		}

		void Autonomous(void) {
			GetWatchdog().SetEnabled(false);

			compressorPump->Start();

			while(IsAutonomous() && IsEnabled()) {
				// TODO: Write autonomous mode code
			}

			compressorPump->Stop();
		}

		void OperatorControl(void) {
			GetWatchdog().SetEnabled(true);
			driveTrain.SetSafetyEnabled(true);
			
			compressorPump->Start();
			
			SmartDashboard *dash = SmartDashboard::GetInstance();
			
			float accelPower = 0;

			// TODO: Move over gyro stuff from other project, once it's all hammered out.
			
			while (IsOperatorControl() && IsEnabled()) {
				GetWatchdog().Feed();
				
				if (controller.GetRawAxis(3) > 0.2 || controller.GetRawAxis(3) < -0.2) { // XBOX: Left XOR Right trigger
					solenoidShifter.Set(SOLENOID_SHIFTER_HIGH_POWER_DIRECTION);
					dash->PutString("Gear", "High");
				} else {
					solenoidShifter.Set(SOLENOID_SHIFTER_LOW_POWER_DIRECTION);
					dash->PutString("Gear", "Low");
				}
				
				if (controller.GetRawButton(ACCEL_BALANCE_BUTTON)) {
					// TODO: Make this better.
					accelPower = Deadband(asin(accelBalance.GetAcceleration())) / ACCELEROMETER_UPPER_RADIANS * ACCELEROMETER_DRIVE_POWER;
					driveTrain.TankDrive(accelPower, accelPower);
					dash->PutString("Drive Mode", "Balancing");
					dash->PutDouble("Accel Output", accelPower);
				} else {
					driveTrain.TankDrive(controller.GetRawAxis(2), controller.GetRawAxis(5)); // Tank drive with left and right sticks on Xbox controller.
					dash->PutString("Drive Mode", "Manual");
				}
				
				if (controller.GetRawButton(AIM_TURRET_BUTTON)) {
					// TODO: Insert camera control, aiming components, when they're done, of course.
				}
				
				if (controller.GetRawButton(FIRE_BUTTON)) {
					// TODO: Insert firing components, when they're done, of course.
				}
			}

			compressorPump->Stop();
			
			driveTrain.SetSafetyEnabled(false);
			GetWatchdog().SetEnabled(false);
		}
		
		void Disabled(void) {
			GetWatchdog().SetEnabled(false);
			compressorPump->Stop();
		}
	};

START_ROBOT_CLASS(Robot2012Orange);
