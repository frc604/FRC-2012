package com._604robotics.robot2012.configuration;

import com._604robotics.utils.XboxController.Button;

/**
 * Button configuration.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface ButtonConfiguration {
    public interface Driver {
        public static final int SHIFT = Button.EitherTrigger;
        public static final int PICKUP = Button.RB;
        
        public static final int AUTO_BALANCE = Button.X;
        public static final int GYRO_RESET = Button.Back;
        
        public static final int TOGGLE_ANGLE = Button.Y;
        public static final int TOGGLE_LIGHT = Button.X;
    }
    
    public interface Manipulator {
        public static final int FIRE = Button.RT;
        public static final int PICKUP = Button.LT;
        
        public static final int AIM_TURRET = Button.Back;
        
        public static final int AUTO_TURRET = Button.LeftStick;
        
        public interface Elevator {
            public static final int FORWARD = Button.Y;
            public static final int LEFT = Button.X;
            public static final int RIGHT = Button.B;
            public static final int BACKWARD = Button.A;
        }
    }
}