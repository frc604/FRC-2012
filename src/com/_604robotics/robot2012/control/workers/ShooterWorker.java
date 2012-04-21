package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterWorker implements Worker {
    public void work () {
        /*
         * Drive the hopper manually.
         */
        Robot.hopperMotor.set(Shooter.hopperPower);
        
        /*
         * Set at fender.
         */
        Robot.firingProvider.setAtFender(Shooter.fender);
        
        if (Robot.elevatorMachine.test(ElevatorState.HIGH)) {
            /*
             * Toggle the hood.
             */
            Robot.solenoidShooter.set(
                    (Shooter.hoodUp)
                        ? ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE
                        : ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE
            );

            /*
            * Shoot!
            */
            Robot.shooterMachine.crank(ElevatorMachine.ElevatorState.HIGH);
            Robot.speedProvider.reset();
        }
    }
}
