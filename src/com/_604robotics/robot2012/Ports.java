        package com._604robotics.robot2012;
/*Ports for the sensors, motors, solenoids, and compressor*/
public class Ports {
	public static final int XBOX_CONTROLLER_PORT	=1;

	public static final int FRONT_LEFT_MOTOR_PORT	=3;
	public static final int FRONT_RIGHT_MOTOR_PORT	=2;
	public static final int REAR_LEFT_MOTOR_PORT	=4;
	public static final int REAR_RIGHT_MOTOR_PORT	=1;

	public static final int GYRO_PORT			=1;
	public static final int ACCELEROMETER_PORT		=2;
        public static final int ENCODER_PORT1                   =3;
        public static final int ENCODER_PORT2                   =4;
        public static final int ENCODER_PORT3                   =5;
        public static final int ENCODER_PORT4                   =6;

	public static final int COMPRESSOR_PORT			=3;
	public static final int PRESSURE_SWITCH_PORT	=4;

	public static final int SHIFTER_SOLENOID_FORWARD_PORT	=4;
	public static final int SHIFTER_SOLENOID_REVERSE_PORT	=5;
        public static final int PICKUP_SOLENOID_DROPPED_PORT    =6;
        public static final int PICKUP_SOLENOID_RAISED_PORT     =7;

/* Button configuration. */
	public static final int AIM_TURRET_BUTTON		=1;
		// XBOX: B button
	public static final int FIRE_BUTTON				=0;
		// XBOX: A button

	public static final int ACCEL_BALANCE_BUTTON	=2;
		// XBOX: X button
	public static final int GYRO_RESET_BUTTON		=7;
		// XBOX: START button
}
