package com._604robotics.robot2012.control.modes;

import com._604robotics.utils.SmarterDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Vector;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class SequentialModeLauncher implements ControlMode {
    private final Vector modes = new Vector();
    private final Vector enabled = new Vector();
    
    private String name = "SequentialModeLauncher";
    private int mode = 0;
    
    public SequentialModeLauncher (String name) {
        this.name = name;
    }
    
    private static String packBoolean (boolean bool) {
        return (bool)
                ? "true"
                : "false"
        ;
    }
    
    private static boolean unpackBoolean (Object packed) {
        return ((String) packed).equals("true");
    }
    
    private static String renderModeName (ControlMode mode, int index) {
        return "(" + index + ") " + mode.getName();
    }
    
    private String renderModeName (int index) {
        return renderModeName(this.getMode(index), index);
    }
    
    private String getModeKey (int index) {
        return this.getName() + ": " + this.renderModeName(index);
    }
    
    private ControlMode getMode (int index) {
        return ((ControlMode) modes.elementAt(index));
    }
    
    private ControlMode getCurrentMode () {
        return this.getMode(mode);
    }
    
    public void registerControlMode (ControlMode mode, boolean enabled) {
        this.modes.addElement(mode);
        this.enabled.addElement(packBoolean(enabled));
    }
    
    public void renderSmartDashboard () {
        SmartDashboard.putString(this.getName(), renderModeName(this.getCurrentMode(), mode));
        for (int i = 0; i < modes.size(); i++)
            SmartDashboard.putBoolean(this.getModeKey(i), unpackBoolean(this.enabled.elementAt(i)));
    }
    
    public void init () {
        mode = 0;
        if (mode < modes.size())
            this.getCurrentMode().init();
    }
    
    public boolean step () {
        if (mode >= modes.size())
            return false;
        
        boolean isEnabled = SmarterDashboard.getBoolean(this.getModeKey(mode), unpackBoolean(enabled.elementAt(mode)));
        enabled.setElementAt(packBoolean(isEnabled), mode);
        
        if (!isEnabled|| !this.getCurrentMode().step()) {
            this.getCurrentMode().disable();
            
            mode++;
            this.getCurrentMode().init();
        }
        
        return true;
    }
    
    public void disable () {
        if (mode < modes.size())
            this.getCurrentMode().disable();
    }

    public String getName () {
        return this.name;
    }
}
