package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.PickupMachine;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class PickupWorker implements Worker {
    private static int settleState = 2;
    private static Timer settleTimer = new Timer();
    
    public PickupWorker () {
        settleTimer.start();
    }
    
    public void work () {
        if (Elevator.recalibrating || !Elevator.calibrated) {
            /*
             * Firce the pickup down for recalibration.
             */
            Robot.pickupMachine.crank(PickupMachine.PickupState.OUT);
        } else {
            /*
            * Flip the pickup up and down, if the elevator is in a safe position.
            */
            if (Pickup.up) {
                if (Robot.elevatorMachine.test(ElevatorMachine.ElevatorState.PICKUP_OKAY) || Elevator.disabled)
                    Robot.pickupMachine.crank(PickupMachine.PickupState.IN);
            } else {
                if (Robot.pickupMachine.crank(PickupMachine.PickupState.OUT)) {
                    /*
                    * If the pickup is down and the elevator is at rest, then allow
                    * the user to trigger the pickup mechanism.
                    */
                    if (Robot.elevatorMachine.test(ElevatorMachine.ElevatorState.LOW)) {
                        /*
                        * Controls the pickup mechanism.
                        */
                        if (Pickup.sucking) {
                            Robot.pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                            Robot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                            Robot.elevatorMotors.set(ActuatorConfiguration.ELEVATOR_PICKUP_POWER);

                            settleState = 0;
                            settleTimer.stop();
                        }
                    }
                }
            }

            if (!Pickup.sucking) {
                /*
                * Manual pickup control.
                */
                Robot.pickupMotor.set(Pickup.speed);

                /*
                * Settle the balls back in a bit after picking up.
                */
                if (settleState != 2) {
                    if (settleState == 0) {
                        settleTimer.reset();
                        settleTimer.start();

                        settleState = 1;
                    }

                    if (settleTimer.get() < 0.3) {
                        Robot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER_REVERSE);
                    } else {
                        settleTimer.stop();
                        settleState = 2;
                    }
                }
            }
        }
    }
}
