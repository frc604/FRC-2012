package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import edu.wpi.first.wpilibj.smartdashboard.PIDDPEditor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterDashboard implements DashboardSection {
    private static final ShooterDashboard instance;
    private static final PIDDPEditor shooterEditor;
    
    public static boolean ignoreHeight = false;
    
    public void enable () {
        if (Robot.speedProvider instanceof AwesomeSpeedController)
            SmartDashboard.putData("Shooter Speed Controller", ShooterDashboard.shooterEditor);
    }
    
    public void render () {
		if (Robot.speedProvider instanceof AwesomeSpeedController) {
            if (ShooterDashboard.shooterEditor != null)
                ShooterDashboard.shooterEditor.update();
            
			((AwesomeSpeedController) Robot.speedProvider).fac = Dashboard.renderDouble("Shooter fac", ((AwesomeSpeedController) Robot.speedProvider).fac);
			((AwesomeSpeedController) Robot.speedProvider).maxSpeed = Dashboard.renderDouble("Shooter maxSpeed", ((AwesomeSpeedController) Robot.speedProvider).maxSpeed);
		}
        
		Robot.encoderShooter.setFac(Dashboard.renderDouble("Shooter Encoder fac", Robot.encoderShooter.getFac()));

        SmartDashboard.putDouble("encoderShooter", Robot.encoderShooter.get());
        SmartDashboard.putDouble("Current Encoder Rate", Robot.encoderShooter.getRate());
        SmartDashboard.putDouble("Current Shooter Output", Robot.shooterMotors.get());
        SmartDashboard.putDouble("Shooter Speed", Robot.shooterMachine.getShooterSpeed());
        
        ShooterDashboard.ignoreHeight = Dashboard.renderBoolean("Shoot Regardless of Height", ShooterDashboard.ignoreHeight);
    }
    
    private ShooterDashboard () {
        
    }
    
    static {
        instance = new ShooterDashboard();
        
        if (Robot.speedProvider instanceof AwesomeSpeedController)
            shooterEditor = new PIDDPEditor(((AwesomeSpeedController) Robot.speedProvider).getController());
        else
            shooterEditor = null;
    }
    
    public static ShooterDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "Shooter";
    }
}
