package com._604robotics.robot2012.cameraservo;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.cameraservo.configuration.PortConfiguration;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraServo extends SimpleRobot {
    Victor turret;
    
    Relay light;
    
    CameraInterface cameraInterface = new RemoteCameraTCP();
    
    public CameraServo () {
        this.getWatchdog().setEnabled(false);
    }
    
    public void robotInit () {
        turret = new Victor(PortConfiguration.TURRET_MOTOR_PORT);
        
        light = new Relay(PortConfiguration.LIGHT_RELAY_PORT, Relay.Direction.kForward);
        light.set(Relay.Value.kOn);
        
        SmartDashboard.putDouble("P", SmartDashboard.getDouble("P", -0.75));
        SmartDashboard.putDouble("I", SmartDashboard.getDouble("I", -0.02));
        SmartDashboard.putDouble("D", SmartDashboard.getDouble("D", -1.2));
        
        System.out.println("Hello, ninja h4X0r.");
    }

    public void autonomous () {
        
    }

    public void operatorControl () {
        cameraInterface.begin();
        
        CameraInput cameraInput = new CameraInput(cameraInterface);
        TurretOutput turretOutput = new TurretOutput(turret);
        PIDController turretController = new PIDController(SmartDashboard.getDouble("P", 0D), SmartDashboard.getDouble("I", 0D), SmartDashboard.getDouble("D", 0D), cameraInput, turretOutput);
        
        turretController.setOutputRange(-0.6D, 0.6D);
        turretController.enable();
        
        while (this.isOperatorControl() && this.isEnabled())
            Timer.delay(1);
        
        turretController.disable();
        
        cameraInterface.end();
    }
    
    public void disabled () {
        
    }
}