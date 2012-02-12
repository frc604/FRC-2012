#include <nivision.h>
//#include <niimaq.h>
#include <stdlib.h>
#include <math.h>
#include <iostream>
#include <fstream>
#include <string>
#include "WPILib.h"
#include "CameraControl.h"
#include "PotentiometerMonitor.cpp"
#include "TurretOutput.cpp"

using namespace std;

/* Port configuration for sensors and actuators. */
	#define MANIPULATOR_JOYSTICK_PORT 1

	#define LEFT_DRIVE_MOTOR_PORT 8
	#define RIGHT_DRIVE_MOTOR_PORT 4
	
	#define SHOOTER_MOTOR_PORT 3
	#define TURRET_MOTOR_PORT 2
	#define PICKUP_MOTOR_PORT 6
	#define FEED_MOTOR_PORT 7

	#define POTENTIOMETER_PORT 7
	#define GYRO_PORT 1

	#define LIGHT_RELAY_PORT 1

/* Button configuration. */
	

/* Sensor configuration. */
	#define TURRET_POWER -0.4

class VisionTesting : public SimpleRobot {
	Joystick joystickManipulator;
	Victor motorTurret;
	
	AnalogChannel analogPotentiometer;
	Gyro gyro;
	
	Task cameraTask;
	
	PotentiometerMonitor *potentiometerMonitor;
	TurretOutput *turretOutput;
	PIDController *turretController;
	
	Relay relayLight;
	
	public:
		VisionTesting (void):
			joystickManipulator(MANIPULATOR_JOYSTICK_PORT),
			motorTurret(TURRET_MOTOR_PORT),
			analogPotentiometer(POTENTIOMETER_PORT),
			gyro(GYRO_PORT),
			cameraTask("cameraTask", (FUNCPTR)(CameraProcess)),
			relayLight(LIGHT_RELAY_PORT, Relay::kForwardOnly)
		{
			GetWatchdog().SetEnabled(false);
			
			potentiometerMonitor = new PotentiometerMonitor(&analogPotentiometer);
			turretOutput = new TurretOutput(&motorTurret);
			turretController = new PIDController(0.009, 0, 0.0011, potentiometerMonitor, turretOutput);
			turretController->SetSetpoint(90);
		}
				
		void Autonomous (void) {
			GetWatchdog().SetEnabled(false);
		}

		void OperatorControl (void) {
			GetWatchdog().SetEnabled(true);
			GetWatchdog().Feed();
			
			double gyroPosition;
			double currentPosition;
			double lastPosition;
			
			CameraData &cameraData = CameraData::GetInstance();
			
			Timer *runTimer = new Timer();
			runTimer->Start();
			
			Timer *gyroTimer = new Timer();
			gyroTimer->Start();
			
			float turretPower = 0;
			
			bool wasEnabled = false;
			
			ofstream logFile;
			char buffer[128];
			
			sprintf(buffer, "robot-%f.log", Timer::GetFPGATimestamp());
			logFile.open(buffer);
			
			CameraData::GetInstance().enabled = true;
			cameraTask.Start();
			
			bool lightOn = false;
			
			string line;
			ifstream targetFile;
			char *a;
			
			bool wasAuto = false;
			float currPoint = 90;
			
			while (IsOperatorControl() && IsEnabled()) {
				GetWatchdog().Feed();
				
				cameraData.log = joystickManipulator.GetRawButton(5);
				
				if(joystickManipulator.GetRawButton(9)) {
					lightOn = !lightOn;
					if(lightOn)
						relayLight.Set(Relay::kOn);
					else
						relayLight.Set(Relay::kOff);
				}
				
				if(joystickManipulator.GetRawButton(8)) {
					targetFile.open("target.txt");
					if(targetFile.is_open()) {
						while(targetFile.good()) {
							getline(targetFile, line);
							a = new char[line.size() + 1];
							a[line.size()] = 0;
							memcpy(a, line.c_str(), line.size());
						}
					}
					currPoint = atof(a);
					turretController->SetSetpoint(currPoint);
					targetFile.close();
				}
				
				if(joystickManipulator.GetRawButton(2)) {
					wasAuto = true;
					turretController->SetSetpoint(potentiometerMonitor->PIDGet() + cameraData.offsetAngleX);
				} else if(wasAuto) {
					wasAuto = false;
					turretController->SetSetpoint(currPoint);
				}
				
				if(joystickManipulator.GetRawButton(4)) {
					gyroPosition = 0;
					lastPosition = 0;
					runTimer->Reset();
					gyroTimer->Reset();
					gyro.Reset();
				}
				
				currentPosition = gyro.GetAngle();
				gyroPosition += currentPosition - lastPosition;
				lastPosition = currentPosition;
				
				//gyroPosition -= gyroTimer->Get() * 0.0238095238;
				gyroTimer->Reset();
				
				SmartDashboard::GetInstance()->PutDouble("gyroPosition", gyroPosition);
				sprintf(buffer, "[%f] %f\n", runTimer->Get(), gyroPosition);
				logFile << buffer;
				
				if(joystickManipulator.GetRawButton(1)) {
					if(!wasEnabled) {
						turretController->Enable();
						wasEnabled = true;
					} else {
						SmartDashboard::GetInstance()->PutDouble("lastOutput", turretOutput->lastOutput);
					}
				} else {
					if(wasEnabled) {
						turretController->Disable();
						wasEnabled = false;
					} else {
						SmartDashboard::GetInstance()->PutDouble("lastOutput", 0);
					}
					
					turretPower = joystickManipulator.GetX();
					if(turretPower < -0.2 || turretPower > 0.2)
						motorTurret.Set(joystickManipulator.GetX() * TURRET_POWER);
					else
						motorTurret.Set(0);
				}
				
				SmartDashboard::GetInstance()->PutDouble("centerX", cameraData.centerX);
				SmartDashboard::GetInstance()->PutDouble("centerY", cameraData.centerY);
				SmartDashboard::GetInstance()->PutDouble("offsetAngleX", cameraData.offsetAngleX);
				SmartDashboard::GetInstance()->PutDouble("offsetAngleY", cameraData.offsetAngleY);
				SmartDashboard::GetInstance()->PutDouble("frames", CameraData::GetInstance().frames);
				SmartDashboard::GetInstance()->PutDouble("fps", CameraData::GetInstance().fps);
				
				SmartDashboard::GetInstance()->PutDouble("analogPotentiometer", analogPotentiometer.GetVoltage());
				SmartDashboard::GetInstance()->PutDouble("PotentiometerToDegrees", potentiometerMonitor->PIDGet());
				SmartDashboard::GetInstance()->PutDouble("setPoint", turretController->GetSetpoint());
			}
			
			relayLight.Set(Relay::kOff);
			
			logFile.close();
			
			gyroTimer->Stop();
			runTimer->Stop();
			
			CameraData::GetInstance().enabled = false;
			cameraTask.Stop();
			
			GetWatchdog().SetEnabled(false);
		}
		
		void RobotInit(void) {
			printf("Hello, ninja h4X0r.");
		}
		
		void Disabled (void) {
			GetWatchdog().SetEnabled(false);
		}
	};

START_ROBOT_CLASS(VisionTesting);
