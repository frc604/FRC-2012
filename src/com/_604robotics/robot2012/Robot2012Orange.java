package com._604robotics.robot2012;

import com._604robotics.robot2012.controlModes.HybridControlMode;
import com._604robotics.robot2012.controlModes.TeleopControlMode;

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

	TeleopControlMode teleop = new TeleopControlMode();
	HybridControlMode hybrid = new HybridControlMode();
	
	
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
	 * Automated drive for autonomous mode.
	 * 
	 * If in middle, drive forward, knock down bridge, turn around.
	 * 
	 * Else, or then, go ahead and try to score.
	 */
	public void autonomous() {
		
		while (isAutonomous() && isEnabled()) {
			hybrid.step();
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
		teleop.init();
		
		while (isOperatorControl() && isEnabled()) {
			
		}
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