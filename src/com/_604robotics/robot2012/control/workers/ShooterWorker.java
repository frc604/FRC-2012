package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Pickup;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.dashboard.ShooterDashboard;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterWorker implements Worker {
    public void work () {
        /*
         * Sample the encoder.
         */
        Robot.encoderShooter.sample();
        
        /*
         * Drive the hopper manually.
         */
        if (!Pickup.sucking)
            Robot.hopperMotor.set(Shooter.hopperPower);
        
        /*
         * Set at fender.
         */
        Robot.firingProvider.setAtFender(Shooter.fender);
        
        /*
         * Enable and disable vision.
         */
        Robot.firingProvider.setEnabled(Shooter.vision);
        
        if (ShooterDashboard.ignoreHeight || Robot.elevatorMachine.test(ElevatorState.HIGH)) {
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
            if (Shooter.shooting)
                Robot.shooterMachine.crank(ShooterState.SHOOTING);
        }
    }
}
