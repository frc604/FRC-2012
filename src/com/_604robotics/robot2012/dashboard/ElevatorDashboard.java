/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.UpDownPIDEditor;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ElevatorDashboard implements DashboardSection {
    private static final ElevatorDashboard instance;
    private static final UpDownPIDEditor elevatorEditor;
    
    public void enable () {
        SmartDashboard.putData("Elevator PID Controller", ElevatorDashboard.elevatorEditor);
    }
    
    public void render () {
        ElevatorDashboard.elevatorEditor.update();
        
        SmartDashboard.putDouble("encoderElevator", Robot.encoderElevator.get());
        SmartDashboard.putDouble("Current Elevator Setpoint", Robot.pidElevator.getSetpoint());
        SmartDashboard.putDouble("Elevator Output", Robot.pidElevator.get());
    }
    
    private ElevatorDashboard () {
        
    }
    
    static {
        instance = new ElevatorDashboard();
        
        elevatorEditor = new UpDownPIDEditor(Robot.pidElevator);
    }
    
    public static ElevatorDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "Elevator";
    }
}
