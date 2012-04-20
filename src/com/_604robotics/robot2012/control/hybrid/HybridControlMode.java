package com._604robotics.robot2012.control.hybrid;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.ControlMode;
import com._604robotics.utils.SmarterDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class HybridControlMode extends ControlMode {
    private final AutonControlMode auton = new AutonControlMode();
	private final KinectControlMode kinectMode = new KinectControlMode(this);
    
	private boolean kinect = false;
	private boolean abort = false;
    
    private boolean autonRunning = true;
	private boolean kinectInitted = false;
	
	public boolean step() {
		kinect = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
		abort = theRobot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
		
		if (abort) {
			theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
			return false;
		}
        
        theRobot.encoderShooter.sample();
		
		if (!kinect) {
            if (autonRunning)
    			autonRunning = auton.step();
            else
				theRobot.speedProvider.reset();
            this.resetMotors();
		} else {
			if(!kinectInitted) {
				
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
			
			if(!kinectMode.step())
				return false;
		}
        
        return true;
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
