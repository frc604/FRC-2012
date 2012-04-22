package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class AutonomousDashboard implements DashboardSection {
    private static final AutonomousDashboard instance;
    
    public static int step = 0;
    public static boolean done = false;
    
    public static double step2 = AutonomousConfiguration.STEP_2_SHOOT_TIME;
    public static double step3 = AutonomousConfiguration.STEP_3_TURN_TIME;
    public static double step4 = AutonomousConfiguration.STEP_4_DRIVE_TIME;
    public static double step5 = AutonomousConfiguration.STEP_5_WAIT_TIME;
    public static double maxStep = AutonomousConfiguration.STEP_5_WAIT_TIME;
    
    public void enable () {
        
    }
    
    public void render () {
        SmartDashboard.putDouble("Auton: Current Step", AutonomousDashboard.step);
        SmartDashboard.putBoolean("Auton: Done?", AutonomousDashboard.done);
        
        AutonomousDashboard.step2 = Dashboard.renderDouble("Auton: Step 2", step2);
        AutonomousDashboard.step3 = Dashboard.renderDouble("Auton: Step 3", step3);
        AutonomousDashboard.step4 = Dashboard.renderDouble("Auton: Step 4", step4);
        AutonomousDashboard.step5 = Dashboard.renderDouble("Auton: Step 5", step5);
        AutonomousDashboard.maxStep = Dashboard.renderDouble("Auton: Max Step", maxStep);
        
        SmartDashboard.putDouble("gyroHeading", Robot.gyroHeading.getAngle());
    }
    
    public static void setStep (int step) {
        AutonomousDashboard.step = step;
    }
    
    public static void setDone (boolean done) {
        AutonomousDashboard.done = done;
    }
    
    private AutonomousDashboard () {
        
    }
    
    static {
        instance = new AutonomousDashboard();
    }
    
    public static AutonomousDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "Autonomous";
    }
}
