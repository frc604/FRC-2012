package com._604robotics.robot2012;

import com._604robotics.robot2012.autonomous.PIDDriveEncoderDifference;
import com._604robotics.robot2012.autonomous.PIDDriveEncoderOutput;
import com._604robotics.robot2012.autonomous.PIDDriveGyro;
import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.*;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.robot2012.machine.*;
import com._604robotics.robot2012.machine.TurretMachine.TurretState;
import com._604robotics.robot2012.rotation.DummyRotationProvider;
import com._604robotics.robot2012.rotation.RotationProvider;
import com._604robotics.utils.UpDownPIDController.Gains;
import com._604robotics.utils.*;
import com._604robotics.utils.XboxController.Axis;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main class for the 2012 robot code.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * @author  Aaron Wang <aaronw94@gmail.com>
 */
public class Robot2012Orange extends SimpleRobot {
    XboxController driveController;
    XboxController manipulatorController;
    
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
    Encoder encoderTurretRotation;
    
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
    PIDController pidTurretRotation;

    StrangeMachine pickupMachine;
    StrangeMachine elevatorMachine;
    StrangeMachine turretMachine;
    ShooterMachine shooterMachine;
    
    RotationProvider rotationProvider;
    
    SendableChooser inTheMiddle;
    
    CameraInterface cameraInterface;
    
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
        encoderTurretRotation = new Encoder(PortConfiguration.Encoders.TURRET_ROTATION_A, PortConfiguration.Encoders.TURRET_ROTATION_B);
        
        encoderLeftDrive.setDistancePerPulse(SensorConfiguration.Encoders.LEFT_DRIVE_INCHES_PER_CLICK);
        encoderRightDrive.setDistancePerPulse(SensorConfiguration.Encoders.RIGHT_DRIVE_INCHES_PER_CLICK);
        
        encoderTurretRotation.setDistancePerPulse(SensorConfiguration.Encoders.TURRET_DEGREES_PER_CLICK);
        
        encoderElevator.setOffset(525);
        
        encoderLeftDrive.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
        encoderRightDrive.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
        
        encoderTurretRotation.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
        
        encoderLeftDrive.start();
        encoderRightDrive.start();
        
        encoderElevator.start();
        encoderTurretRotation.start();
        
        /* Sets up the limit switch for the turret. */
        
        turretLimitSwitch = new DigitalInput(PortConfiguration.Sensors.TURRET_LIMIT_SWITCH);
        
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
        
        DeadbandedSource elevatorSource = new DeadbandedSource(encoderElevator);
        
        pidElevator = new UpDownPIDController(new Gains(0.0085, 0D, 0.018), new Gains(0.0029, 0.000003, 0.007), elevatorSource, elevatorMotors);
        pidTurretRotation = new PIDController(-0.0022, -0.0008, -0.006, encoderTurretRotation, turretRotationMotor);
        
        elevatorSource.setController(pidElevator);
        elevatorSource.setDeadband(-5D, 5D);
        
        pidElevator.setInputRange(0, 1550);
        pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
        pidElevator.setSetpoint(822);
        pidTurretRotation.setOutputRange(ActuatorConfiguration.TURRET_ROTATION_POWER_MIN, ActuatorConfiguration.TURRET_ROTATION_POWER_MAX);
        
        elevatorMotors.setController(pidElevator);
        turretRotationMotor.setController(pidTurretRotation);
        
        /* Sets up the rotation provider. */
        
        rotationProvider = new DummyRotationProvider(pidTurretRotation);
        
        /* Sets up the Machines. */
        
        pickupMachine = new PickupMachine(solenoidPickup);
        elevatorMachine = new ElevatorMachine(pidElevator, encoderElevator);
        turretMachine = new TurretMachine(pidTurretRotation, rotationProvider);
        shooterMachine = new ShooterMachine(shooterMotors, hopperMotor);
        
        /* Sets up the switcher for autonomous. */
        
        inTheMiddle = new SendableChooser();
        inTheMiddle.addDefault("Autonomous: In the Middle", "Yes");
        inTheMiddle.addObject("Autonomous: On the Sides", "No");
        
        SmartDashboard.putData("inTheMiddle", inTheMiddle);
        
        SmartDashboard.putDouble("Shooter Speed", 1D);
        
        /* Sets up the camera inteface. */
        
        cameraInterface = new RemoteCameraTCP();
        cameraInterface.begin();
                
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
    public static boolean isInRange(double xValue, double upperRange, double lowerRange) { // Self-explanatory.
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
    
    public void aimAndShoot() {
        ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
        
        /*
         * Aim, or make sure we're aimed. Then fire.
         */
        
        if (turretMachine.crank(TurretState.AIMED)) {
            // TODO: Add actual firing stuff here.
            
            solenoidHopper.set(ActuatorConfiguration.SOLENOID_HOPPER.PUSH);
            shooterMachine.crank(ShooterState.SHOOTING);
        }
    }

    /**
     * Automated drive for autonomous mode.
     * 
     * It's not done yet.
     */
    public void autonomous() {
        // TODO: Calibrate encoders.
        
        compressorPump.start();
        
        int step = 0;
        double backwardDistance = AutonomousConfiguration.BACKWARD_DISTANCE;
        
        int calibrationState = 0;
        
        /*
         * If we're not in the middle, skip over the bridge stuff, and change
         * the backward distance.
         */
        
        if (((String) inTheMiddle.getSelected()).equals("Yes")) {
            step = 3;
            backwardDistance = AutonomousConfiguration.BACKWARD_DISTANCE_SIDES;
        }

        // TODO: Move some more stuff over to the configuration file. I really don't feel like doing ot right now.
        
        /* Set up the PID controllers. */
        
        PIDController pidDriveStraight = new PIDController(0D, 0D, 0D, new PIDDriveEncoderDifference(encoderLeftDrive, encoderRightDrive), new PIDDriveEncoderOutput(driveTrain));
        PIDController pidDriveBackwards = new PIDController(0D, 0D, 0D, new PIDDriveEncoderDifference(encoderLeftDrive, encoderRightDrive), new PIDDriveEncoderOutput(driveTrain, true));
        PIDController pidTurnAround = new PIDController(0D, 0D, 0D, gyroHeading, new PIDDriveGyro(driveTrain));
        
        pidDriveStraight.setOutputRange(-0.5D, 0.5D);
        pidDriveStraight.setOutputRange(-0.5D, 0.5D);
        pidDriveStraight.setOutputRange(-0.5D, 0.5D);
        
        pidDriveStraight.setSetpoint(0D);
        pidDriveBackwards.setSetpoint(0D);
        pidTurnAround.setSetpoint(180D);
        
        Timer controlTimer = new Timer();
        controlTimer.start();
        
        while (isAutonomous() && isEnabled()) {
            /* Calibrate the turret while everything else is going on. */
            
            if (step < 8) {
                switch (calibrationState) {
                    case 0:
                        if (turretLimitSwitch.get()) {
                            turretRotationMotor.set(-0.4);
                        } else {
                            turretRotationMotor.set(0D);
                            encoderTurretRotation.reset();
                            
                            step++;
                        }
                        break;
                    case 1:
                        pidTurretRotation.setSetpoint(90D);
                        
                        if (!pidTurretRotation.isEnable())
                            pidTurretRotation.enable();
                        
                        if (pidTurretRotation.onTarget()) {
                            pidTurretRotation.disable();
                            encoderTurretRotation.reset();
                            
                            step++;
                        }
                        
                        break;
                }
            }
            
            /* Handle the main logic. */
            
            switch (step) {
                case 0:
                    /* Drive straight. */
                    
                    pidDriveStraight.enable();
                    
                    step++;
                    
                    break;
                case 1:
                    /* One we're there, stop and smash down the bridge. */
                    
                    if (controlTimer.get() >= 6 || (encoderLeftDrive.getDistance() >= AutonomousConfiguration.FORWARD_DISTANCE && encoderRightDrive.getDistance() >= AutonomousConfiguration.FORWARD_DISTANCE)) {
                        pidDriveStraight.disable();
                        driveTrain.tankDrive(0D, 0D);
                        
                        solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.OUT);
                        
                        controlTimer.reset();
                        step++;
                    }
                    
                    break;
                case 2:
                    /* Wait a second. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (controlTimer.get() >= 1) 
                        step++;
                    
                    break;
                case 3:
                    /* Make sure the pickup is down, and drive backward. */
                        
                    solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.OUT);
                    
                    encoderLeftDrive.reset();
                    encoderRightDrive.reset();

                    pidDriveBackwards.enable();

                    step++;
                    
                    break;
                case 4:
                    /* Once we're there, stop. */
                    
                    if (controlTimer.get() >= 6 || (encoderLeftDrive.getDistance() <= backwardDistance && encoderRightDrive.getDistance() >= backwardDistance)) {
                        pidDriveBackwards.disable();
                        driveTrain.tankDrive(0D, 0D);
                        
                        controlTimer.reset();
                        
                        step++;
                    }
                    
                    break;
                case 5:
                    /* Turn around, bright eyes! */
                    
                    pidTurnAround.enable();
                    controlTimer.reset();
                    
                    step++;
                    
                    break;
                case 6:
                    /* Stop turning around once we've done a 180. */
                    
                    if (controlTimer.get() >= 2 || pidTurnAround.onTarget()) {
                        pidTurnAround.disable();
                        controlTimer.stop();
                        
                        driveTrain.tankDrive(0D, 0D);
                        
                        step++;
                        
                        break;
                    }
                case 7:
                    /* Block until the turret is finished being calibrated. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (calibrationState > 1)
                        step++;
                case 8:
                    // TODO: Aim and shoot. Maybe we could have a single "Shoot" function, that could be called in both Autonomous and Teleop modes? Same goes for aiming.
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    break;
            }
        }
        
        pidDriveStraight.disable();
        pidDriveBackwards.disable();
        pidTurnAround.disable();

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

        double accelPower;
        
        boolean upHigh = false;
        boolean pickupIn = true;
        
        boolean noFixedDirection = true;
        int turretDirection = TurretState.FORWARD;
        
        int settleState = 2;
        Timer settleTimer = new Timer();
        
        manipulatorController.resetToggles();
        driveController.resetToggles();
        
        pidElevator.reset();
        pidTurretRotation.reset();
        
        while (isOperatorControl() && isEnabled()) {
            shooterMachine.setShooterSpeed(SmartDashboard.getDouble("Shooter Speed", 1D));
            
            /* Controls the gear shift. */
            
            if (driveController.getButton(ButtonConfiguration.Driver.SHIFT))
                solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR);
            
            /* 
             * Automatically balances the robot on the bridge. Well, that's the
             * plan, anyway.
             */

            if (driveController.getButton(ButtonConfiguration.Driver.AUTO_BALANCE)) {
                // TODO: Replace this with stuff from the balancing code, once it's hammered out.
                
                accelPower = deadband(MathUtils.asin(accelBalance.getAcceleration()), 0.1745, -0.1745, 0D) / SensorConfiguration.ACCELEROMETER_UPPER_RADIANS * ActuatorConfiguration.ACCELEROMETER_DRIVE_POWER;
                driveTrain.tankDrive(accelPower, accelPower);
                
                SmartDashboard.putString("Drive Mode", "Balancing");
                SmartDashboard.putDouble("Accel Output", accelPower);
            } else {
                driveTrain.tankDrive(driveController.getAxis(Axis.LEFT_STICK_Y), driveController.getAxis(Axis.RIGHT_STICK_Y)); // Tank drive with left and right sticks on Xbox controller.
                SmartDashboard.putString("Drive Mode", "Manual");
            }
            
            /* Toggle the "default" height between "up high" and "down low". */
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.FORWARD)) {
                upHigh = true;
                noFixedDirection = false;
                turretDirection = TurretState.FORWARD;
            } else if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.LEFT)) {
                upHigh = true;
                noFixedDirection = false;
                turretDirection = TurretState.LEFT;
            } else if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.RIGHT)) {
                upHigh = true;
                noFixedDirection = false;
                turretDirection = TurretState.RIGHT;
            } else if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.BACKWARD)) {
                upHigh = true;
                noFixedDirection = false;
                turretDirection = TurretState.BACKWARD;
            } else if (manipulatorController.getStick(ButtonConfiguration.Manipulator.Elevator.DOWN)) {
                upHigh = false;
            }
            
            /* Toggle the pickup state between "up" and "down". */
            
            if (driveController.getToggle(ButtonConfiguration.Driver.TOGGLE_PICKUP))
                pickupIn = !pickupIn;
            
            /*
             * If pickupIn is true, or upHigh is true, then make sure the elevator
             * is up high enough, then lift up the pickup.
             * 
             * Else, check the "default" position. If it is up high, then lower
             * the pickup and raise the elevator simultaneously. If it is down
             * low, then make sure the pickup is down first, and then lower the
             * elevator.
             */
            
            System.out.println("pickupIn: " + pickupIn);
            System.out.println("upHigh: " + upHigh);
            
            if (pickupIn || upHigh) {
                if (elevatorMachine.test(ElevatorState.PICKUP_OKAY)) {
                    pickupMachine.crank(PickupState.IN);
                    
                    if (upHigh && noFixedDirection)
                        noFixedDirection = turretMachine.crank(turretDirection);
                }
                
                if (upHigh) {
                    if (elevatorMachine.crank(ElevatorState.HIGH)) {
                        SmartDashboard.putString("Ready to Shoot", "Yes");
                        
                        /* Toggles the shooter angle. */
                        
                        if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
                            if (solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE)
                                solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
                            else
                                solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
                        }
                        
                        if (manipulatorController.getButton(ButtonConfiguration.Manipulator.AIM_AND_SHOOT))
                            this.aimAndShoot();
                    }
                } else {
                    elevatorMachine.crank(ElevatorState.MEDIUM);
                }
            } else {
                boolean b = turretMachine.crank(TurretState.SIDEWAYS);
                System.out.println("Turret Is There (Faked)? " + b);
                if (b) {
                    /*
                     * To prevent us from unintentionally violating <G21>, the
                     * pickup cannot go out until the turret is in the sideways
                     * position.
                     */
                    
                    boolean c = pickupMachine.crank(PickupState.OUT);
                    System.out.println("Pickup Is Out? " + c);
                    if (c) {
                        /*
                         * If the pickup is down and the elevator is at rest,
                         * then allow the user to trigger the pickup mechanism.
                         */
                        
                        boolean d = elevatorMachine.crank(ElevatorState.LOW);
                        System.out.println("Elevator Is Down? " + d);
                        if (d) {
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
                
                if (settleTimer.get() < 0.5) {
                    hopperMotor.set(ActuatorConfiguration.HOPPER_POWER_REVERSE);
                } else {
                    settleTimer.stop();
                    
                    settleState = 2;
                }
            }
            
            /* Toggles the light. */
            
            if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_LIGHT))
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