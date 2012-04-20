package com._604robotics.robot2012.control.teleop;

import com._604robotics.robot2012.TheRobot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.ControlMode;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.utils.XboxController.Axis;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopControlMode extends ControlMode {
    private final TheRobot theRobot = TheRobot.theRobot;
    
    int settleState = 2;
    Timer settleTimer = new Timer();
    
    boolean upHigh = false;
    boolean pickupIn = true;

    public void init() {
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        theRobot.driveTrain.setSafetyEnabled(true);
        theRobot.compressorPump.start();
        
        theRobot.manipulatorController.resetToggles();
        theRobot.driveController.resetToggles();

        theRobot.pidElevator.reset();
    }

    public boolean step() {
        UserInterface.readConfigFromSmartDashboard();
        
        PeriodicTasks.processInputs();
        PeriodicTasks.processOutputs();
        
        /*
         * Disable/re-eneable the elevator. For emergency usage.
         */
        
        if (theRobot.driveController.getToggle(ButtonConfiguration.Driver.DISABLE_ELEVATOR)) {
            theRobot.elevatorMotors.setDisabled(!theRobot.elevatorMotors.getDisabled());
        }

        if (Math.abs(theRobot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y)) > 0) {
            theRobot.hopperMotor.set(theRobot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
        }

        if (!theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP) && Math.abs(theRobot.manipulatorController.getAxis(Axis.LEFT_STICK_Y)) > 0) {
            theRobot.pickupMotor.set(theRobot.manipulatorController.getAxis(Axis.LEFT_STICK_Y));
        }

        /*
         * Drive train controls.
         */

        if (theRobot.driveController.getButton(ButtonConfiguration.Driver.TINY_FORWARD)) {
            theRobot.driveTrain.tankDrive(ActuatorConfiguration.TINY_FORWARD_SPEED, ActuatorConfiguration.TINY_FORWARD_SPEED);
            SmartDashboard.putString("Drive Mode", "Tiny (Forward)");
        } else if (theRobot.driveController.getButton(ButtonConfiguration.Driver.TINY_REVERSE)) {
            theRobot.driveTrain.tankDrive(ActuatorConfiguration.TINY_REVERSE_SPEED, ActuatorConfiguration.TINY_REVERSE_SPEED);
            SmartDashboard.putString("Drive Mode", "Tiny (Reverse)");
        } else if (theRobot.driveController.getButton(ButtonConfiguration.Driver.SLOW_BUTTON)) {
            theRobot.driveTrain.tankDrive(theRobot.driveController.getAxis(Axis.LEFT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED * -1, theRobot.driveController.getAxis(Axis.RIGHT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED * -1);
            SmartDashboard.putString("Drive Mode", "Manual (Slow)");
        } else {
            theRobot.driveTrain.tankDrive(theRobot.driveController.getAxis(Axis.LEFT_STICK_Y) * -1, theRobot.driveController.getAxis(Axis.RIGHT_STICK_Y) * -1);
            SmartDashboard.putString("Drive Mode", "Manual");
        }

        /*
         * Manually set whether or not we're at the fender.
         */

        if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_FENDER)) {
            theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            if (theRobot.elevatorMachine.test(ElevatorState.HIGH)) {
                theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            }
            theRobot.firingProvider.setAtFender(true);
        } else if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_KEY)) {
            theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
            if (theRobot.elevatorMachine.test(ElevatorState.HIGH)) {
                theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
            }
            theRobot.firingProvider.setAtFender(false);
        }

        /*
         * Toggle the "default" height between "up high" and "down low".
         */

        if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.UP)) {
            upHigh = true;
            pickupIn = true;
        } else if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.DOWN)) {
            upHigh = false;
        }

        /*
         * Toggle the pickup state between "up" and "down".
         */

        if (theRobot.driveController.getToggle(ButtonConfiguration.Driver.TOGGLE_PICKUP)) {
            pickupIn = !pickupIn;
            if (pickupIn == false) {
                upHigh = false;
            }
        }
        
        SmartDashboard.putBoolean("upHigh", upHigh);
        SmartDashboard.putBoolean("pickupIn", pickupIn);

        /*
         * If upHigh is true, then raise the elevator. If pickupIn is true, then
         * make sure the elevator is up high enough, and lift up the pickup.
         *
         * Else, check the "default" position. If it is up high, then lower the
         * pickup and raise the elevator simultaneously. If it is down low, then
         * make sure the pickup is down first, and then lower the elevator.
         */

        if (theRobot.driveController.getButton(ButtonConfiguration.Driver.CALIBRATE)) {
            if (theRobot.pickupMachine.crank(PickupState.OUT)) {
                theRobot.elevatorMotors.setDisabled(false);
                theRobot.elevatorMotors.set(-0.4);
            }
        } else {
            if (upHigh) {
                if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT) || theRobot.elevatorMachine.crank(ElevatorState.HIGH)) {
                    SmartDashboard.putString("Ready to Shoot", "Yes");

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

                    if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT)) {
                        ///System.out.println("GET READY U GUIZE");
                        theRobot.shooterMachine.crank(ShooterState.SHOOTING);
                    }
                }
            } else {
                if (pickupIn) {
                    theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
                } else if (theRobot.pickupMachine.test(PickupState.OUT)) {
                    theRobot.elevatorMachine.crank(ElevatorState.LOW);
                }
            }

            if (pickupIn) {
                if (theRobot.elevatorMachine.test(ElevatorState.PICKUP_OKAY) || theRobot.elevatorMotors.getDisabled()) {
                    theRobot.pickupMachine.crank(PickupState.IN);
                }
            } else {
                if (theRobot.pickupMachine.crank(PickupState.OUT)) {
                    /*
                     * If the pickup is down and the elevator is at rest, then
                     * allow the user to trigger the pickup mechanism.
                     */

                    if (theRobot.elevatorMachine.test(ElevatorState.LOW)) {
                        /*
                         * Controls the pickup mechanism.
                         */

                        if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP)) {
                            theRobot.pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                            theRobot.hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                            theRobot.elevatorMotors.set(ActuatorConfiguration.ELEVATOR_PICKUP_POWER);

                            settleState = 0;
                            settleTimer.stop();
                        }
                    }
                }
            }
        }

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

        UserInterface.writeDebugInformation();
        UserInterface.updateDriverAssist();
        
        PeriodicTasks.reloadActuators();

        return true;
    }

    public void disable() {
        
        theRobot.driveTrain.setSafetyEnabled(false);

        theRobot.pidElevator.disable();

        theRobot.compressorPump.stop();

        upHigh = false;
        pickupIn = true;
    }
}
