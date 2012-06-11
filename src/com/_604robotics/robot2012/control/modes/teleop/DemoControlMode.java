package com._604robotics.robot2012.control.modes.teleop;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.models.*;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.robot2012.dashboard.DemoDashboard;
import com._604robotics.robot2012.firing.EncoderSpeedsForDist;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.utils.XboxController.Axis;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class DemoControlMode implements ControlMode {

    private final Timer exTimer = new Timer();

    public void init() {
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        Robot.driveTrain.setSafetyEnabled(true);

        Robot.manipulatorController.resetToggles();
        Robot.driveController.resetToggles();

        Robot.pidElevator.reset();
        
        Robot.manipulatorController.setDeadband(Axis.LEFT_STICK_Y, -0.2, 0.2);
        Robot.manipulatorController.setDeadband(Axis.RIGHT_STICK_Y, -0.2, 0.2);

        exTimer.reset();
        exTimer.start();

        Shooter.setManual(false);
        EncoderSpeedsForDist.setUseDemoHeight();
    }

    public boolean step() {
        /*
         * Drive control.
         */
        Drive.drive(Robot.driveController.getAxis(Axis.LEFT_STICK_Y) * DemoDashboard.driveSpeedMultiplier, Robot.driveController.getAxis(Axis.RIGHT_STICK_Y) * DemoDashboard.driveSpeedMultiplier, true);
        Drive.autoAim(Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Drive.AUTO_AIM));

        /*
         * Elevator control.
         */
        if (Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Elevator.UP)) {
            Elevator.goUp();
            Pickup.flipUp();
        } else if (Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Elevator.DOWN)) {
            Elevator.goDown();
        }

        /*
         * Shooter control.
         */
        Shooter.setManualSpeed(DemoDashboard.manualShooterSpeed);
        Shooter.setManual(Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Shooter.DISABLE_VISION));

        double hopperSpeed = 0D;

        if (Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Shooter.SUCK_IN)) {
            hopperSpeed = ActuatorConfiguration.HOPPER_POWER;
        }
        if (Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Shooter.SPIT_OUT)) {
            hopperSpeed = ActuatorConfiguration.HOPPER_POWER_REVERSE;
        }

        if (Robot.manipulatorController.getButton(ButtonConfiguration.Demo.Student.SHOOT)) {
            Elevator.goUp();
            Pickup.flipUp();

            if (Robot.elevatorMachine.test(ElevatorState.HIGH)) {
                Shooter.shoot();
                
                if (Shooter.isCharged())
                    Shooter.driveHopper();
                else
                    Shooter.driveHopper(hopperSpeed);
            } else {
                Shooter.shoot(false);
                Shooter.driveHopper(hopperSpeed);
            }
        } else {
            Shooter.shoot(false);
            Shooter.driveHopper(hopperSpeed);
        }

        Shooter.toggleHood(Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Shooter.TOGGLE_ANGLE));
        
        //Shooter.setManualSpeed(Shooter.manualSpeed + Robot.manipulatorController.getAxis(Axis.LEFT_STICK_Y) * -0.6);
        //Shooter.setManualSpeed(Shooter.manualSpeed + Robot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y) * -0.1);
        //System.out.println(Shooter.manualSpeed);

        /*
         * Pickup control.
         */
        Pickup.suckIn(Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Pickup.SUCK_IN));
        
        if (Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Pickup.GO_DOWN)) {
            Elevator.goDown();
            Pickup.flipDown();
        }
        
        if (Robot.driveController.getButton(ButtonConfiguration.Demo.Mentor.Pickup.GO_UP)) {
            Elevator.goDown();
            Pickup.flipUp();
        }

        return true;
    }

    public void disable() {
        Robot.driveTrain.setSafetyEnabled(false);

        Robot.pidElevator.disable();
        Robot.speedProvider.reset();

        Shooter.setManual(false);
        EncoderSpeedsForDist.setUseDemoHeight(false);
    }

    public String getName() {
        return "Demo";
    }
}
