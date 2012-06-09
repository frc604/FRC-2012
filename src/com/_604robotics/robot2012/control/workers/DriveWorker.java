package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.PIDConfiguration;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.vision.Target;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DriveWorker implements Worker {

    public void work() {
        /*
         * Handle shifting.
         */
        Robot.solenoidShifter.set(
                (Drive.shifted && !Drive.autoAim)
                ? ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR
                : ActuatorConfiguration.SOLENOID_SHIFTER.LOW_GEAR);

        if (Drive.autoAim) {
            /*
             * Automatic left-right aiming.
             */
            Target target = Robot.cameraInterface.getSingleTarget();

            if (target != null) {
                if (PIDConfiguration.AutoAim.USE_GYRO) {
                   AutoAimer.autoAimer.aim(target);
                } else {
                    Robot.pidSourceDriveAngle.cache((target.getX()) / target.getZ());
                    Robot.pidOutputDrive.setForwardPower(Drive.leftPower);
                    Robot.pidAutoAim.enable();
                }
            } else if (!Robot.pidAutoAim.isEnable()) {
                Robot.driveTrain.tankDrive(0D, 0D);
            }
        } else {
            Robot.pidAutoAim.disable();
            
            //AutoAimer.autoAimer.dontAim();

            /*
             * Manual driving.
             */
            if (Drive.slowed) {
                Robot.driveTrain.tankDrive(Drive.leftPower * ActuatorConfiguration.MAX_SLOW_SPEED, Drive.rightPower * ActuatorConfiguration.MAX_SLOW_SPEED);
            } else {
                Robot.driveTrain.tankDrive(Drive.leftPower, Drive.rightPower);
            }
        }
    }
}
