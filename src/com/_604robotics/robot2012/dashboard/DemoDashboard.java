package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.control.models.Shooter;
import edu.wpi.first.wpilibj.smartdashboard.SendableTag;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DemoDashboard implements DashboardSection {
    private static final DemoDashboard instance;
    private static final SendableTag tag;
    
    public static double driveSpeedMultiplier = 1D;
    public static double manualShooterSpeed = 250D;
    
    public void enable () {
        SmartDashboard.putData(tag);
    }
    
    public void render () {
        driveSpeedMultiplier = Dashboard.renderDouble("Demo: Drive Speed Multiplier", driveSpeedMultiplier);
        manualShooterSpeed = Dashboard.renderDouble("Demo: Manual Shooter Speed", manualShooterSpeed);
    }
    
    private DemoDashboard () {
        
    }
    
    static {
        instance = new DemoDashboard();
        tag = new SendableTag("Demo", new String[] {
            "Demo: Drive Speed Multiplier",
            "Demo: Manual Shooter Speed"
        });
    }
    
    public static DemoDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "Demo";
    }
}
