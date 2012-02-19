package com._604robotics.robot2012.configuration;

import com._604robotics.utils.XboxController.Button;

public interface ButtonConfiguration {
    public interface Driver {
        public static final int SHIFT = Button.EitherTrigger;
        public static final int PICKUP = Button.A;
        
        public static final int AUTO_BALANCE = Button.X;
        public static final int GYRO_RESET = Button.Back;
    }
    
    public interface Manipulator {
        public static final int FIRE = Button.A;
        public static final int AIM_TURRET = Button.B;
        
        public static final int TOGGLE_ANGLE = Button.Y;
        public static final int TOGGLE_LIGHT = Button.X;
    }
}