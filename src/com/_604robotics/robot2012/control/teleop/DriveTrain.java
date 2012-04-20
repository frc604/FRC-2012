package com._604robotics.robot2012.control.teleop;

import com._604robotics.robot2012.TheRobot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DriveTrain {
    public static final TheRobot theRobot = TheRobot.theRobot;
    
    public static void updateShifters () {
        if (theRobot.driveController.getButton(ButtonConfiguration.Driver.SHIFT)) {
            theRobot.solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR);
        } else {
            theRobot.solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.LOW_GEAR);
        }
    }
}
