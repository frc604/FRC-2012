package com._604robotics.robot2012.control.modes.teleop;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.utils.XboxController.Axis;
import edu.wpi.first.wpilibj.DriverStation;

public class CompetitionControlMode implements ControlMode {
    public void init() {
        DriverStation.getInstance().setDigitalOut(2, false);
        DriverStation.getInstance().setDigitalOut(5, false);

        Robot.driveTrain.setSafetyEnabled(true);
        Robot.compressorPump.start();
        
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
        
        if (Robot.driveController.getButton(ButtonConfiguration.Driver.TINY_FORWARD))
            Drive.bump(1);
        else if (Robot.driveController.getButton(ButtonConfiguration.Driver.TINY_REVERSE))
            Drive.bump(-1);
        else
            Drive.drive(Robot.driveController.getAxis(Axis.LEFT_STICK_Y), Robot.driveController.getAxis(Axis.RIGHT_STICK_Y), true);
        
        /*
         * Elevator control.
         */
        Elevator.toggleDisabled(Robot.driveController.getButton(ButtonConfiguration.Driver.DISABLE_ELEVATOR));
        Elevator.recalibrate(Robot.driveController.getButton(ButtonConfiguration.Driver.CALIBRATE));
        
        if (Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.UP))
            Elevator.goUp();
        else if (Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.DOWN))
            Elevator.goDown();
        
        /*
         * Pickup control.
         */
        Pickup.suckIn(Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP));
        Pickup.setSpeed(Robot.manipulatorController.getAxis(Axis.LEFT_STICK_Y));
        
        Pickup.toggleFlip(Robot.driveController.getToggle(ButtonConfiguration.Driver.TOGGLE_PICKUP));
        
        /*
         * Shooter control.
         */
        Shooter.toggleHood(Robot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE));
        Shooter.shoot(Robot.manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT));
        Shooter.driveHopper(Robot.manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
        
        if (Robot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_FENDER))
            Shooter.setAtFender();
        else if (Robot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_KEY))
            Shooter.setAtKey();
        
        return true;
    }

    public void disable() {
        Robot.driveTrain.setSafetyEnabled(false);

        Robot.pidElevator.disable();
        Robot.compressorPump.stop();
    }

    public String getName() {
        return "Competition";
    }
}
