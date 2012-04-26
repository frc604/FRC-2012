package com._604robotics.robot2012.configuration;

import com._604robotics.utils.XboxController.Button;

/**
 * Button configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface ButtonConfiguration {
    public interface Driver {
        public static final int SHIFT = Button.LT;
        
        public static final int TOGGLE_PICKUP = Button.RB;
        
        public static final int DISABLE_ELEVATOR = Button.Back;
        
        public static final int SLOW_BUTTON = Button.EitherStick;
        public static final int STINGER_DOWN = Button.B;
        
        public static final int TINY_FORWARD = Button.Y;
        public static final int TINY_REVERSE = Button.A;
        
        public static final int AUTO_AIM = Button.RT;
        
        public static final int CALIBRATE = Button.LB;
    }
    
    /* public interface Manipulator {
        public interface Elevator {
            public static final int UP = Button.Y; // 6
            public static final int DOWN = Button.A; // 7
        }
        
        public static final int SHOOT = Button.RT; // 1
        
        public static final int PICKUP = Button.X; // 8
        
        public static final int TOGGLE_ANGLE = Button.RB; // 9
        
        public static final int AT_FENDER = Button.LB; // 4
        public static final int AT_KEY = Button.LT; // 5
    } */
    
    public interface Manipulator {
        public interface Elevator {
            public static final int UP = 6;
            public static final int DOWN = 7;
        }
        
        public static final int SHOOT = 1;
        
        public static final int PICKUP = 8;
        
        public static final int TOGGLE_ANGLE = 9;
        
        public static final int AT_FENDER = 4;
        public static final int AT_KEY = 5;
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