package com._604robotics.utils;

import edu.wpi.first.wpilibj.Joystick;
import java.util.Hashtable;

/**
 * Wrapper joystick class for the Xbox 360 controllers.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class Controller {
    protected final Joystick joystick;
    
    private Hashtable toggles = new Hashtable();
    private Hashtable deadbandsUpper = new Hashtable();
    private Hashtable deadbandsLower = new Hashtable();
    
    /**
     * Internal, kludgey function for turning a boolean into something that a
     * Hashtable can store.
     * 
     * @param   store   The boolean to convert.
     * 
     * @return  A string representation of the boolean.
     */
    private static String storeBoolean (boolean store) {
        return (store) ? "1" : "0";
    }
    
    /**
     * Internal, kinda-kludgey function for deadbanding a value for an axis.
     * 
     * @param   axis    The axis whose deadband should be applied.
     * @param   value   The value to deadband.
     * 
     * @return  The deadbanded value.
     */
    private double deadband (int axis, double value) {
        final String ax = Integer.toString(axis);
        if (this.deadbandsLower.containsKey(ax) && value > Double.parseDouble((String) this.deadbandsLower.get(ax)) && value < Double.parseDouble((String) this.deadbandsUpper.get(ax)))
            return 0D;
        return value;
    }
    
    /**
     * Enumeration for the available sticks on the Xbox controller.
     */
    public interface Stick {
        public static final int LEFT_STICK = 1;
        public static final int RIGHT_STICK = 4;
        public static final int DPAD = 6;
    }
    
    /**
     * Initialize a new XboxController on the specified port.
     * 
     * @param   port    The USB port the controller is connected to.
     */
    public Controller (int port) {
        this.joystick = new Joystick(port);
    }
    
    /**
     * Initialize a new XboxController from the underlying Joystick.
     * 
     * @param   joystick    The Joystick to overlay the XboxController interface
     *                      on.
     */
    public Controller (Joystick joystick) {
        this.joystick = joystick;
    }
    
    /**
     * Get the value of the specified axis.
     * 
     * @param   axis    One of the axis values specified in XboxController.Axis.
     */
    public double getAxis (int axis) {
        return this.deadband(axis, this.joystick.getRawAxis(axis));
    }
    
    /**
     * Get whether or not there's a value reading on the stick.
     * 
     * @param   stick   One of the stick values specified in
     *                  XboxController.Stick.
     * 
     * @return  Whether or not there's a value reading on the stick.
     */
    public boolean getStick (int stick) {
        return this.getAxis(stick) != 0 || this.getAxis(stick + 1) != 0;
    }
    
    /**
     * Get whether or not the specified button is currently pressed.
     * 
     * @param   button  One of the button values specified in
     *                  XboxController.Button.
     */
    public boolean getButton (int button) {
        return this.joystick.getRawButton(button);
    }
    
    /**
     * Resets the toggle registry for the contrller.
     */
    public void resetToggles () {
        this.toggles = new Hashtable();
    }
    
    /**
     * Get the toggle state of the specified button.
     * 
     * @param   button  One of the button values specified in
     *                  XboxController.Button.
     */
    public boolean getToggle (int button) {
        boolean toggled = this.toggles.containsKey(Integer.toString(button)) && this.toggles.get(Integer.toString(button)).equals(storeBoolean(true));
        boolean now = this.getButton(button);
        
        this.toggles.put(Integer.toString(button), storeBoolean(now));
        
        return !toggled && now;
    }
    
    /**
     * Gets the underlying Joystick object.
     * 
     * What, is XboxController not good enough for you?
     * 
     * @return  The underlying Joystick object.
     */
    public Joystick getJoystick () {
        return this.joystick;
    }
    
    /**
     * Sets the deadband for a particular axis.
     * 
     * @param   axis    The axis to set the deadband for.
     * @param   lower   The lower bound of the deadband.
     * @param   upper   The upper bound of the deadband.
     */
    public void setDeadband(int axis, double lower, double upper) {
        this.deadbandsLower.put(Integer.toString(axis), Double.toString(lower));
        this.deadbandsUpper.put(Integer.toString(axis), Double.toString(upper));
    }
}