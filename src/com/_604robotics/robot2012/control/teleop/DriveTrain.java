package com._604robotics.robot2012.control.teleop;

import com._604robotics.robot2012.TheRobot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.utils.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DriveTrain {
    public static final TheRobot theRobot = TheRobot.theRobot;
    
    public static void processDrive () {
        DriveTrain.updateShifters();

        if (theRobot.driveController.getButton(ButtonConfiguration.Driver.TINY_FORWARD)) {
            DriveTrain.tinyDrive(1);
        } else if (theRobot.driveController.getButton(ButtonConfiguration.Driver.TINY_REVERSE)) {
            DriveTrain.tinyDrive(-1);
        } else if (theRobot.driveController.getButton(ButtonConfiguration.Driver.SLOW_BUTTON)) {
            DriveTrain.slowDrive();
        } else {
            DriveTrain.manualDrive();
        }
    }
    
    public static void updateShifters () {
        if (theRobot.driveController.getButton(ButtonConfiguration.Driver.SHIFT)) {
            theRobot.solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR);
        } else {
            theRobot.solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.LOW_GEAR);
        }
    }
    
    public static void tinyDrive (int polarity) {
        theRobot.driveTrain.tankDrive(ActuatorConfiguration.TINY_DRIVE_SPEED * polarity, ActuatorConfiguration.TINY_DRIVE_SPEED * polarity);
        SmartDashboard.putString("Drive Mode", "Tiny");
    }
    
    public static void slowDrive () {
        theRobot.driveTrain.tankDrive(theRobot.driveController.getAxis(XboxController.Axis.LEFT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED * -1, theRobot.driveController.getAxis(XboxController.Axis.RIGHT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED * -1);
        SmartDashboard.putString("Drive Mode", "Manual (Slow)");
    }
    
    public static void manualDrive () {
        theRobot.driveTrain.tankDrive(theRobot.driveController.getAxis(XboxController.Axis.LEFT_STICK_Y) * -1, theRobot.driveController.getAxis(XboxController.Axis.RIGHT_STICK_Y) * -1);
        SmartDashboard.putString("Drive Mode", "Manual");
    }
}
