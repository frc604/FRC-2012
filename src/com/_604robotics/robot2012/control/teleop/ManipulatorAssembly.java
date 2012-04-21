package com._604robotics.robot2012.control.teleop;

import com._604robotics.robot2012.TheRobot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.PickupMachine;
import com._604robotics.robot2012.machine.ShooterMachine;
import com._604robotics.utils.XboxController;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ManipulatorAssembly {
    public static final TheRobot theRobot = TheRobot.theRobot;
    
    private static int settleState = 2;
    private static Timer settleTimer = new Timer();
    
    public static void recalibrate () {
        if (theRobot.pickupMachine.crank(PickupMachine.PickupState.OUT)) {
            theRobot.elevatorMotors.setDisabled(false);
            theRobot.elevatorMotors.set(-0.4);
        }
    }
    
    public static void manualHopper () {
        if (Math.abs(theRobot.manipulatorController.getAxis(XboxController.Axis.RIGHT_STICK_Y)) > 0) {
            theRobot.hopperMotor.set(theRobot.manipulatorController.getAxis(XboxController.Axis.RIGHT_STICK_Y));
        }
    }
    
    public static void manualShooter () {
        if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT)) {
            theRobot.shooterMachine.crank(ShooterMachine.ShooterState.SHOOTING);
        }
    }
    
    public static void updateElevator () {   
        /*
         * Disable/re-eneable the elevator. For emergency usage.
         */
        
        if (theRobot.driveController.getToggle(ButtonConfiguration.Driver.DISABLE_ELEVATOR)) {
            theRobot.elevatorMotors.setDisabled(!theRobot.elevatorMotors.getDisabled());
        }
        
        /*
         * If upHigh is true, then raise the elevator. If pickupIn is true, then
         * make sure the elevator is up high enough, and lift up the pickup.
         *
         * Else, check the "default" position. If it is up high, then lower the
         * pickup and raise the elevator simultaneously. If it is down low, then
         * make sure the pickup is down first, and then lower the elevator.
         */
        
        if (UserInterface.upHigh) {
            if (theRobot.elevatorMachine.crank(ElevatorMachine.ElevatorState.HIGH)) {
                /*
                 * Toggles the shooter angle.
                 */

                if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
                    if (theRobot.solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE) {
                        theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
                    } else {
                        theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
                    }
                }
            }
        } else {
            if (UserInterface.pickupIn) {
                theRobot.elevatorMachine.crank(ElevatorMachine.ElevatorState.MEDIUM);
            } else if (theRobot.pickupMachine.test(PickupMachine.PickupState.OUT)) {
                theRobot.elevatorMachine.crank(ElevatorMachine.ElevatorState.LOW);
            }
        }
    }
    
    public static void updatePickup () {
        if (UserInterface.pickupIn) {
            if (theRobot.elevatorMachine.test(ElevatorMachine.ElevatorState.PICKUP_OKAY) || theRobot.elevatorMotors.getDisabled()) {
                theRobot.pickupMachine.crank(PickupMachine.PickupState.IN);
            }
        } else {
            if (theRobot.pickupMachine.crank(PickupMachine.PickupState.OUT)) {
                /*
                 * If the pickup is down and the elevator is at rest, then allow
                 * the user to trigger the pickup mechanism.
                 */

                if (theRobot.elevatorMachine.test(ElevatorMachine.ElevatorState.LOW)) {
                    /*
                     * Controls the pickup mechanism.
                     */

                    if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP)) {
                        theRobot.pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                        theRobot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                        theRobot.elevatorMotors.set(ActuatorConfiguration.ELEVATOR_PICKUP_POWER);

                        settleState = 0;
                        settleTimer.stop();
                    } else if (Math.abs(theRobot.manipulatorController.getAxis(XboxController.Axis.LEFT_STICK_Y)) > 0) {
                        theRobot.pickupMotor.set(theRobot.manipulatorController.getAxis(XboxController.Axis.LEFT_STICK_Y));
                    }
                }
            }
        }
    }
    
    public static void settleBalls () {
        /*
         * Settle the balls back in a bit after picking up.
         */

        if (!theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP) && settleState != 2) {
            if (settleState == 0) {
                settleTimer.reset();
                settleTimer.start();

                settleState = 1;
            }

            if (settleTimer.get() < 0.3) {
                theRobot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER_REVERSE);
            } else {
                settleTimer.stop();

                settleState = 2;
            }
        }
    }
    
    public static void resetSettler () {
        settleState = 2;
        settleTimer.reset();
    }
}
