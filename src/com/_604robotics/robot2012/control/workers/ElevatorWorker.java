package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.PickupMachine;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ElevatorWorker implements Worker {
    public void work () {
        /*
         * Recalibrate elevator if limit switch depressed.
         */
        Robot.tryCalibrateElevator();
        
        /*
         * Handle disabled state.
         */
        Robot.elevatorMotors.setDisabled(Elevator.disabled);
        
        if (Elevator.recalibrating || !Elevator.calibrated) {
            /*
             * If the pickup is out, force the elevator down for recalibration.
             */
            if (Robot.pickupMachine.test(PickupMachine.PickupState.OUT)) {
                Robot.elevatorMotors.setDisabled(false);
                Robot.elevatorMotors.set(-0.4);
            }
        } else {
            /*
             * If we're going up high, well, go there.
             * 
             * If we're going down low, and the pickup should be in, go to the
             * medium position.
             * 
             * If we're going down low, and the pickup IS out, go to the low
             * position.
             */
            if (Elevator.high) {
                if (Robot.elevatorMachine.crank(ElevatorMachine.ElevatorState.HIGH)) {
                    /*
                    * Allow the shooter angle to be toggled.
                    */
                    Robot.solenoidShooter.set(
                            (Shooter.hoodUp)
                                ? ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE
                                : ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE
                    );
                }
            } else {
                if (Pickup.up)
                    Robot.elevatorMachine.crank(ElevatorMachine.ElevatorState.MEDIUM);
                else if (Robot.pickupMachine.test(PickupState.OUT))
                    Robot.elevatorMachine.crank(ElevatorMachine.ElevatorState.LOW);
            }
        }
    }
}
