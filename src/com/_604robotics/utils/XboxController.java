package com._604robotics.utils;

import edu.wpi.first.wpilibj.Joystick;

public class XboxController {
    private final Joystick joystick;

    public interface Axis {
        public static final int LEFT_STICK_X = 1;
        public static final int LEFT_STICK_Y = 2;
        public static final int RIGHT_STICK_X = 4;
        public static final int RIGHT_STICK_Y = 5;
    }
    
    public interface Button {
        public static final int A = 0;
        public static final int B = 1;
        public static final int X = 2;
        public static final int Y = 3;
        public static final int LB = 4;
        public static final int RB = 5;
        public static final int Back = 6;
        public static final int Start = 7;
        public static final int LeftStick = 8;
        public static final int RightStick = 9;
        public static final int LT = 10;
        public static final int RT = 11;
        public static final int EitherTrigger = 12;
        
        public interface DPad {
            public static int Up = 13;
            public static int Down = 14;
            public static int Left = 15;
            public static int Right = 16;
        }
    }
    
    public XboxController (int port) {
        this.joystick = new Joystick(port);
    }
    
    public XboxController (Joystick joystick) {
        this.joystick = joystick;
    }
    
    public double getAxis (int axis) {
        return this.joystick.getRawAxis(axis);
    }
    
    public boolean getButton (int button) {
        switch (button) {
            case Button.LT:
                return this.joystick.getRawAxis(3) < 0D;
                case Button.RT:
                    return this.joystick.getRawAxis(3) > 0D;
            case Button.EitherTrigger:
                return this.joystick.getRawAxis(3) != 0D;
            case Button.DPad.Up:
                return this.joystick.getRawAxis(7) < 0D;
            case Button.DPad.Down:
                return this.joystick.getRawAxis(7) > 0D;
            case Button.DPad.Left:
                return this.joystick.getRawAxis(6) < 0D;
            case Button.DPad.Right:
                return this.joystick.getRawAxis(6) > 0D;
            default:
                return this.joystick.getRawButton(button);
        }
    }
    
    public Joystick getJoystick () {
        return this.joystick;
    }
}