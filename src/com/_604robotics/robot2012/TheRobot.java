package com._604robotics.robot2012;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.FiringConfiguration;
import com._604robotics.robot2012.configuration.PortConfiguration;
import com._604robotics.robot2012.firing.CameraFiringProvider;
import com._604robotics.robot2012.firing.ManualFiringProvider;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.PickupMachine;
import com._604robotics.robot2012.machine.ShooterMachine;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import com._604robotics.robot2012.speedcontrol.NaiveSpeedProvider;
import com._604robotics.robot2012.speedcontrol.SpeedProvider;
import com._604robotics.utils.DualVictor;
import com._604robotics.utils.EncoderPIDSource;
import com._604robotics.utils.EncoderSamplingRate;
import com._604robotics.utils.Gyro360;
import com._604robotics.utils.SmarterDashboard;
import com._604robotics.utils.SpringableDoubleSolenoid;
import com._604robotics.utils.SpringableRelay;
import com._604robotics.utils.SpringableVictor;
import com._604robotics.utils.StrangeMachine;
import com._604robotics.utils.UpDownPIDController;
import com._604robotics.utils.XboxController;
import com._604robotics.utils.UpDownPIDController.Gains;
import com._604robotics.utils.XboxController.Axis;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.KinectStick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class TheRobot {
	public static final TheRobot theRobot = new TheRobot();
	private static boolean initialized = false;
	
	public XboxController driveController;
	public XboxController manipulatorController;
	
	public KinectStick leftKinect;
	public KinectStick rightKinect;
	
	public RobotDrive driveTrain;
	
	public DualVictor elevatorMotors;
	
	public DualVictor shooterMotors;
	public SpringableVictor hopperMotor;
	public SpringableVictor pickupMotor;
	
	public SpringableRelay ringLight;
	
	public EncoderPIDSource encoderElevator;
	public EncoderSamplingRate encoderShooter;
	
	public DigitalInput elevatorLimitSwitch;
	
	public Gyro360 gyroHeading;
	
	public Compressor compressorPump;
	
	public SpringableDoubleSolenoid solenoidShifter;
	public DoubleSolenoid solenoidShooter;
	public DoubleSolenoid solenoidPickup;
	public SpringableDoubleSolenoid solenoidHopper;
	
	public UpDownPIDController pidElevator;
	
	public StrangeMachine pickupMachine;
	public ElevatorMachine elevatorMachine;
	public ShooterMachine shooterMachine;
	
	public SendableChooser inTheMiddle;
	
	public CameraInterface cameraInterface;
	
	public CameraFiringProvider firingProvider;
	public SpeedProvider speedProvider;
	
	private TheRobot() {
		
	}
	
	public static void init() {
		if(!initialized)
			theRobot.initialize();
		
		initialized = true;
	}
	
	private void initialize() {
		driveController = new XboxController(PortConfiguration.Controllers.DRIVE);
		manipulatorController = new XboxController(PortConfiguration.Controllers.MANIPULATOR);
		
		leftKinect = new KinectStick(PortConfiguration.Kinect.LEFT);
		rightKinect = new KinectStick(PortConfiguration.Kinect.RIGHT);
		
		manipulatorController.setDeadband(Axis.LEFT_STICK_Y, -0.2, 0.2);
		manipulatorController.setDeadband(Axis.RIGHT_STICK_Y, -0.2, 0.2);
		
		/* Set up the drive train. */
		
		driveTrain = new RobotDrive(new Victor(PortConfiguration.Motors.LEFT_DRIVE), new Victor(PortConfiguration.Motors.RIGHT_DRIVE));
		driveTrain.setSafetyEnabled(false);
		
		driveTrain.setInvertedMotor(MotorType.kFrontLeft, false);
		driveTrain.setInvertedMotor(MotorType.kFrontRight, false);
		driveTrain.setInvertedMotor(MotorType.kRearLeft, false);
		driveTrain.setInvertedMotor(MotorType.kRearRight, false);
		
		/* Set up the elevator, shooter, hopper, pickup, and rotation motors. */
		
		elevatorMotors = new DualVictor(PortConfiguration.Motors.ELEVATOR_LEFT, PortConfiguration.Motors.ELEVATOR_RIGHT);
		
		shooterMotors = new DualVictor(PortConfiguration.Motors.SHOOTER_LEFT, PortConfiguration.Motors.SHOOTER_RIGHT);
		hopperMotor = new SpringableVictor(PortConfiguration.Motors.HOPPER);
		pickupMotor = new SpringableVictor(PortConfiguration.Motors.PICKUP);
		
		/* Sets up the ring light relay. */
		
		ringLight = new SpringableRelay(PortConfiguration.Relays.RING_LIGHT_PORT, PortConfiguration.Relays.RING_LIGHT_DIRECTION, ActuatorConfiguration.RING_LIGHT.OFF);
		
		/* Sets up the encoders. */
		
		encoderElevator = new EncoderPIDSource(PortConfiguration.Encoders.ELEVATOR_A, PortConfiguration.Encoders.ELEVATOR_B);
		encoderElevator.setOffset(616);
		encoderElevator.start();
		
		encoderShooter = new EncoderSamplingRate(PortConfiguration.Encoders.SHOOTER_A, PortConfiguration.Encoders.SHOOTER_B);
		encoderShooter.setDistancePerPulse(1);
		encoderShooter.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
		encoderShooter.setSamplingRate(20);
		encoderShooter.setFac(SmarterDashboard.getDouble("fac", 0.5));
		encoderShooter.start();
		
		/* Sets up the limit switches for calibration. */
		
		elevatorLimitSwitch = new DigitalInput(PortConfiguration.Sensors.ELEVATOR_LIMIT_SWITCH);
		
		/* Sets up the gyro. */
		
		gyroHeading = new Gyro360(PortConfiguration.Sensors.GYRO_HEADING);
		
		/* Sets up the pneumatics. */
		
		compressorPump = new Compressor(PortConfiguration.Pneumatics.PRESSURE_SWITCH, PortConfiguration.Pneumatics.COMPRESSOR);
		
		solenoidShifter = new SpringableDoubleSolenoid(PortConfiguration.Pneumatics.SHIFTER_SOLENOID.LOW_GEAR, PortConfiguration.Pneumatics.SHIFTER_SOLENOID.HIGH_GEAR, ActuatorConfiguration.SOLENOID_SHIFTER.LOW_GEAR);
		solenoidShooter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHOOTER_SOLENOID.LOWER_ANGLE, PortConfiguration.Pneumatics.SHOOTER_SOLENOID.UPPER_ANGLE);
		solenoidPickup = new DoubleSolenoid(PortConfiguration.Pneumatics.PICKUP_SOLENOID.IN, PortConfiguration.Pneumatics.PICKUP_SOLENOID.OUT);
		solenoidHopper = new SpringableDoubleSolenoid(PortConfiguration.Pneumatics.HOPPER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.HOPPER_SOLENOID.REVERSE, ActuatorConfiguration.SOLENOID_HOPPER.REGULAR);
		
		solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
		
		/* Initializes inputs on the SmartDashboard and driver station. */
		
		DriverStation.getInstance().setDigitalOut(2, true);
		DriverStation.getInstance().setDigitalOut(5, false);
		
		SmartDashboard.putDouble("Elevator Up P", 0.0085);
		SmartDashboard.putDouble("Elevator Up I", 0D);
		SmartDashboard.putDouble("Elevator Up D", 0.018);
		
		SmartDashboard.putDouble("Elevator Down P", 0.0029);
		SmartDashboard.putDouble("Elevator Down I", 0.000003);
		SmartDashboard.putDouble("Elevator Down D", 0.007);
		
		/* Sets up the PID controllers. */
		
		pidElevator = new UpDownPIDController(new Gains(SmarterDashboard.getDouble("Elevator Up P", 0.0085), SmarterDashboard.getDouble("Elevator Up I", 0D), SmarterDashboard.getDouble("Elevator Up D", 0.018)), new Gains(SmarterDashboard.getDouble("Elevator Down P", 0.0029), SmarterDashboard.getDouble("Elevator Down I", 0.000003), SmarterDashboard.getDouble("Elevator Down P", 0.007)), encoderElevator, elevatorMotors);
		
		pidElevator.setInputRange(0, 1550);
		pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
		pidElevator.setSetpoint(822);
		
		elevatorMotors.setController(pidElevator);
		
		/* Sets up the switcher for autonomous. */
		
		inTheMiddle = new SendableChooser();
		inTheMiddle.addDefault("Autonomous: On the Sides", "No");
		inTheMiddle.addObject("Autonomous: In the Middle", "Yes");
		
		SmartDashboard.putData("inTheMiddle", inTheMiddle);
		
		/* Sets up the camera inteface. */
		
		cameraInterface = new RemoteCameraTCP();
		cameraInterface.begin();
		
		/* Sets up the firing provider. */
		
		firingProvider = new CameraFiringProvider(cameraInterface, new ManualFiringProvider());
		firingProvider.setPhysicsEnabled(false);
		firingProvider.setEnabled(false);
		
		/* Sets up the speed provider for the shooter. */
		
		//speedProvider = new StupidSpeedProvider(shooterMotors);
		//speedProvider = new NaiveSpeedProvider(shooterMotors, encoderShooter);
		//speedProvider = new ProcessSpeedProvider(-0.0001, 0D, -0.0008, encoderShooter, shooterMotors);
		speedProvider = new AwesomeSpeedController(-0.001, 0D, -0.001, 0D, encoderShooter, shooterMotors);
		
		if (speedProvider instanceof AwesomeSpeedController) {
			SmartDashboard.putDouble("Shooter P", ((AwesomeSpeedController) speedProvider).getP());
			SmartDashboard.putDouble("Shooter I", ((AwesomeSpeedController) speedProvider).getI());
			SmartDashboard.putDouble("Shooter D", ((AwesomeSpeedController) speedProvider).getD());
			SmartDashboard.putDouble("Shooter DP", ((AwesomeSpeedController) speedProvider).getDP());
			SmartDashboard.putDouble("Shooter fac", ((AwesomeSpeedController) speedProvider).fac);
			SmartDashboard.putDouble("Shooter maxSpeed", ((AwesomeSpeedController) speedProvider).maxSpeed);
		}
		
		/* Sets up the Machines. */
		
		pickupMachine = new PickupMachine(solenoidPickup);
		elevatorMachine = new ElevatorMachine(pidElevator, encoderElevator, solenoidShooter);
		shooterMachine = new ShooterMachine(hopperMotor, firingProvider, speedProvider, elevatorMotors);
		
		/* Sets up debug outputs. */
		
		SmartDashboard.putString("Shooter Charged: ", "NO NO NO NO NO");
		SmartDashboard.putBoolean("Elevator Calibrated", false);
		
		SmartDashboard.putDouble("Shooter Preset: Fender", FiringConfiguration.FENDER_FIRING_POWER);
		SmartDashboard.putDouble("Shooter Preset: Key", FiringConfiguration.KEY_FIRING_POWER);
		
		SmartDashboard.putDouble("Auton: Step 2", AutonomousConfiguration.STEP_2_SHOOT_TIME);
		SmartDashboard.putDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_TURN_TIME);
		SmartDashboard.putDouble("Auton: Step 4", AutonomousConfiguration.STEP_4_DRIVE_TIME);
		SmartDashboard.putDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_WAIT_TIME);
		SmartDashboard.putDouble("Auton: Max Step", AutonomousConfiguration.MAX_STEP);
		
		SmartDashboard.putDouble("fac", encoderShooter.getFac());
	}
}
