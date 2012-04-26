package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.FiringConfiguration;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import com._604robotics.robot2012.speedcontrol.BangBangSpeedController;
import com._604robotics.utils.SmarterDashboard;
import edu.wpi.first.wpilibj.smartdashboard.PIDDPEditor;
import edu.wpi.first.wpilibj.smartdashboard.SendableTag;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterDashboard implements DashboardSection {
    private static final ShooterDashboard instance;
    private static final PIDDPEditor shooterEditor;
    private static final SendableTag tag;
    
    public static boolean ignoreHeight = false;
    public static boolean useManualSetpoint = false;
    
    public static double tolerance = FiringConfiguration.SPEED_TOLERANCE;
    
    public void enable () {
        SmartDashboard.putData(tag);
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
        
        if (Robot.speedProvider instanceof BangBangSpeedController) {
            ((BangBangSpeedController) Robot.speedProvider).lowPower = Dashboard.renderDouble("Shooter lowPower", ((BangBangSpeedController) Robot.speedProvider).lowPower);
            ((BangBangSpeedController) Robot.speedProvider).spinupSpeed = Dashboard.renderDouble("Shooter spinupSpeed", ((BangBangSpeedController) Robot.speedProvider).spinupSpeed);
        }
        
		Robot.encoderShooter.setFac(Dashboard.renderDouble("Shooter Encoder fac", Robot.encoderShooter.getFac()));
        ShooterDashboard.tolerance = Dashboard.renderDouble("Shooter tolerance", ShooterDashboard.tolerance);

        ShooterDashboard.ignoreHeight = Dashboard.renderBoolean("Shoot Regardless of Height", ShooterDashboard.ignoreHeight);
        ShooterDashboard.useManualSetpoint = Dashboard.renderBoolean("Use Manual Shooter Setpoint", ShooterDashboard.useManualSetpoint);
        
        // TODO: Seriously, clean this up.
        
        Shooter.setManual(ShooterDashboard.useManualSetpoint);
        if (ShooterDashboard.useManualSetpoint)
            Shooter.setManualSpeed(SmarterDashboard.getDouble("Shooter Speed", 0D));
        
        SmartDashboard.putDouble("encoderShooter", Robot.encoderShooter.get());
        SmartDashboard.putDouble("Current Encoder Rate", Robot.encoderShooter.getRate());
        SmartDashboard.putDouble("Current Shooter Output", Robot.shooterMotors.get());
        
        if (!Shooter.manual)
            SmartDashboard.putDouble("Shooter Speed", Robot.firingProvider.getSpeed());
    }
    
    private ShooterDashboard () {
        
    }
    
    static {
        instance = new ShooterDashboard();
        
        if (Robot.speedProvider instanceof AwesomeSpeedController)
            shooterEditor = new PIDDPEditor(((AwesomeSpeedController) Robot.speedProvider).getController());
        else
            shooterEditor = null;
        
        tag = new SendableTag("Shooter", new String[] {
            "Shooter Speed Controller",
            "encoderShooter",
            "Current Encoder Rate",
            "Current Shooter Output",
            "Shooter Speed",
            "Shoot Regardless of Height",
            "Shooter Encoder fac",
            "Shooter fac",
            "Shooter maxSpeed"
        });
    }
    
    public static ShooterDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "Shooter";
    }
}
