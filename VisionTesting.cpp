#include <nivision.h>
//#include <niimaq.h>
#include "WPILib.h"
#include "ImageProcessing.cpp"
#include "CameraData.cpp"

/* Port configuration for sensors and actuators. */
	#define MANIPULATOR_JOYSTICK_PORT 1

	#define LEFT_DRIVE_MOTOR_PORT 8
	#define RIGHT_DRIVE_MOTOR_PORT 4
	
	#define SHOOTER_MOTOR_PORT 3
	#define TURRET_MOTOR_PORT 2
	#define PICKUP_MOTOR_PORT 6
	#define FEED_MOTOR_PORT 7

	#define POTENTIOMETER_PORT 7

/* Button configuration. */
	

/* Actuator polarity and speed configuration. */
	#define TURRET_POWER 0.4

/* Sensor configuration. */
	#define POTENTIOMETER_LEFT_VOLTAGE 1.7
	#define POTENTIOMETER_RIGHT_VOLTAGE 0.1

class VisionTesting : public SimpleRobot {
	Joystick joystickManipulator;
	Victor motorTurret;
	AnalogChannel analogPotentiometer;
	
	Task cameraTask;
	
	public:
		VisionTesting (void):
			joystickManipulator(MANIPULATOR_JOYSTICK_PORT),
			motorTurret(TURRET_MOTOR_PORT),
			analogPotentiometer(POTENTIOMETER_PORT),
			cameraTask("cameraTask", (FUNCPTR)(CameraProcess))
		{
			GetWatchdog().SetEnabled(false);
		}
		
		static int CameraProcess() {
			// TODO: Test.
			// TODO: Add error handling.
			
			Image *image;
			IVA_Data *imageData;
			IVA_Result *particleResults;
			int targets;
			
			double currentX = 0.0;
			double currentY = 0.0;
			
			double centerX = 0.0;
			double centerY = 0.0;
			
			Timer *second = new Timer();
			int fps = 0;
			
			int totalFrames = 0;
			
			second->Start();
			
			while(CameraData::GetInstance().enabled) {
				if(AxisCamera::GetInstance().IsFreshImage()) {
					AxisCamera::GetInstance().GetImage(image);
					imageData = IVA_ProcessImage(image);
					particleResults = imageData->stepResults[9].results;
					
					targets = (int) particleResults->resultVal.numVal;
					particleResults++;
					
					for(int i = 0; i < targets; i++) {
						currentX = particleResults->resultVal.numVal;
						particleResults++;
						currentY = particleResults->resultVal.numVal;
						particleResults++;
						
						if(currentY > centerY) {
							centerX = currentX;
							centerY = currentY;
						}
					}
					
					IVA_DisposeData(imageData);
					
					totalFrames++;
					
					CameraData::GetInstance().centerX = centerX;
					CameraData::GetInstance().centerY = centerY;
					
					CameraData::GetInstance().frames = totalFrames;
					
					if(second->Get() >= 1) {
						CameraData::GetInstance().fps = fps;
						
						fps = 0;
						second->Reset();
					}
					
					fps++;
				}
			}
			
			second->Stop();
			
			return 0;
		}

		float PotentiometerToDegrees(float voltage) {
			return (voltage - POTENTIOMETER_RIGHT_VOLTAGE) / (POTENTIOMETER_LEFT_VOLTAGE - POTENTIOMETER_RIGHT_VOLTAGE) * 180; 
		}
		
		float CameraXToDegrees(float cameraX) {
			return 90; // TODO: Implement this.
		}
		
		float CameraYToDegrees(float cameraY) {
			return 90; // TODO: Implement this.
		}
		
		void Autonomous (void) {
			GetWatchdog().SetEnabled(false);
		}

		void OperatorControl (void) {
			GetWatchdog().SetEnabled(true);
			
			float turretPower;

			CameraData::GetInstance().enabled = true;
			cameraTask.Start();
			
			while (IsOperatorControl() && IsEnabled()) {
				GetWatchdog().Feed();
				
				turretPower = joystickManipulator.GetY();
				
				if(turretPower > 0.2 || turretPower < -0.2)
					motorTurret.Set(joystickManipulator.GetY() * TURRET_POWER);
				else
					motorTurret.Set(0);
				
				SmartDashboard::GetInstance()->PutDouble("centerX", CameraData::GetInstance().centerX);
				SmartDashboard::GetInstance()->PutDouble("centerY", CameraData::GetInstance().centerY);
				SmartDashboard::GetInstance()->PutDouble("frames", CameraData::GetInstance().frames);
				SmartDashboard::GetInstance()->PutDouble("fps", CameraData::GetInstance().fps);
				
				SmartDashboard::GetInstance()->PutDouble("analogPotentiometer", analogPotentiometer.GetVoltage());
				SmartDashboard::GetInstance()->PutDouble("PotentiometerToDegrees", PotentiometerToDegrees(analogPotentiometer.GetVoltage()));
			}
			
			CameraData::GetInstance().enabled = false;
			cameraTask.Stop();
			
			GetWatchdog().SetEnabled(false);
		}
		
		void Disabled (void) {
			GetWatchdog().SetEnabled(false);
		}
	};

START_ROBOT_CLASS(VisionTesting);
