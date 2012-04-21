package com._604robotics.robot2012;

import com._604robotics.robot2012.control.ControlMode;
import com._604robotics.robot2012.control.LearningControlMode;
import com._604robotics.robot2012.control.hybrid.HybridControlMode;
import com._604robotics.robot2012.control.teleop.TeleopControlMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
	TheRobot theRobot = TheRobot.theRobot;

	ControlMode teleop = new TeleopControlMode();
	ControlMode learning = new LearningControlMode();
	ControlMode hybrid = new HybridControlMode();
	
	SendableChooser teleopMode;
	
	/**
	 * Constructor.
	 */
	public Robot2012Orange () {
        /* Initialize calibration signals. */
        
		DriverStation.getInstance().setDigitalOut(2, false);
		DriverStation.getInstance().setDigitalOut(5, false);
		
		/* Initialize mode selector. */
		
		teleopMode = new SendableChooser();
		teleopMode.addDefault("Teleop Mode: Competition", teleop);
		teleopMode.addObject("Teleop Mode: Learning", learning);
		
		SmartDashboard.putData("teleopMode", teleopMode);
		
        /* Ditch the built-in Watchdog. */
        
		this.getWatchdog().setEnabled(false);
	}
	
	/**
	 * Initializes the robot on startup.
	 */
	public void robotInit () {
		TheRobot.init();
		System.out.println("All done booting!");
	}
	
	/**
	 * Automated drive for autonomous mode.
	 */
	public void autonomous() {
        hybrid.init();
        
		while (isAutonomous() && isEnabled()) {
			hybrid.step();
        }
        
        hybrid.disable();
	}
	
	
	/**
	 * Operator-controlled drive for Teleop mode.
	 */
	public void operatorControl() {
        ControlMode mode = (ControlMode) teleopMode.getSelected();
        
		mode.init();
		
		while (isOperatorControl() && isEnabled()) {
            mode.step();
        }
        
        mode.disable();
	}
	
	/**
	 * Disabled mode processing.
	 */
	public void disabled() {
		theRobot.compressorPump.stop();
		theRobot.driveTrain.setSafetyEnabled(false);
        
		Timer lastRecalibrated = new Timer();
		lastRecalibrated.start();
		
		while (!isEnabled()) {
			if (!theRobot.elevatorLimitSwitch.get()) {
				DriverStation.getInstance().setDigitalOut(5, true);
				SmartDashboard.getBoolean("Elevator Calibrated", true);
				if (lastRecalibrated.get() >= 1)
					theRobot.encoderElevator.reset();
				else
					lastRecalibrated.reset();
			}
		}
	}
}