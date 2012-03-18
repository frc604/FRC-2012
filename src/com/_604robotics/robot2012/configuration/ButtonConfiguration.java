package com._604robotics.robot2012.configuration;

import com._604robotics.utils.XboxController.Button;
import com._604robotics.utils.XboxController.Stick;

/**
 * Button configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface ButtonConfiguration {
    public interface Driver {
        public static final int SHIFT = Button.EitherTrigger;
        public static final int TOGGLE_PICKUP = Button.RB;
        
        public static final int AUTO_BALANCE = Button.X;
        public static final int DISABLE_ELEVATOR = Button.Back;
        
        public static final int SLOW_BUTTON = Button.EitherStick;
        
        public static final int TINY_FORWARD = Button.Y;
        public static final int TINY_REVERSE = Button.A;
        
        public static final int CALIBRATE = Button.LB;
    }
    
    public interface Manipulator {
        public interface Elevator {
            public static final int FORWARD = Button.Y;
            public static final int LEFT = Button.X;
            public static final int RIGHT = Button.B;
            public static final int DOWN = Button.A;
        }
        
        public static final int AIM = Button.RB;
        public static final int SHOOT = Button.RT;
        public static final int POWER_HOPPER = Button.RightStick;
        
        public static final int PICKUP = Button.LT;
        
        public static final int TOGGLE_ANGLE = Button.LB;
        public static final int TOGGLE_LIGHT = Button.Back;
    }
    
    public interface Kinect {
        public static final int ENABLE = 1;
        public static final int ABORT = 2;
        public static final int DRIVE_ENABLED = 3;
        public static final int PICKUP_IN = 4;
        public static final int SUCK = 5;
        public static final int SHOOT = 6;
    }
}