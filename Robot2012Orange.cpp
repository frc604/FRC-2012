#include "WPILib.h"
#include <cmath>

/* Port configuration for sensors and actuators. */
	#define LEFT_DRIVE_JOYSTICK_USB_PORT 3
	#define MANIPULATOR_JOYSTICK_USB_PORT 1
	#define RIGHT_DRIVE_JOYSTICK_USB_PORT 2

	#define FRONT_LEFT_MOTOR_PORT 3
	#define FRONT_RIGHT_MOTOR_PORT 2
	#define REAR_LEFT_MOTOR_PORT 4
	#define REAR_RIGHT_MOTOR_PORT 1

	#define GYRO_PORT 1
	#define ACCELEROMETER_PORT 2

	#define COMPRESSOR_PORT 3
	#define PRESSURE_SWITCH_PORT 4

/* Button configuration. */
	/* Driver Button Configuration */
		#define DRIVER_SHIFT_BUTTON 1

		#define DRIVER_GYRO_RESET_BUTTON 5
		#define DRIVER_GYRO_FORWARD_BUTTON 3
		#define DRIVER_GYRO_REVERSE_BUTTON 2

		#define DRIVER_ACCEL_BALANCE_BUTTON 6
			// TODO: Configure this.

/* Actuator polarity and speed configuration. */
	#define GYRO_DRIVE_POWER 0.9
		// TODO: Configure this.
	#define ACCELEROMETER_DRIVE_POWER 0.5
		// TODO: Configure this.

/* Sensor configuration. */
	#define GYRO_DRIFT 0.0238095238
		// TODO: Configure this.

	#define ACCELEROMETER_SENSITIVITY 1
		// TODO: Configure this.
	#define ACCELEROMETER_UPPER_RADIANS 0.7854
		// TODO: Configure this.

class Robot2012Orange : public SimpleRobot {
	Joystick joystickManipulator;
	Joystick joystickDriveLeft;
	Joystick joystickDriveRight;

	RobotDrive driveTrain;

	Gyro gyroDriving;
	Accelerometer accelBalance;

	Compressor* compressorPump;

	public:
		Robot2012Orange(void):
			joystickManipulator(MANIPULATOR_JOYSTICK_USB_PORT),
			joystickDriveLeft(LEFT_DRIVE_JOYSTICK_USB_PORT),
			joystickDriveRight(RIGHT_DRIVE_JOYSTICK_USB_PORT),
			driveTrain(new Victor(FRONT_LEFT_MOTOR_PORT), new Victor(REAR_LEFT_MOTOR_PORT), new Victor(FRONT_RIGHT_MOTOR_PORT), new Victor(REAR_RIGHT_MOTOR_PORT)),
			gyroDriving(GYRO_PORT),
			accelBalance(ACCELEROMETER_PORT)
		{
			GetWatchdog().SetEnabled(false); // If you're just beginning, and nothing's going on, there's no need for Watchdog to be doing anything.

			driveTrain.SetInvertedMotor(RobotDrive::kFrontLeftMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive::kFrontRightMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive::kRearLeftMotor, true);
			driveTrain.SetInvertedMotor(RobotDrive::kRearRightMotor, true);

			accelBalance.SetSensitivity(ACCELEROMETER_SENSITIVITY);

			compressorPump = new Compressor(PRESSURE_SWITCH_PORT, COMPRESSOR_PORT);
		}

		float Deadband(float xValue, float upperBand = 0.1745, float lowerBand = -0.1745, float correctedValue = 0.0) { // The antithesis of the BindToRange function
			return (IsInRange(xValue, upperBand, lowerBand)) ? (correctedValue) : (xValue);
		}

		void Autonomous(void) {
			GetWatchdog().SetEnabled(false); // No need for Watchdog in Autonomous, either.

			compressorPump->Start(); // Let's start up the compressor and charge up for Teleop.

			while(IsAutonomous() && IsEnabled());

			compressorPump->Stop(); // Okay, fun's over
		}

		void OperatorControl(void) {
			GetWatchdog().SetEnabled(true); // We do want Watchdog in Teleop, though.
			compressorPump->Start(); // Let's start up the compressor too, while we're at it.

			/* Declare and initialize variables. */
				double doubleGyroPosition;
				double doubleCurrentPosition;
				double doubleLastPosition;

				bool boolGyroForwardButton = false;
				bool boolGyroReverseButton = false;

				float floatAccelPower;

			/* Debug Functionality */
				DriverStationLCD *dsLCD = DriverStationLCD::GetInstance();
				dsLCD->Printf(DriverStationLCD::kUser_Line1, 1, "                 ");
				dsLCD->Printf(DriverStationLCD::kUser_Line2, 1, "                 ");
				dsLCD->Printf(DriverStationLCD::kUser_Line3, 1, "                 ");
				dsLCD->Printf(DriverStationLCD::kUser_Line4, 1, "                 ");
				dsLCD->Printf(DriverStationLCD::kUser_Line5, 1, "                 ");
				dsLCD->Printf(DriverStationLCD::kUser_Line6, 1, "                 ");

			while(IsOperatorControl() && IsEnabled()) {
				GetWatchdog().Feed(); // Feed the Watchdog.
			
				/* Drive Control */
					/* Shifting */
						if(joystickDriveLeft.GetRawButton(DRIVER_SHIFT_BUTTON) || joystickDriveRight.GetRawButton(DRIVER_SHIFT_BUTTON)) solenoidShifter.Set(SOLENOID_SHIFTER_HIGH_POWER_DIRECTION); else solenoidShifter.Set(SOLENOID_SHIFTER_LOW_POWER_DIRECTION);

					/* Gyro Control */
						if(joystickDriveLeft.GetRawButton(DRIVER_GYRO_RESET_BUTTON) && joystickDriveRight.GetRawButton(DRIVER_GYRO_RESET_BUTTON)) {
							gyroDriving.Reset(); // Reset the gyro.
						} else {
							boolGyroForwardButton = joystickDriveLeft.GetRawButton(DRIVER_GYRO_FORWARD_BUTTON) && joystickDriveRight.GetRawButton(DRIVER_GYRO_FORWARD_BUTTON);
							boolGyroReverseButton = joystickDriveLeft.GetRawButton(DRIVER_GYRO_REVERSE_BUTTON) && joystickDriveRight.GetRawButton(DRIVER_GYRO_REVERSE_BUTTON);
							
							doubleCurrentPosition = gyroDriving.GetAngle();
							doubleGyroPosition += doubleCurrentPosition-doubleLastPosition;
							doubleLastPosition = doubleCurrentPosition;
							
							doubleGyroPosition -= timerDriveTimer->Get()*GYRO_DRIFT; // Account for drift.
							timerDriveTimer->Reset();
							
							if(doubleGyroPosition >= 360) doubleGyroPosition -= 360;
							if(doubleGyroPosition < 0) doubleGyroPosition += 360;
							
							if((boolGyroForwardButton || boolGyroReverseButton) && !(boolGyroForwardButton && boolGyroReverseButton)) {
								if(boolGyroForwardButton) {
									if(doubleGyroPosition > 180 && doubleGyroPosition < 358) {
										driveTrain.TankDrive(-GYRO_DRIVE_POWER, GYRO_DRIVE_POWER);
									} else if(doubleGyroPosition > 2 && doubleGyroPosition <= 180) {
										driveTrain.TankDrive(GYRO_DRIVE_POWER, -GYRO_DRIVE_POWER);
									} else {
										driveTrain.TankDrive(0.0, 0.0);
									}
								}
								
								if(boolGyroReverseButton) {
									if(doubleGyroPosition < 178) {
										driveTrain.TankDrive(-GYRO_DRIVE_POWER, GYRO_DRIVE_POWER);
									} else if(doubleGyroPosition > 182) {
										driveTrain.TankDrive(GYRO_DRIVE_POWER, -GYRO_DRIVE_POWER);
									} else {
										driveTrain.TankDrive(0.0, 0.0);
									}
								}
							} else {
								if(joystickDriveLeft.GetRawButton(DRIVER_ACCEL_BALANCE_BUTTON)) {
									/* Accelerometer Balancing */
										floatAccelPower = Deadband(asin(accelBalance.GetAcceleration())) / ACCELEROMETER_UPPER_RADIANS * ACCELEROMETER_DRIVE_POWER;
										driveTrain.TankDrive(floatAccelPower, floatAccelPower);
										dsLcd->Printf(DriverStationLCD::kUser_Line1, 1, "Accel Output: %f", floatAccelPower);
								} else {
									/* Drive Train */
										driveTrain.TankDrive(joystickDriveLeft, joystickDriveRight);
								}
							}
						}

			compressorPump->Stop(); // We're disabling now, so let's switch off the compressor, for safety reasons.
			GetWatchdog().SetEnabled(false); // Teleop is done, so let's turn off Watchdog.
		}
		
		void Disabled(void) {
			GetWatchdog().SetEnabled(false);
			compressorPump->Stop();
		}
	};

START_ROBOT_CLASS(Robot2012Orange);
