package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Drive;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DriveWorker implements Worker {
    public void work () {
        /*
         * Handle shifting.
         */
        Robot.solenoidShifter.set(
                (Drive.shifted)
                    ? ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR
                    : ActuatorConfiguration.SOLENOID_SHIFTER.LOW_GEAR
        );
        
        /*
         * Handle driving.
         */
        if (Drive.slowed)
            Robot.driveTrain.tankDrive(Drive.leftPower * ActuatorConfiguration.MAX_SLOW_SPEED, Drive.rightPower * ActuatorConfiguration.MAX_SLOW_SPEED);
        else
            Robot.driveTrain.tankDrive(Drive.leftPower, Drive.rightPower);
    }
}
