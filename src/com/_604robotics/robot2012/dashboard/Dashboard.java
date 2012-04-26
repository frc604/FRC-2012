package com._604robotics.robot2012.dashboard;

import com._604robotics.utils.SmarterDashboard;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Vector;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class Dashboard {
    private static final Vector sections = new Vector();
    
    private static void enableSection (DashboardSection section) {
        sections.addElement(section);
        section.enable();
    }
    
    public static void registerSection (DashboardSection section) {
        //SmartDashboard.putData(new DisplaySectionCommand(section));
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
    
    private static class DisplaySectionCommand extends Command {
        private final DashboardSection section;
        private boolean done = false;
        
        public DisplaySectionCommand (DashboardSection section) {
            super(section.getName());
            this.section = section;
        }

        public void initialize() {
            
        }

        public void execute() {
            System.out.println("EXECUTING " + this.section.getName());
            Dashboard.enableSection(this.section);
            this.done = true;
        }

        public boolean isFinished() {
            return this.done;
        }

        public void end() {
            this.getTable().putBoolean("delete", true);
        }

        public void interrupted() {
            
        }
    }
}
