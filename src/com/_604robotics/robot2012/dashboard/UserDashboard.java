package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class UserDashboard implements DashboardSection {
    private static final UserDashboard instance;
    
    public static boolean shooterCharged = false;
    public static boolean elevatorCalibrated = false;
    
    public void enable () {
        
    }
    
    public void render () {
		SmartDashboard.putString("Shooter Charged: ",
                (UserDashboard.shooterCharged)
                    ? "YES YES YES YES YES"
                    : "NO NO NO NO NO"
        );
		SmartDashboard.putBoolean("Elevator Calibrated", UserDashboard.elevatorCalibrated);
        // TODO: Implement in merge.
        
        SmartDashboard.putBoolean("At the Fender?", Robot.firingProvider.isAtFender());
    }
    
    public static void setShooterCharged (boolean shooterCharged) {
        UserDashboard.shooterCharged = shooterCharged;
    }
    
    public static void elevatorCalibrated () {
        UserDashboard.elevatorCalibrated = true;
    }
    
    private UserDashboard () {
        
    }
    
    static {
        instance = new UserDashboard();
    }
    
    public static UserDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "User";
    }
}
