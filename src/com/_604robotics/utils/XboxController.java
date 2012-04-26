package com._604robotics.utils;

import edu.wpi.first.wpilibj.Joystick;
import java.util.Hashtable;

/**
 * Wrapper joystick class for the Xbox 360 controllers.
 * 
 * 
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class XboxController extends Controller {
    /**
     * Enumeration for the available sticks on the Xbox controller.
     */
    public interface Stick {
        public static final int LEFT_STICK = 1;
        public static final int RIGHT_STICK = 4;
        public static final int DPAD = 6;
    }
    
    /**
     * Enumeration for the available axes on the Xbox controller.
     */
    public interface Axis {
        public static final int LEFT_STICK_X = 1;
        public static final int LEFT_STICK_Y = 2;
        public static final int RIGHT_STICK_X = 4;
        public static final int RIGHT_STICK_Y = 5;
    }
    
    /**
     * Enumeration for the available buttons on the Xbox controller.
     */
    public interface Button {
        public static final int A = 1;
        public static final int B = 2;
        public static final int X = 3;
        public static final int Y = 4;
        public static final int LB = 5;
        public static final int RB = 6;
        public static final int Back = 7;
        public static final int Start = 8;
        public static final int LeftStick = 9;
        public static final int RightStick = 10;
        public static final int LT = 11;
        public static final int RT = 12;
        public static final int EitherTrigger = 13;
        public static final int EitherStick = 18;
        
        public interface DPad {
            public static int Up = 14;
            public static int Down = 15;
            public static int Left = 16;
            public static int Right = 17;
        }
    }
    
    /**
     * Initialize a new XboxController on the specified port.
     * 
     * @param   port    The USB port the controller is connected to.
     */
    public XboxController (int port) {
        super(port);
    }
    
    /**
     * Initialize a new XboxController from the underlying Joystick.
     * 
     * @param   joystick    The Joystick to overlay the XboxController interface
     *                      on.
     */
    public XboxController (Joystick joystick) {
        super(joystick);
    }
    
    /**
     * Get whether or not the specified button is currently pressed.
     * 
     * @param   button  One of the button values specified in
     *                  XboxController.Button.
     */
    public boolean getButton (int button) {
        switch (button) {
            case Button.LT:
                return this.joystick.getRawAxis(3) > 0.2;
            case Button.RT:
                return this.joystick.getRawAxis(3) < -0.2;
            case Button.EitherTrigger:
                return Math.abs(this.joystick.getRawAxis(3)) > 0.2;
            case Button.DPad.Up:
                return this.joystick.getRawAxis(7) < -0.2;
            case Button.DPad.Down:
                return this.joystick.getRawAxis(7) > 0.2;
            case Button.DPad.Left:
                return this.joystick.getRawAxis(6) < -0.2;
            case Button.DPad.Right:
                return this.joystick.getRawAxis(6) > 0.2;
            case Button.EitherStick:
                return this.getButton(Button.LeftStick) || this.getButton(Button.RightStick);
            default:
                return super.getButton(button);
        }
    }
}