package com._604robotics.robot2012.control.modes.teleop;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.configuration.FiringConfiguration;
import com._604robotics.robot2012.control.models.*;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.utils.XboxController.Axis;
import edu.wpi.first.wpilibj.DriverStation;

public class CompetitionControlMode implements ControlMode {
    public void init() {
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        Robot.driveTrain.setSafetyEnabled(true);
        
        Robot.manipulatorController.resetToggles();
        Robot.driveController.resetToggles();

        Robot.pidElevator.reset();
    }

    public boolean step() {
        /*
         * Drive control.
         */
        Drive.shift(Robot.driveController.getButton(ButtonConfiguration.Driver.SHIFT));
        Drive.setSlow(Robot.driveController.getButton(ButtonConfiguration.Driver.SLOW_BUTTON));
        Drive.autoAim(Robot.driveController.getButton(ButtonConfiguration.Driver.AUTO_AIM));
        
        if (Robot.driveController.getButton(ButtonConfiguration.Driver.TINY_FORWARD))
            Drive.bump(1);
        else if (Robot.driveController.getButton(ButtonConfiguration.Driver.TINY_REVERSE))
            Drive.bump(-1);
        else
            Drive.drive(Robot.driveController.getAxis(Axis.LEFT_STICK_Y), Robot.driveController.getAxis(Axis.RIGHT_STICK_Y), true);
        
        /*
         * Stinger control.
         */
        Stinger.put(Robot.driveController.getButton(ButtonConfiguration.Driver.STINGER_DOWN));
        
        /*
         * Elevator control.
         */
        Elevator.toggleDisabled(Robot.driveController.getButton(ButtonConfiguration.Driver.DISABLE_ELEVATOR));
        Elevator.recalibrate(Robot.driveController.getButton(ButtonConfiguration.Driver.CALIBRATE));
        
        if (Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.UP)) {
            Elevator.goUp();
            Pickup.flipUp();
        } else if (Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.DOWN)) {
            Elevator.goDown();
        }
        
        /*
         * Pickup control.
         */
        Pickup.suckIn(Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP));
        Pickup.setSpeed(Robot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
        
        Pickup.toggleFlip(Robot.driveController.getToggle(ButtonConfiguration.Driver.TOGGLE_PICKUP));
        if (!Pickup.up)
            Elevator.goDown();
        
        /*
         * Shooter control.
         */
        Shooter.toggleHood(Robot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE));
        Shooter.shoot(Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT));
        
        if (!FiringConfiguration.TELEOP_AUTO_HOPPER || Math.abs(Robot.manipulatorController.getAxis(Axis.LEFT_STICK_Y)) > 0.2 )
            Shooter.driveHopper(Robot.manipulatorController.getAxis(Axis.LEFT_STICK_Y));
        else
            Shooter.driveHopper(Shooter.isCharged());
        
        if (Robot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_FENDER))
            Shooter.setAtFender();
        else if (Robot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_KEY))
            Shooter.setAtKey();
        
        return true;
    }

    public void disable() {
        Robot.driveTrain.setSafetyEnabled(false);

        Robot.pidElevator.disable();
        Robot.speedProvider.reset();
    }

    public String getName() {
        return "Competition";
    }
}
