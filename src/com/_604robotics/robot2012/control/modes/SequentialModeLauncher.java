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
    
    private static String packBooleanFromInt (int bool) {
        return (bool == 1)
                ? "true"
                : "false"
        ;
    }
    
    private static boolean unpackBoolean (Object packed) {
        return ((String) packed).equals("true");
    }
    
    private static int unpackBooleanAsInt (Object packed) {
        return (((String) packed).equals("true"))
                ? 1
                : 0
        ;
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
            SmartDashboard.putInt(this.getModeKey(i), unpackBooleanAsInt(this.enabled.elementAt(i)));
    }
    
    public void init () {
        mode = 0;
    }
    
    public boolean step () {
        if (mode >= modes.size())
            return false;
        
        int isEnabled = SmarterDashboard.getInt(this.getModeKey(mode), unpackBooleanAsInt(enabled.elementAt(mode)));
        enabled.setElementAt(packBooleanFromInt(isEnabled), mode);
        
        if (isEnabled != 1 || !this.getCurrentMode().step()) {
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
