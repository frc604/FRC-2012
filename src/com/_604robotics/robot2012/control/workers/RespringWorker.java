package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class RespringWorker implements Worker {
    public void work () {
        /*
         * Reload the springs.
         */
        Robot.speedProvider.reset();

        Robot.elevatorMotors.reload();
        Robot.shooterMotors.reload();
        Robot.hopperMotor.reload();
        Robot.pickupMotor.reload();

        Robot.ringLight.reload();

        Robot.solenoidShifter.reload();
        Robot.solenoidHopper.reload();
    }
}
