package com._604robotics.robot2012;

import com._604robotics.robot2012.autonomous.PIDDriveEncoderDifference;
import com._604robotics.robot2012.autonomous.PIDDriveEncoderOutput;
import com._604robotics.robot2012.autonomous.PIDDriveGyro;
import com._604robotics.robot2012.balancing.Balancing;
import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.*;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.robot2012.machine.*;
import com._604robotics.robot2012.machine.TurretMachine.TurretState;
import com._604robotics.robot2012.rotation.RotationProvider;
import com._604robotics.robot2012.rotation.SlowbroRotationProvider;
import com._604robotics.utils.UpDownPIDController.Gains;
import com._604robotics.utils.*;
import com._604robotics.utils.XboxController.Axis;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Date;

/**
 * Main class for the 2012 robot code.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * @author  Aaron Wang <aaronw94@gmail.com>
 * @author  Colin Aitken <cacolinerd@gmail.com>
 */
public class Robot2012Orange extends SimpleRobot {
    XboxController driveController;
    XboxController manipulatorController;
    
    KinectStick leftKinect;
    KinectStick rightKinect;
    
    RobotDrive driveTrain;
    
    DualVictor elevatorMotors;
    
    DualVictor shooterMotors;
    SpringableVictor hopperMotor;
    SpringableVictor pickupMotor;
    
    SpringableVictor turretRotationMotor;
    
    SpringableRelay ringLight;
    
    Encoder encoderLeftDrive;
    Encoder encoderRightDrive;
    
    EncoderPIDSource encoderElevator;
    EncoderPIDSource encoderTurretRotation;
    
    DigitalInput elevatorLimitSwitch;
    DigitalInput turretLimitSwitch;
    
    Gyro360 gyroHeading;
    Gyro gyroBalance;
    Accelerometer accelBalance;
    
    Compressor compressorPump;
    
    SpringableDoubleSolenoid solenoidShifter;
    DoubleSolenoid solenoidShooter;
    DoubleSolenoid solenoidPickup;
    SpringableDoubleSolenoid solenoidHopper;
    
    UpDownPIDController pidElevator;
    ConvertingPIDController pidTurretRotation;

    StrangeMachine pickupMachine;
    StrangeMachine elevatorMachine;
    TurretMachine turretMachine;
    ShooterMachine shooterMachine;
    
    RotationProvider rotationProvider;
    
    SendableChooser inTheMiddle;
    
    CameraInterface cameraInterface;
    
    VelocityController velocityController;
    
    boolean upHigh = false;
    boolean pickupIn = true;
    
    boolean noFixedDirection = true;
    int turretDirection = TurretState.FORWARD;
    
    boolean dontAim = false;
    
    public static double getDouble(String key, double def) {
        try {
            return SmartDashboard.getDouble(key, def);
        } catch (Exception ex) {
            return def;
        }
    }
    
    /**
     * Constructor.
     * 
     * Disables the built-in watchdog, since it's not really needed anymore.
     */    
    public Robot2012Orange() {
        this.getWatchdog().setEnabled(false);
    }

    /**
     * Initializes the robot on startup.
     * 
     * Sets up all the controllers, sensors, actuators, etc.
     */
    public void robotInit () {
        /* Set up the controllers. */
        
        driveController = new XboxController(PortConfiguration.Controllers.DRIVE);
        manipulatorController = new XboxController(PortConfiguration.Controllers.MANIPULATOR);
        
        leftKinect = new KinectStick(PortConfiguration.Kinect.LEFT);
        rightKinect = new KinectStick(PortConfiguration.Kinect.RIGHT);
        
        manipulatorController.setDeadband(Axis.RIGHT_STICK_X, -0.2, 0.2);
        manipulatorController.setDeadband(Axis.RIGHT_STICK_Y, -0.2, 0.2);
        
        /* Set up the drive train. */
        
        driveTrain = new RobotDrive(new Victor(PortConfiguration.Motors.LEFT_DRIVE), new Victor(PortConfiguration.Motors.RIGHT_DRIVE));
        driveTrain.setSafetyEnabled(false);

        driveTrain.setInvertedMotor(MotorType.kFrontLeft, true);
        driveTrain.setInvertedMotor(MotorType.kFrontRight, true);
        driveTrain.setInvertedMotor(MotorType.kRearLeft, true);
        driveTrain.setInvertedMotor(MotorType.kRearRight, true);
        
        /* Set up the elevator, shooter, hopper, pickup, and rotation motors. */
        
        elevatorMotors = new DualVictor(PortConfiguration.Motors.ELEVATOR_LEFT, PortConfiguration.Motors.ELEVATOR_RIGHT);
        
        shooterMotors = new DualVictor(PortConfiguration.Motors.SHOOTER_LEFT, PortConfiguration.Motors.SHOOTER_RIGHT);
        hopperMotor = new SpringableVictor(PortConfiguration.Motors.HOPPER);
        pickupMotor = new SpringableVictor(PortConfiguration.Motors.PICKUP);
        
        turretRotationMotor = new SpringableVictor(PortConfiguration.Motors.TURRET_ROTATION);
        
        /* Sets up the ring light relay. */
        
        ringLight = new SpringableRelay(PortConfiguration.Relays.RING_LIGHT_PORT, PortConfiguration.Relays.RING_LIGHT_DIRECTION, ActuatorConfiguration.RING_LIGHT.OFF);
        
        /* Sets up the encoders for the drive, elevator, and turret. */
        
        encoderLeftDrive = new Encoder(PortConfiguration.Encoders.Drive.LEFT_A, PortConfiguration.Encoders.Drive.LEFT_B);
        encoderRightDrive = new Encoder(PortConfiguration.Encoders.Drive.RIGHT_A, PortConfiguration.Encoders.Drive.RIGHT_B);
        
        encoderElevator = new EncoderPIDSource(PortConfiguration.Encoders.ELEVATOR_A, PortConfiguration.Encoders.ELEVATOR_B);
        encoderTurretRotation = new EncoderPIDSource(PortConfiguration.Encoders.TURRET_ROTATION_A, PortConfiguration.Encoders.TURRET_ROTATION_B);
        
        encoderLeftDrive.setDistancePerPulse(SensorConfiguration.Encoders.LEFT_DRIVE_INCHES_PER_CLICK);
        encoderRightDrive.setDistancePerPulse(SensorConfiguration.Encoders.RIGHT_DRIVE_INCHES_PER_CLICK);
        
        encoderElevator.setOffset(616);
        
        encoderTurretRotation.setDistancePerPulse(SensorConfiguration.Encoders.TURRET_DEGREES_PER_CLICK);
        
        encoderLeftDrive.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
        encoderRightDrive.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
        
        encoderLeftDrive.start();
        encoderRightDrive.start();
        
        encoderElevator.start();
        encoderTurretRotation.start();
        
        /* Sets up the limit switches for calibration. */
        
        elevatorLimitSwitch = new DigitalInput(PortConfiguration.Sensors.ELEVATOR_LIMIT_SWITCH);
        
        /* Sets up the gyros and the accelerometer. */
        
        gyroHeading = new Gyro360(PortConfiguration.Sensors.GYRO_HEADING);
        gyroBalance = new Gyro(PortConfiguration.Sensors.GYRO_BALANCE);
        accelBalance = new Accelerometer(PortConfiguration.Sensors.ACCELEROMETER);
        accelBalance.setSensitivity(SensorConfiguration.ACCELEROMETER_SENSITIVITY);

        /* Sets up the pneumatics. */
        
        compressorPump = new Compressor(PortConfiguration.Pneumatics.PRESSURE_SWITCH, PortConfiguration.Pneumatics.COMPRESSOR);
        
        solenoidShifter = new SpringableDoubleSolenoid(PortConfiguration.Pneumatics.SHIFTER_SOLENOID.LOW_GEAR, PortConfiguration.Pneumatics.SHIFTER_SOLENOID.HIGH_GEAR, ActuatorConfiguration.SOLENOID_SHIFTER.LOW_GEAR);
        solenoidShooter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHOOTER_SOLENOID.LOWER_ANGLE, PortConfiguration.Pneumatics.SHOOTER_SOLENOID.UPPER_ANGLE);
        solenoidPickup = new DoubleSolenoid(PortConfiguration.Pneumatics.PICKUP_SOLENOID.IN, PortConfiguration.Pneumatics.PICKUP_SOLENOID.OUT);
        solenoidHopper = new SpringableDoubleSolenoid(PortConfiguration.Pneumatics.HOPPER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.HOPPER_SOLENOID.REVERSE, ActuatorConfiguration.SOLENOID_HOPPER.REGULAR);
        
        /*
         * Sets up the PID controllers, and initializes inputs on the
         * SmartDashboard.
         */
        
        pidElevator = new UpDownPIDController(new Gains(0.0085, 0D, 0.018), new Gains(0.0029, 0.000003, 0.007), encoderElevator, elevatorMotors);
        pidTurretRotation = new ConvertingPIDController(-0.0022, -0.0008, -0.006, encoderTurretRotation, turretRotationMotor);
        
        pidElevator.setInputRange(0, 1550);
        pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
        pidElevator.setSetpoint(822);
        
        pidTurretRotation.setConversionFactor(1 / SensorConfiguration.Encoders.TURRET_DEGREES_PER_CLICK);
        pidTurretRotation.setOutputRange(ActuatorConfiguration.TURRET_ROTATION_POWER_MIN, ActuatorConfiguration.TURRET_ROTATION_POWER_MAX);
        
        elevatorMotors.setController(pidElevator);
        turretRotationMotor.setController(pidTurretRotation);
        
        /* Sets up the switcher for autonomous. */
        
        inTheMiddle = new SendableChooser();
        inTheMiddle.addDefault("Autonomous: In the Middle", "Yes");
        inTheMiddle.addObject("Autonomous: On the Sides", "No");
        
        SmartDashboard.putData("inTheMiddle", inTheMiddle);
        
        SmartDashboard.putDouble("Shooter Speed", 1D);
        
        /* Sets up the camera inteface. */
        
        cameraInterface = new RemoteCameraTCP();
        cameraInterface.begin();
                
        /* Sets up the rotation provider. */
        
        rotationProvider = new SlowbroRotationProvider(pidTurretRotation, cameraInterface, encoderTurretRotation);
        
        /* Sets up the Machines. */
        
        pickupMachine = new PickupMachine(solenoidPickup);
        elevatorMachine = new ElevatorMachine(pidElevator, encoderElevator);
        turretMachine = new TurretMachine(pidTurretRotation, rotationProvider, encoderTurretRotation);
        shooterMachine = new ShooterMachine(shooterMotors, hopperMotor);
        
        double p_Vel, i_Vel, d_Vel;
        SmartDashboard.putDouble("P_Vel", p_Vel = getDouble("P_Vel", 0));
        SmartDashboard.putDouble("I_Vel", i_Vel = getDouble("I_Vel", 0));
        SmartDashboard.putDouble("D_Vel", d_Vel = getDouble("D_Vel", 0));
        
        velocityController = new VelocityController(p_Vel, i_Vel, d_Vel, encoderLeftDrive, encoderRightDrive, driveTrain, gyroBalance);
        
        SmartDashboard.putDouble("Confidence Threshold", 0.7);
        SmartDashboard.putDouble("Target Timeout", 1.5);
        SmartDashboard.putDouble("Steady Threshold", 0.5);
        SmartDashboard.putDouble("Unsteady Threshold", 1D);
        
        SmartDashboard.putDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_FORWARD_TIME);
        SmartDashboard.putDouble("Auton: Step 5 Sides", AutonomousConfiguration.STEP_5_FORWARD_TIME_SIDES);
        SmartDashboard.putDouble("Auton: Step 1", AutonomousConfiguration.STEP_1_FORWARD_TIME);
        SmartDashboard.putDouble("Auton: Step 2", AutonomousConfiguration.STEP_2_WAIT_TIME);
        SmartDashboard.putDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_BACKWARD_TIME);
        SmartDashboard.putDouble("Auton: Step 4", AutonomousConfiguration.STEP_4_TURN_TIME);
        SmartDashboard.putDouble("Auton: Step 10", AutonomousConfiguration.STEP_10_SHOOTING_TIME);
        
        /* Because we can. */
        
        System.out.println("Hello, ninja h4X0r.");
    }
    
    /**
     * Figures out if a value is within a specific range.
     * 
     * @param   xValue      The value to test.
     * @param   upperRange  The upper bound of the range.
     * @param   lowerRange  The lower bound of the range.
     * 
     * @return  TRUE if xValue is between upperRange and lowerRange; FALSE if
     *          not.
     */
    public static boolean isInRange(double xValue, double upperRange, double lowerRange) {
        return xValue <= upperRange && xValue >= lowerRange;
    }

    /**
     * If a value is within a range, set it to a specific value.
     * 
     * This is most commonly used to put a deadband on joystick inputs or 
     * motor outputs.
     * 
     * @param   xValue          The value to test.
     * @param   upperBand       The upper bound of the range.
     * @param   lowerBand       The lower bound of the range.
     * @param   correctedValue  The value to return if xValue is within the
     *                          range.
     * 
     * @return  xValue if xValue does not fall within the range; correctedValue
     *          otherwise.
     */
    public static double deadband(double xValue, double upperBand, double lowerBand, double correctedValue) {
        return (isInRange(xValue, upperBand, lowerBand))
                ? correctedValue
                : xValue;
    }
    /**
     * Aim at backboard, shoot.
     */
    public void aimAndShoot() {
        ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
        
        /*
         * Aim, or make sure we're aimed. Then fire.
         */
        
        if (dontAim || turretMachine.crank(TurretState.AIMED)) {
            // TODO: Add actual firing stuff here.
            
            System.out.println("SHOOTING");
            
            //solenoidHopper.set(ActuatorConfiguration.SOLENOID_HOPPER.PUSH);
            shooterMachine.crank(ShooterState.SHOOTING);
        }
    }

    /**
     * Automated drive for autonomous mode.
     * 
     * If in middle, drive forward, knock down bridge, turn around.
     * 
     * Else, or then, go ahead and try to score.
     */
    public void autonomous() {
        // TODO: Calibrate encoders.
        
        compressorPump.start();
        
        boolean elevatorCalibrated = false;
        int step = 1;
        
        double drivePower;
        double gyroAngle;
        
        double forwardTime = getDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_FORWARD_TIME_SIDES);
        
        boolean turnedAround = false;
        boolean pickupIsIn = false;
        
        boolean kinect = false;
        boolean abort = false;
        
        boolean lightState = false;
        
        /* Reset stuff. */
        
        upHigh = false;
        pickupIn = true;

        noFixedDirection = true;
        turretDirection = TurretState.FORWARD;

        dontAim = false;
        
        /* If we're not in the middle, skip over the bridge stuff. */
        
        if (((String) inTheMiddle.getSelected()).equals("Yes")) {
            step = 4;
            forwardTime = getDouble("Auton: Step 5 Sides", AutonomousConfiguration.STEP_5_FORWARD_TIME_SIDES);
        }

        Timer controlTimer = new Timer();
        controlTimer.start();

        Timer calibrationTimer = new Timer();
        calibrationTimer.start();
        
        encoderTurretRotation.reset();
        encoderTurretRotation.setOffset(SensorConfiguration.TURRET_CALIBRATION_OFFSET);
        turretMachine.setTurretSidewaysPosition(encoderTurretRotation.getDistance());
        
        elevatorMotors.set(0D);
        
        gyroHeading.reset();
        
        long began = new Date().getTime();
        
        while (isAutonomous() && isEnabled()) {
            kinect = leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
            abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
            
            if (kinect || abort)
                break;
            
            /* Calibrate the elevator while everything else is going on. */
            
            pickupMachine.crank(PickupState.OUT);
            
            if (step > 4 && !elevatorCalibrated) {
                if (calibrationTimer.get() < 5) {
                    if (elevatorLimitSwitch.get()) {
                        elevatorMotors.set(-0.4);
                    } else {
                        calibrationTimer.stop();
                        elevatorMotors.set(0D);
                        encoderElevator.reset();
                        elevatorCalibrated = true;
                    }
                } else {
                    calibrationTimer.stop();
                    elevatorMotors.set(0D);
                    encoderElevator.reset();
                    elevatorCalibrated = true;
                }
            }
            
            System.out.println("elevatorLimitSwitch = " + elevatorLimitSwitch.get());
            System.out.println("elevatorCalibrated = " + elevatorCalibrated);
            
            System.out.println("---------------------------");
            
            if (new Date().getTime() - began > 1500)
                step = 5;
            
            if (controlTimer.get() >= 1) {
                lightState = !lightState;
                ringLight.set((lightState) ? ActuatorConfiguration.RING_LIGHT.ON : ActuatorConfiguration.RING_LIGHT.OFF);
                controlTimer.reset();
            }
            
            if (step > AutonomousConfiguration.MAX_STEP) {
                driveTrain.tankDrive(0D, 0D);

                elevatorMotors.reload();
                shooterMotors.reload();
                hopperMotor.reload();
                turretRotationMotor.reload();
                ringLight.reload();

                continue;
            }
            
            /* Handle the main logic. */
            
            switch (step) {
                case 1:
                    /* Drive forward and stop, then smash down the bridge. */
                    
                    if (controlTimer.get() <= AutonomousConfiguration.STEP_1_FORWARD_TIME) {
                        drivePower = Math.max(0.2, 1 - controlTimer.get() / getDouble("Auton: Step 1", AutonomousConfiguration.STEP_1_FORWARD_TIME));
                        driveTrain.tankDrive(drivePower, drivePower);
                    } else {
                        driveTrain.tankDrive(0D, 0D);
                        pickupMachine.crank(PickupState.OUT);
                        
                        controlTimer.reset();
                        step++;
                    }
                    
                    break;
                case 2:
                    /* Wait a bit. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (controlTimer.get() >= getDouble("Auton: Step 2", AutonomousConfiguration.STEP_2_WAIT_TIME)) 
                        step++;
                    
                    break;
                case 3:
                    /* Drive backward. */
                        
                    if (controlTimer.get() <= AutonomousConfiguration.STEP_3_BACKWARD_TIME) {
                        drivePower = Math.max(0.2, 1 - controlTimer.get() / getDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_BACKWARD_TIME));
                        driveTrain.tankDrive(drivePower, drivePower);
                    } else {
                        driveTrain.tankDrive(0D, 0D);
                        
                        gyroHeading.reset();
                        
                        controlTimer.reset();
                        step++;
                    }
                    
                    break;
                case 4:
                    /*
                     * Make sure the pickup is down. Turn around, and face the
                     * nets.
                     */
                    
                    pickupMachine.crank(PickupState.OUT);
                    
                    if (controlTimer.get() <= getDouble("Auton: Step 4", AutonomousConfiguration.STEP_4_TURN_TIME)) {
                        gyroAngle = gyroHeading.getAngle();
                        
                        if (turnedAround || (gyroAngle > 179 && gyroAngle < 181)) {
                            turnedAround = true;
                            driveTrain.tankDrive(0D, 0D);
                        } else {
                            drivePower = Math.max(0.2, 1 - gyroAngle / 180);
                            driveTrain.tankDrive(drivePower, drivePower * -1);
                        }
                    } else {
                        driveTrain.tankDrive(0D, 0D);
                        
                        controlTimer.reset();
                        step++;
                    }
                    
                    break;
                case 5:
                    /*
                     * Keep the pickup down, just in case. Drive forward toward
                     * the nets. The elevator should be calibrating while this
                     * is going on.
                     */
                    
                    pickupMachine.crank(PickupState.OUT);
                    
                    if (controlTimer.get() <= forwardTime) {
                        drivePower = Math.max(0.2, 1 - controlTimer.get() / forwardTime);
                        driveTrain.tankDrive(drivePower, drivePower);
                    } else {
                        driveTrain.tankDrive(0D, 0D);
                        controlTimer.reset();
                        step++;
                    }
                    
                    break;
                case 6:
                    /* Block until the elevator is calibrated. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (elevatorCalibrated)
                        step++;
                    
                    break;
                case 7:
                    /* Put the elevator up and the pickup in. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (elevatorMachine.test(ElevatorState.PICKUP_OKAY))
                        pickupIsIn = pickupMachine.crank(PickupState.IN);
                    else
                        pickupIsIn = false;
                    
                    if (elevatorMachine.test(ElevatorState.TURRET_OKAY))
                        turretMachine.crank(TurretState.FORWARD);
                    
                    if (elevatorMachine.crank(ElevatorState.HIGH) && pickupIsIn)
                        step++;
                    
                    break;
                case 8:
                    /* Put the turret forward. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (turretMachine.crank(TurretState.FORWARD))
                        step++;
                    
                    break;
                case 9:
                    /* Aim... */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (turretMachine.crank(TurretState.AIMED)) {
                        controlTimer.reset();
                        step++;
                    }
                    
                    break;
                case 10:
                    /* ...and shoot! */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (controlTimer.get() < getDouble("Auton: Step 10", AutonomousConfiguration.STEP_10_SHOOTING_TIME))
                        shooterMachine.crank(ShooterState.SHOOTING);
                    
                    break;
            }
            
            elevatorMotors.reload();
            shooterMotors.reload();
            hopperMotor.reload();
            turretRotationMotor.reload();
            ringLight.reload();
        }
        
        while (isAutonomous() && isEnabled() && !abort && !kinect) {
            kinect = leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
            abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
            
            driveTrain.tankDrive(0D, 0D);

            elevatorMotors.reload();
            shooterMotors.reload();
            hopperMotor.reload();
            turretRotationMotor.reload();
            ringLight.reload();
        }
        
        if (kinect && elevatorCalibrated) {
            ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
            
            while (isAutonomous() && isEnabled() && !abort) {
                abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
                
                if (abort)
                    break;
                
                if (turretMachine.crank(TurretState.SIDEWAYS) && pickupMachine.crank(PickupState.OUT) && elevatorMachine.crank(ElevatorState.LOW))
                    break;
                
                driveTrain.tankDrive(0D, 0D);
                
                elevatorMotors.reload();
                shooterMotors.reload();
                hopperMotor.reload();
                turretRotationMotor.reload();
            }
            
            ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
            
            while (isAutonomous() && isEnabled() && !abort) {
                abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);

                if (abort)
                    break;
                
                if (leftKinect.getRawButton(ButtonConfiguration.Kinect.DRIVE_ENABLED))
                    driveTrain.tankDrive(leftKinect.getRawAxis(1) * 0.8, rightKinect.getRawAxis(1) * 0.8);
                else
                    driveTrain.tankDrive(0D, 0D);

                if (leftKinect.getRawButton(ButtonConfiguration.Kinect.PICKUP_IN))
                    pickupMachine.crank(PickupState.IN);
                else
                    pickupMachine.crank(PickupState.OUT);

                if (leftKinect.getRawButton(ButtonConfiguration.Kinect.SUCK) && pickupMachine.test(PickupState.OUT)) {
                    pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                    hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                } else {
                    pickupMotor.set(0D);
                    hopperMotor.set(0D);
                }

                // TODO: Implement shooting.
                
                elevatorMotors.reload();
                shooterMotors.reload();
                hopperMotor.reload();
                turretRotationMotor.reload();
                ringLight.reload();
            }
        }
        
        driveTrain.tankDrive(0D, 0D);
        
        pickupMotor.set(0D);
        hopperMotor.set(0D);
        
        elevatorMotors.reload();
        shooterMotors.reload();
        hopperMotor.reload();
        turretRotationMotor.reload();
        ringLight.reload();
        
        compressorPump.stop();
    }

    /**
     * Operator-controlled drive for Teleop mode.
     * 
     * Handles robot driving, automated balancing for the bridge, ball pickup,
     * turret aiming, firing, angle adjustments, light control, elevator
     * control - both automated and manual - pneumatics, shifting, and various
     * other things.
     */
    public void operatorControl() {
        driveTrain.setSafetyEnabled(true);
        compressorPump.start();
        
        int settleState = 2;
        Timer settleTimer = new Timer();
        
        manipulatorController.resetToggles();
        driveController.resetToggles();
        
        pidElevator.reset();
        pidTurretRotation.reset();
        
        while (isOperatorControl() && isEnabled()) {
            shooterMachine.setShooterSpeed(getDouble("Shooter Speed", -1D));
            ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
            
            if (driveController.getToggle(ButtonConfiguration.Driver.DISABLE_ELEVATOR))
                elevatorMotors.setDisabled(!elevatorMotors.getDisabled());
            
            if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.AIMING_OVERRIDE))
                dontAim = !dontAim;
            
            if (!elevatorLimitSwitch.get())
                encoderElevator.reset();
            
            /* Controls the gear shift. */
            
            if (driveController.getButton(ButtonConfiguration.Driver.SHIFT))
                solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR);
            
            /* 
             * Automatically balances the robot on the bridge. Well, that's the
             * plan, anyway.
             */

            if (driveController.getButton(ButtonConfiguration.Driver.AUTO_BALANCE)) {
                
                double p = getDouble("P_Vel", 0);
                double i = getDouble("I_Vel", 0);
                double d = getDouble("D_Vel", 0);
                
                velocityController.setAngleGains(p, i, d);
                // TODO: Replace this with stuff from the balancing code, once it's hammered out.
                
                //accelPower = deadband(MathUtils.asin(accelBalance.getAcceleration()), 0.1745, -0.1745, 0D) / SensorConfiguration.ACCELEROMETER_UPPER_RADIANS * ActuatorConfiguration.ACCELEROMETER_DRIVE_POWER;
                
                double driveVel = Balancing.getSpeedforBalance(gyroBalance.getAngle());
                
                velocityController.setVelocity(driveVel);
                SmartDashboard.putDouble("Drive Velocity", driveVel);
                
                //driveTrain.tankDrive(accelPower, accelPower);
                
                SmartDashboard.putString("Drive Mode", "Balancing");
                SmartDashboard.putDouble("DriveVel", driveVel);
            } else if (driveController.getButton(ButtonConfiguration.Driver.TINY_FORWARD)) {
                driveTrain.tankDrive(ActuatorConfiguration.TINY_FORWARD_SPEED, ActuatorConfiguration.TINY_FORWARD_SPEED);
                SmartDashboard.putString("Drive Mode", "Tiny (Forward)");
            } else if (driveController.getButton(ButtonConfiguration.Driver.TINY_REVERSE)) {
                driveTrain.tankDrive(ActuatorConfiguration.TINY_REVERSE_SPEED, ActuatorConfiguration.TINY_REVERSE_SPEED);
                SmartDashboard.putString("Drive Mode", "Tiny (Reverse)");
            } else if (driveController.getButton(ButtonConfiguration.Driver.SLOW_BUTTON)) {
                driveTrain.tankDrive(driveController.getAxis(Axis.LEFT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED, driveController.getAxis(Axis.RIGHT_STICK_Y) * ActuatorConfiguration.MAX_SLOW_SPEED);
                SmartDashboard.putString("Drive Mode", "Manual (Slow)");
            } else {
                driveTrain.tankDrive(driveController.getAxis(Axis.LEFT_STICK_Y), driveController.getAxis(Axis.RIGHT_STICK_Y));
                SmartDashboard.putString("Drive Mode", "Manual");
            }
            
            /* Toggle the "default" height between "up high" and "down low". */
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.FORWARD)) {
                upHigh = true;
                pickupIn = true;
                
                noFixedDirection = false;
                turretDirection = TurretState.FORWARD;
            } else if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.LEFT)) {
                upHigh = true;
                pickupIn = true;
                
                noFixedDirection = false;
                turretDirection = TurretState.LEFT;
            } else if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.RIGHT)) {
                upHigh = true;
                pickupIn = true;
                
                noFixedDirection = false;
                turretDirection = TurretState.RIGHT;
            } else if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.DOWN)) {
                upHigh = false;
            }
            
            /* Toggle the pickup state between "up" and "down". */
            
            if (driveController.getToggle(ButtonConfiguration.Driver.TOGGLE_PICKUP))
                pickupIn = !pickupIn;
            
            SmartDashboard.putBoolean("upHigh", upHigh);
            SmartDashboard.putBoolean("pickupIn", pickupIn);
            
            /*
             * If pickupIn is true and upHigh is true, or only pickupIn is true,
             * then make sure the elevator is up high enough, and lift up the pickup.
             * 
             * Else, check the "default" position. If it is up high, then lower
             * the pickup and raise the elevator simultaneously. If it is down
             * low, then make sure the pickup is down first, and then lower the
             * elevator.
             */
            
            if ((pickupIn && upHigh) || pickupIn) {
                if (elevatorMachine.test(ElevatorState.PICKUP_OKAY) || elevatorMotors.getDisabled())
                    pickupMachine.crank(PickupState.IN);
                
                if (upHigh && !noFixedDirection && elevatorMachine.test(ElevatorState.TURRET_OKAY))
                    noFixedDirection = turretMachine.crank(turretDirection);

                if (upHigh) {
                    if (elevatorMachine.crank(ElevatorState.HIGH)) {
                        SmartDashboard.putString("Ready to Shoot", "Yes");
                        
                        if (Math.abs(manipulatorController.getAxis(Axis.LEFT_STICK_X)) > 0) {
                            noFixedDirection = true;
                            turretRotationMotor.set(manipulatorController.getAxis(Axis.LEFT_STICK_X) * 0.5);
                        }

                        /* Toggles the shooter angle. */
                        
                        if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
                            if (solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE)
                                solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
                            else
                                solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
                        }
                        
                        if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.AIM_AND_SHOOT))
                            pidTurretRotation.reset();
                        
                        if (manipulatorController.getButton(ButtonConfiguration.Manipulator.AIM_AND_SHOOT))  {
                            noFixedDirection = true;
                            this.aimAndShoot();
                        }
                    }
                } else {
                    if (turretMachine.crank(TurretState.SIDEWAYS))
                        elevatorMachine.crank(ElevatorState.MEDIUM);
                }
            } else {
                if (turretMachine.crank(TurretState.SIDEWAYS)) {
                    /*
                     * To prevent us from unintentionally violating <G21>, the
                     * pickup cannot go out until the turret is in the sideways
                     * position.
                     */
                    
                    if (pickupMachine.crank(PickupState.OUT)) {
                        /*
                         * If the pickup is down and the elevator is at rest,
                         * then allow the user to trigger the pickup mechanism.
                         */
                        
                        if (elevatorMachine.crank(ElevatorState.LOW)) {
                            /* Controls the pickup mechanism. */

                            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP)) {
                                pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                                hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                                
                                settleState = 0;
                                settleTimer.stop();
                            }
                        }
                    }
                }
            }
            
            /*
             * Settle the balls back in a bit after picking up.
             */
        
            if (!manipulatorController.getButton(ButtonConfiguration.Manipulator.PICKUP) && settleState != 2) {
                if (settleState == 0) {
                    settleTimer.reset();
                    settleTimer.start();
                    
                    settleState = 1;
                }
                
                if (settleTimer.get() < 0.3) {
                    hopperMotor.set(ActuatorConfiguration.HOPPER_POWER_REVERSE);
                } else {
                    settleTimer.stop();
                    
                    settleState = 2;
                }
            }
            
            /* Toggles the light. */
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.TOGGLE_LIGHT))
                ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
            
            /* Reload the springs. */
            
            elevatorMotors.reload();
            shooterMotors.reload();
            hopperMotor.reload();
            pickupMotor.reload();
            turretRotationMotor.reload();
            
            ringLight.reload();
            
            solenoidShifter.reload();
            solenoidHopper.reload();
            
            /* Debug output. */
            
            SmartDashboard.putDouble("gyroHeading", gyroHeading.getAngle());
            SmartDashboard.putDouble("gyroBalance", gyroBalance.getAngle());
            SmartDashboard.putDouble("accelBalance", accelBalance.getAcceleration());
            
            SmartDashboard.putDouble("encoderLeftDrive", encoderLeftDrive.get());
            SmartDashboard.putDouble("encoderRightDrive", encoderRightDrive.get());
            
            SmartDashboard.putDouble("encoderElevator", encoderElevator.get());
            SmartDashboard.putDouble("encoderTurretRotation", encoderTurretRotation.get());
            
            SmartDashboard.putInt("ups", ((RemoteCameraTCP) cameraInterface).getUPS());
            
            SmartDashboard.putDouble("Turret Output", pidTurretRotation.get());
            SmartDashboard.putDouble("Elevator Output", pidElevator.get());
            
            SmartDashboard.putDouble("Current Turret Setpoint", pidTurretRotation.getSetpoint());
            SmartDashboard.putDouble("Current Elevator Setpoint", pidElevator.getSetpoint());
            
            SmartDashboard.putDouble("Real Turret Setpoint", pidTurretRotation.getRealSetpoint());
            
            SmartDashboard.putDouble("Current Turret Angle", encoderTurretRotation.getDistance());
        }

        pidElevator.disable();
        pidTurretRotation.disable();
        
        compressorPump.stop();
        
        driveTrain.setSafetyEnabled(false);
    }

    /**
     * The robot is disabled.
     * 
     * Like ze goggles, zees does nothing.
     */
    public void disabled() {
        compressorPump.stop();
        driveTrain.setSafetyEnabled(false);
    }
}