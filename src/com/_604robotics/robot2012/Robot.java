package com._604robotics.robot2012;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.PIDConfiguration;
import com._604robotics.robot2012.configuration.PortConfiguration;
import com._604robotics.robot2012.firing.CameraFiringProvider;
import com._604robotics.robot2012.firing.ManualFiringProvider;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.PickupMachine;
import com._604robotics.robot2012.machine.ShooterMachine;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import com._604robotics.robot2012.speedcontrol.SpeedProvider;
import com._604robotics.utils.*;
import com._604robotics.utils.XboxController.Axis;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot {
    private static final Timer lastRecalibrated = new Timer();
    
	public static final XboxController driveController;
	public static final XboxController manipulatorController;
	
	public static final KinectStick leftKinect;
	public static final KinectStick rightKinect;
	
	public static final RobotDrive driveTrain;
	
	public static final DualVictor elevatorMotors;
	
	public static final DualVictor shooterMotors;
	public static final SpringableVictor hopperMotor;
	public static final SpringableVictor pickupMotor;
	
	public static final SpringableRelay ringLight;
	
	public static final EncoderPIDSource encoderElevator;
	public static final EncoderSamplingRate encoderShooter;
	
	public static final DigitalInput elevatorLimitSwitch;
	
	public static final Gyro360 gyroHeading;
	
	public static final Compressor compressorPump;
	
	public static final SpringableDoubleSolenoid solenoidShifter;
	public static final DoubleSolenoid solenoidShooter;
	public static final DoubleSolenoid solenoidPickup;
	public static final SpringableDoubleSolenoid solenoidStinger;
	
	public static final UpDownPIDController pidElevator;
    
    public static final CachingPIDSource pidSourceDriveAngle;
    public static final PIDController pidAutoAim;
	
	public static final StrangeMachine pickupMachine;
	public static final ElevatorMachine elevatorMachine;
	public static final ShooterMachine shooterMachine;
	
	public static final CameraInterface cameraInterface;
	
	public static final CameraFiringProvider firingProvider;
	public static final SpeedProvider speedProvider;
	
	static {
        
        lastRecalibrated.start();
        
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
		encoderShooter.setFac(0.5);
        
        //encoderShooter = new EncoderRateSensor(PortConfiguration.Encoders.SHOOTER_A, PortConfiguration.Encoders.SHOOTER_B);
        
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
		solenoidStinger = new SpringableDoubleSolenoid(PortConfiguration.Pneumatics.STINGER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.STINGER_SOLENOID.REVERSE, ActuatorConfiguration.SOLENOID_STINGER.UP);
		
		solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
		
		/* Sets up the PID controllers. */
		
		pidElevator = new UpDownPIDController(PIDConfiguration.Elevator.UP, PIDConfiguration.Elevator.DOWN, encoderElevator, elevatorMotors);
		
		pidElevator.setInputRange(0, 1550);
		pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
		pidElevator.setSetpoint(822);
        
        pidSourceDriveAngle = new CachingPIDSource();
        pidAutoAim = new PIDController(PIDConfiguration.AutoAim.P, PIDConfiguration.AutoAim.I, PIDConfiguration.AutoAim.D, pidSourceDriveAngle, new TurningDrivePIDOutput(driveTrain));
		
		elevatorMotors.setController(pidElevator);
		
		/* Sets up the camera inteface. */
		
		cameraInterface = new RemoteCameraTCP();
		cameraInterface.begin();
		
		/* Sets up the firing provider. */
		
		firingProvider = new CameraFiringProvider(cameraInterface, new ManualFiringProvider());
		firingProvider.setPhysicsEnabled(true);
		firingProvider.setEnabled(true);
		
		/* Sets up the speed provider for the shooter. */
		
		//speedProvider = new StupidSpeedProvider(shooterMotors);
		//speedProvider = new NaiveSpeedProvider(shooterMotors, encoderShooter);
		//speedProvider = new ProcessSpeedProvider(-0.0001, 0D, -0.0008, encoderShooter, shooterMotors);
		speedProvider = new AwesomeSpeedController(PIDConfiguration.Shooter.P, PIDConfiguration.Shooter.I, PIDConfiguration.Shooter.D, PIDConfiguration.Shooter.DP, encoderShooter, shooterMotors);
		//speedProvider = new BangBangSpeedController(shooterMotors, encoderShooter);
        
		/* Sets up the Machines. */
		
		pickupMachine = new PickupMachine();
		elevatorMachine = new ElevatorMachine();
		shooterMachine = new ShooterMachine();
		        
        /* Send calibration signals. */
		
		DriverStation.getInstance().setDigitalOut(2, true);
		DriverStation.getInstance().setDigitalOut(5, false);
        
        
        /* Ready for action! */
        
        System.out.println("All done booting!");
	}
    
    public static void init () {
        // This space intentionally left blank.
    }
    
    public static void tryCalibrateElevator() {
        if (!Robot.elevatorLimitSwitch.get()) {
            DriverStation.getInstance().setDigitalOut(5, true);
            SmartDashboard.getBoolean("Elevator Calibrated", true);
            if (lastRecalibrated.get() >= 1) {
                Robot.encoderElevator.reset();
            } else {
                lastRecalibrated.reset();
            }
        }
    }
}
