package com._604robotics.robot2012;

import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.SmarterDashboard;
import com._604robotics.utils.UpDownPIDController.Gains;
import com._604robotics.utils.XboxController.Axis;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopControlMode extends ControlMode {

    private TheRobot theRobot = TheRobot.theRobot;
    int settleState = 2;
    Timer settleTimer = new Timer();
    Target[] targets;
    Target target;
    AwesomeSpeedController ctrl;	// TODO - make this follow polymorphism correctly...
    boolean upHigh = false;
    boolean pickupIn = true;

    public void step() {
        theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
        theRobot.encoderShooter.sample();

        if (ctrl != null) {
            ctrl.setPIDDP(SmarterDashboard.getDouble("Shooter P", ctrl.getP()), SmarterDashboard.getDouble("Shooter I", ctrl.getI()), SmarterDashboard.getDouble("Shooter D", ctrl.getD()), SmarterDashboard.getDouble("Shooter DP", ctrl.getDP()));
            ctrl.fac = SmarterDashboard.getDouble("Shooter fac", ctrl.fac);
            ctrl.maxSpeed = SmarterDashboard.getDouble("Shooter maxSpeed", ctrl.maxSpeed);
        }

        theRobot.pidElevator.setUpGains(new Gains(SmarterDashboard.getDouble("Elevator Up P", 0.0085), SmarterDashboard.getDouble("Elevator Up I", 0D), SmarterDashboard.getDouble("Elevator Up D", 0.018)));
        theRobot.pidElevator.setDownGains(new Gains(SmarterDashboard.getDouble("Elevator Down P", 0.0029), SmarterDashboard.getDouble("Elevator Down I", 0.000003), SmarterDashboard.getDouble("Elevator Down P", 0.007)));

        if (theRobot.driveController.getToggle(ButtonConfiguration.Driver.DISABLE_ELEVATOR)) {
            theRobot.elevatorMotors.setDisabled(!theRobot.elevatorMotors.getDisabled());
        }

        if (!theRobot.elevatorLimitSwitch.get()) {
            theRobot.encoderElevator.reset();
        }

        if (Math.abs(theRobot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y)) > 0) {
            theRobot.hopperMotor.set(theRobot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
        }

        if (!theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP) && Math.abs(theRobot.manipulatorController.getAxis(Axis.LEFT_STICK_Y)) > 0) {
            theRobot.pickupMotor.set(theRobot.manipulatorController.getAxis(Axis.LEFT_STICK_Y));
        }

        /*
         * Controls the gear shift.
         */

        if (theRobot.driveController.getButton(ButtonConfiguration.Driver.SHIFT)) {
            theRobot.solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR);
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
                        System.out.println("GET READY U GUIZE");
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

        /*
         * Toggles the light.
         */

        if (theRobot.manipulatorController.getButton(ButtonConfiguration.Manipulator.TOGGLE_LIGHT)) {
            theRobot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
        }

        /*
         * Reload the springs.
         */

        theRobot.speedProvider.reset();

        theRobot.elevatorMotors.reload();
        theRobot.shooterMotors.reload();
        theRobot.hopperMotor.reload();
        theRobot.pickupMotor.reload();

        theRobot.ringLight.reload();

        theRobot.solenoidShifter.reload();
        theRobot.solenoidHopper.reload();

        /*
         * Driver assist.
         */

        targets = theRobot.cameraInterface.getTargets();
        target = null;

        for (int i = 0; i < targets.length; i++) {
            if (target == null || targets[i].y < target.y) {
                target = targets[i];
            }
        }

        if (target == null) {
            SmartDashboard.putDouble("Raw X Pos", 999999.999);
        } else {
            SmartDashboard.putDouble("Raw X Pos", target.x);
        }

        /*
         * Debug output.
         */

        SmartDashboard.putDouble("encoderShooter", theRobot.encoderShooter.get());
        SmartDashboard.putDouble("Current Encoder Rate", theRobot.encoderShooter.getRate());
        SmartDashboard.putDouble("Current Shooter Output", theRobot.shooterMotors.get());

        SmartDashboard.putDouble("Shooter Speed", theRobot.shooterMachine.getShooterSpeed());
        SmartDashboard.putBoolean("Using Targets?", theRobot.firingProvider.usingTargets());
        SmartDashboard.putBoolean("At the Fender?", theRobot.firingProvider.isAtFender());

        SmartDashboard.putDouble("gyroHeading", theRobot.gyroHeading.getAngle());

        SmartDashboard.putInt("ups", ((RemoteCameraTCP) theRobot.cameraInterface).getUPS());

        SmartDashboard.putDouble("encoderElevator", theRobot.encoderElevator.get());
        SmartDashboard.putDouble("Current Elevator Setpoint", theRobot.pidElevator.getSetpoint());
        SmartDashboard.putDouble("Elevator Output", theRobot.pidElevator.get());
    }

    public void init() {

        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        theRobot.driveTrain.setSafetyEnabled(true);
        theRobot.compressorPump.start();


        ctrl = (theRobot.speedProvider instanceof AwesomeSpeedController)
                ? ((AwesomeSpeedController) theRobot.speedProvider)
                : null;

        theRobot.manipulatorController.resetToggles();
        theRobot.driveController.resetToggles();

        theRobot.pidElevator.reset();
    }

    public void disable() {
        theRobot.driveTrain.setSafetyEnabled(false);

        theRobot.pidElevator.disable();

        theRobot.compressorPump.stop();

        upHigh = false;
        pickupIn = true;
    }
}
