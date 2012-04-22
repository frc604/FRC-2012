package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class FiringDashboard implements DashboardSection {
    private static final FiringDashboard instance;
    
    public void render () {
        SmartDashboard.putBoolean("Using Targets?", Robot.firingProvider.usingTargets());
        SmartDashboard.putInt("ups", ((RemoteCameraTCP) Robot.cameraInterface).getUPS());
    }
    
    private FiringDashboard () {
        
    }
    
    static {
        instance = new FiringDashboard();
    }
    
    public static FiringDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "Firing";
    }
}
