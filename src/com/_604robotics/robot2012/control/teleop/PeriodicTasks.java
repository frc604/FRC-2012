package com._604robotics.robot2012.control.teleop;

import com._604robotics.robot2012.TheRobot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class PeriodicTasks {
    public static final TheRobot theRobot = TheRobot.theRobot;
    
    public static void processInputs () {
        /*
         * Sample the shooter encoder.
         */
        
        theRobot.encoderShooter.sample();
        
        /*
         * Recalibrate elevator if limit switch depressed.
         */

        if (!theRobot.elevatorLimitSwitch.get()) {
            theRobot.encoderElevator.reset();
        }
    }
    
    public static void processOutputs () {
        /*
         * Make sure the ring light stays on.
         */
        
        theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
    }
    
    public static void reloadActuators () {
        theRobot.speedProvider.reset();

        theRobot.elevatorMotors.reload();
        theRobot.shooterMotors.reload();
        theRobot.hopperMotor.reload();
        theRobot.pickupMotor.reload();

        theRobot.ringLight.reload();

        theRobot.solenoidShifter.reload();
        theRobot.solenoidHopper.reload();
    }
}
