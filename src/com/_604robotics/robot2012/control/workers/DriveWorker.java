package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.vision.Target;

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
                (Drive.shifted && !Drive.autoAim)
                    ? ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR
                    : ActuatorConfiguration.SOLENOID_SHIFTER.LOW_GEAR
        );
        
        if (Drive.autoAim) {
            /*
             * Automatic left-right aiming.
             */
            Target target = Robot.cameraInterface.getSingleTarget();
            
            if (target != null) {
               Robot.pidSourceDriveAngle.cache(target.getAngle());
               Robot.pidAutoAim.enable();
            }
        } else {
            Robot.pidAutoAim.disable();
            
            /*
             * Manual driving.
             */
            if (Drive.slowed)
                Robot.driveTrain.tankDrive(Drive.leftPower * ActuatorConfiguration.MAX_SLOW_SPEED, Drive.rightPower * ActuatorConfiguration.MAX_SLOW_SPEED);
            else
                Robot.driveTrain.tankDrive(Drive.leftPower, Drive.rightPower);
        }
    }
}
