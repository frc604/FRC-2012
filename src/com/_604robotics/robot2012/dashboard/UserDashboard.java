package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SendableTag;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class UserDashboard implements DashboardSection {
    private static final UserDashboard instance;
    private static final SendableTag tag;
    
    public static boolean shooterCharged = false;
    public static boolean elevatorCalibrated = false;
    
    public void enable () {
        SmartDashboard.putData(tag);
    }
    
    public void render () {
		SmartDashboard.putString("Shooter Charged",
                (UserDashboard.shooterCharged)
                    ? "YES YES YES YES YES"
                    : "NO NO NO NO NO"
        );
        
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
        tag = new SendableTag("User", new String[] {
            "Shooter Charged",
            "At the Fender?"
        });
    }
    
    public static UserDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "User";
    }
}
