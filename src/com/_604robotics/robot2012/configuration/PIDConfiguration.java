package com._604robotics.robot2012.configuration;

import com._604robotics.utils.UpDownPIDController.Gains;

public interface PIDConfiguration {
    public interface Elevator {
        public static final Gains UP = new Gains(0.0085, 0D, 0.018);
        public static final Gains DOWN = new Gains(0.0029, 0.000003, 0.007);
    }
    
    public interface Shooter {
        public static final double P = -0.001;
        public static final double I = 0D;
        public static final double D = 0D;
        public static final double DP = -.005D;
        //public static final double D_NO_GYRO = 0.0002D;
        //public static final double DP = -.001D;
    }
    
    //On target < .025
    public interface AutoAim {
        public static final boolean USE_GYRO = true;
        
        public static final double P_NO_GYRO = 8D;
            // TODO: Configure this.
        public static final double I_NO_GYRO = .5D;
            // TODO: Configure this.
        public static final double D_NO_GYRO = 0D;
            // TODO: Configure this.
        
        
        public static final double P_GYRO = -.04D;
            // TODO: Configure this.
        public static final double I_GYRO = -.005D;
            // TODO: Configure this.
        public static final double D_GYRO = .02D;
            // TODO: Configure this.
    }
}