package com._604robotics.robot2012.control.teleop;

import com._604robotics.robot2012.control.ControlMode;
import com._604robotics.robot2012.TheRobot;
import com._604robotics.robot2012.configuration.ButtonConfiguration;

import edu.wpi.first.wpilibj.DriverStation;

public class TeleopControlMode extends ControlMode {
    private final TheRobot theRobot = TheRobot.theRobot;

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
        UserInterface.readControllerInputs();
        UserInterface.readConfigFromSmartDashboard();
        
        PeriodicTasks.processInputs();
        PeriodicTasks.processOutputs();
        
        DriveTrain.processDrive();
        
        if (theRobot.driveController.getButton(ButtonConfiguration.Driver.CALIBRATE)) {
            ManipulatorAssembly.recalibrate();
        } else {
            ManipulatorAssembly.manualHopper();
            ManipulatorAssembly.manualShooter();
            
            ManipulatorAssembly.updateElevator();
            ManipulatorAssembly.updatePickup();
            
            ManipulatorAssembly.settleBalls();
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

        UserInterface.resetToggles();
        ManipulatorAssembly.resetSettler();
    }
}
