package com._604robotics.robot2012.dashboard;

import com._604robotics.utils.SmarterDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Vector;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Dashboard {
    private static final Vector sections = new Vector();
    
    public static void registerSection (DashboardSection section) {
        sections.addElement(section);
        section.enable();
    }
    
    public static void render () {
        for (int i = 0; i < sections.size(); i++)
            ((DashboardSection) sections.elementAt(i)).render();
    }
    
    public static boolean renderBoolean (String key, boolean def) {
        SmartDashboard.putBoolean(key, SmarterDashboard.getBoolean(key, def));
        return SmarterDashboard.getBoolean(key, def);
    }
    
    public static int renderInt (String key, int def) {
        SmartDashboard.putInt(key, SmarterDashboard.getInt(key, def));
        return SmarterDashboard.getInt(key, def);
    }
    
    public static double renderDouble (String key, double def) {
        SmartDashboard.putDouble(key, SmarterDashboard.getDouble(key, def));
        return SmarterDashboard.getDouble(key, def);
    }
}
