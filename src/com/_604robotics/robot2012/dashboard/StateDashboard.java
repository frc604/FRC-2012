package com._604robotics.robot2012.dashboard;

import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
import edu.wpi.first.wpilibj.smartdashboard.SendableTag;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class StateDashboard implements DashboardSection {
    private static final StateDashboard instance;
    private static final SendableTag tag;
    
    public void enable () {
        SmartDashboard.putData(tag);
    }
    
    public void render () {
        SmartDashboard.putBoolean("upHigh", Elevator.high);
        SmartDashboard.putBoolean("pickupIn", Pickup.up);
    }
    
    private StateDashboard () {
        
    }
    
    static {
        instance = new StateDashboard();
        tag = new SendableTag("State", new String[] {
           "upHigh",
           "pickupIn"
        });
    }
    
    public static StateDashboard getInstance () {
        return instance;
    }
    
    public String getName () {
        return "State";
    }
}
