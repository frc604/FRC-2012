package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.vision.Target;
import edu.wpi.first.wpilibj.smartdashboard.PIDCEditor;
import edu.wpi.first.wpilibj.smartdashboard.SendableTag;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class FiringDashboard implements DashboardSection {
    private static final FiringDashboard instance;
    private static final SendableTag tag;
    
    //private static final PIDCEditor editor;
    
    public void enable () {
        SmartDashboard.putData(tag);
        //SmartDashboard.putData("pidAutoAim", editor);
    }
    
    public void render () {
        SmartDashboard.putBoolean("Using Targets?", Robot.firingProvider.usingTargets());
        SmartDashboard.putInt("ups", ((RemoteCameraTCP) Robot.cameraInterface).getUPS());
        
        Target t = Robot.cameraInterface.getSingleTarget();
        if (t != null) {
            SmartDashboard.putDouble("Target X", t.getX());
            SmartDashboard.putDouble("Target Y", t.getY());
            SmartDashboard.putDouble("Target Z", t.getZ());
            
            System.out.println("Z = " + t.getHoopPosition().z);
        } else {
            SmartDashboard.putDouble("Target X", 9001D);
            SmartDashboard.putDouble("Target Y", 9001D);
            SmartDashboard.putDouble("Target Z", 9001D);
        }
    }
    
    private FiringDashboard () {
        
    }
    
    static {
        instance = new FiringDashboard();
        tag = new SendableTag("Firing", new String[]{
           "Using Targets?",
           "ups"
        });
        
        //editor = new PIDCEditor(Robot.pidAutoAim);
    }
    
    public static FiringDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "Firing";
    }
}
