package com._604robotics.robot2012;

import com._604robotics.robot2012.autonomous.PIDDriveEncoderDifference;
import com._604robotics.robot2012.autonomous.PIDDriveEncoderOutput;
import com._604robotics.robot2012.autonomous.PIDDriveGyro;
import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.*;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.StrangeMachine;
import com._604robotics.robot2012.rotation.DummyRotationProvider;
import com._604robotics.robot2012.rotation.RotationProvider;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.DualVictor;
import com._604robotics.utils.Gyro360;
import com._604robotics.utils.SpringableVictor;
import com._604robotics.utils.XboxController;
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
    
    Relay ringLight;
    
    Encoder encoderLeftDrive;
    Encoder encoderRightDrive;
    
    Encoder encoderElevator;
    Encoder encoderTurretRotation;
    
    DigitalInput turretLimitSwitch;
    
    Gyro360 gyroHeading;
    Gyro gyroBalance;
    Accelerometer accelBalance;
    
    Compressor compressorPump;
    
    DoubleSolenoid solenoidShifter;
    DoubleSolenoid solenoidShooter;
    DoubleSolenoid solenoidPickup;
    DoubleSolenoid solenoidHopper;
    
    PIDController pidElevator;
    PIDController pidTurretRotation;

    StrangeMachine pickupMachine;
    StrangeMachine elevatorMachine;
    
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
        
        ringLight = new Relay(PortConfiguration.Relays.RING_LIGHT_PORT, PortConfiguration.Relays.RING_LIGHT_DIRECTION);
        
        /* Sets up the encoders for the drive, elevator, and turret. */
        
        encoderLeftDrive = new Encoder(PortConfiguration.Encoders.Drive.LEFT_A, PortConfiguration.Encoders.Drive.LEFT_B);
        encoderRightDrive = new Encoder(PortConfiguration.Encoders.Drive.RIGHT_A, PortConfiguration.Encoders.Drive.RIGHT_B);
        
        encoderElevator = new Encoder(PortConfiguration.Encoders.ELEVATOR_A, PortConfiguration.Encoders.ELEVATOR_B);
        encoderTurretRotation = new Encoder(PortConfiguration.Encoders.TURRET_ROTATION_A, PortConfiguration.Encoders.TURRET_ROTATION_B);
        
        /* Sets up the limit switch for the turret. */
        
        turretLimitSwitch = new DigitalInput(PortConfiguration.Sensors.TURRET_LIMIT_SWITCH);
        
        /* Sets up the gyros and the accelerometer. */
        
        gyroHeading = new Gyro360(PortConfiguration.Sensors.GYRO_HEADING);
        gyroBalance = new Gyro(PortConfiguration.Sensors.GYRO_BALANCE);
        accelBalance = new Accelerometer(PortConfiguration.Sensors.ACCELEROMETER);
        accelBalance.setSensitivity(SensorConfiguration.ACCELEROMETER_SENSITIVITY);

        /* Sets up the pneumatics. */
        
        compressorPump = new Compressor(PortConfiguration.Pneumatics.PRESSURE_SWITCH, PortConfiguration.Pneumatics.COMPRESSOR);
        
        solenoidShifter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHIFTER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.SHIFTER_SOLENOID.REVERSE);
        solenoidShooter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHOOTER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.SHOOTER_SOLENOID.REVERSE);
        solenoidPickup = new DoubleSolenoid(PortConfiguration.Pneumatics.PICKUP_SOLENOID.FORWARD, PortConfiguration.Pneumatics.PICKUP_SOLENOID.REVERSE);
        solenoidHopper = new DoubleSolenoid(PortConfiguration.Pneumatics.HOPPER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.HOPPER_SOLENOID.REVERSE);
        
        solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.LOW_POWER);
        solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
        solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
        solenoidHopper.set(ActuatorConfiguration.SOLENOID_HOPPER.REGULAR);
        
        /*
         * Sets up the PID controllers, and initializes inputs on the
         * SmartDashboard.
         */
        
        pidElevator = new PIDController(0D, 0D, 0D, encoderElevator, elevatorMotors);
        pidTurretRotation = new PIDController(0D, 0D, 0D, encoderTurretRotation, turretRotationMotor);

        pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
        pidTurretRotation.setOutputRange(ActuatorConfiguration.TURRET_ROTATION_POWER_MIN, ActuatorConfiguration.TURRET_ROTATION_POWER_MAX);
        
        /* Sets up the Machines. */
        
        pickupMachine = new PickupMachine(solenoidPickup);
        elevatorMachine = new ElevatorMachine(pidElevator);
        
        /* Sets up the rotation provider. */
        
        rotationProvider = new DummyRotationProvider(pidTurretRotation);
        
        /* Sets up the switcher for autonomous. */
        
        inTheMiddle = new SendableChooser();
        inTheMiddle.addDefault("Autonomous: In the Middle", "Yes");
        inTheMiddle.addObject("Autonomous: On the Sides", "No");
        
        SmartDashboard.putData("inTheMiddle", inTheMiddle);
        
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
                    /* Raise the pickup and drive straight. */
                    
                    solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.OUT);
                    pidDriveStraight.enable();
                    
                    step++;
                    
                    break;
                case 1:
                    /* One we're there, stop and smash down the bridge. */
                    
                    if (controlTimer.get() >= 6 || (encoderLeftDrive.getDistance() >= AutonomousConfiguration.FORWARD_DISTANCE && encoderRightDrive.getDistance() >= AutonomousConfiguration.FORWARD_DISTANCE)) {
                        pidDriveStraight.disable();
                        driveTrain.tankDrive(0D, 0D);
                        
                        solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
                        
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
                    /* Drive backward. */
                    
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
        boolean lightOn = false;
        
        boolean upHigh = false;
        
        Target[] targets;
        
        manipulatorController.resetToggles();
        
        // TODO: Move over gyro stuff from other project, once it's all hammered out.

        while (isOperatorControl() && isEnabled()) {
            /* Updates the rotational tracking. */
            
            rotationProvider.update();
            SmartDashboard.putDouble("Current Turret Setpoint", pidTurretRotation.get());
            
            /* Controls the gear shift. */
            
            if (driveController.getButton(ButtonConfiguration.Driver.SHIFT)) {
                solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_POWER);
                SmartDashboard.putString("Gear", "High");
            } else {
                solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.LOW_POWER);
                SmartDashboard.putString("Gear", "Low");
            }
            
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
            
            if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_HEIGHT))
                upHigh = !upHigh;
            
            /*
             * If the LIFT button is pressed, make sure the elevator is up high
             * enough, then lift up the pickup.
             * 
             * Else, check the "default" position. If it is up high, then lower
             * the pickup and raise the elevator simultaneously. If it is down
             * low, then make sure the pickup is down first, and then lower the
             * elevator.
             */
            
            if (manipulatorController.getButton(ButtonConfiguration.Driver.LIFT)) {
                if ((upHigh && elevatorMachine.crank(ElevatorState.HIGH)) || elevatorMachine.crank(ElevatorState.MEDIUM))
                    pickupMachine.crank(PickupState.IN);
            } else {
                if (upHigh) {
                    pickupMachine.crank(PickupState.OUT);
                    elevatorMachine.crank(ElevatorState.HIGH);
                } else if (pickupMachine.crank(PickupState.OUT)) {
                    /*
                     * If the pickup is down and the elevator is at rest, then
                     * allow the user to trigger the pickup mechanism.
                     */
                    
                    if (elevatorMachine.crank(ElevatorState.LOW)) {
                        /* Controls the pickup mechanism. */

                        if (driveController.getButton(ButtonConfiguration.Manipulator.PICKUP)) {
                            pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                            hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                        }
                    }
                }
            }
        
            /*
             * If the default position is set up high, and the elevator is at
             * that position now, allow the user to toggle the angle, aim the
             * turret, and shoot.
             */
            
            if (upHigh && elevatorMachine.test(ElevatorState.HIGH)) {
                SmartDashboard.putString("Ready to Shoot", "Yes");
                
                /* Toggles the shooter angle. */

                if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
                    if (solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE)
                        solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
                    else
                        solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
                }
                
                /* Aims the turret based on the vision output. */
                
                if (manipulatorController.getButton(ButtonConfiguration.Manipulator.AIM_AND_FIRE)) {
                    if (!pidTurretRotation.isEnable())
                        pidTurretRotation.enable();
                    
                    if (pidTurretRotation.onTarget()) {
                        /* Fires the shooter. */
                        
                        // TODO: Put in more firing, physics stuff, etc.
                        
                        shooterMotors.set(ActuatorConfiguration.SHOOTER_POWER);
                        hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                        solenoidHopper.set(ActuatorConfiguration.SOLENOID_HOPPER.PUSH);
                    } else {
                        solenoidHopper.set(ActuatorConfiguration.SOLENOID_HOPPER.REGULAR);
                    }
                } else {
                    if (pidTurretRotation.isEnable())
                        pidTurretRotation.disable();
                    
                    solenoidHopper.set(ActuatorConfiguration.SOLENOID_HOPPER.REGULAR);
                }
            } else {
                if (pidTurretRotation.isEnable())
                    pidTurretRotation.disable();

                solenoidHopper.set(ActuatorConfiguration.SOLENOID_HOPPER.REGULAR);

                SmartDashboard.putString("Ready to Shoot", "No");
            }
            
            SmartDashboard.putBoolean("Turret Tracking?", pidTurretRotation.isEnable());
            SmartDashboard.putBoolean("Turret On Target?", pidTurretRotation.onTarget());
            
            /* Toggles the light. */
            
            if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_LIGHT)) {
                lightOn = !lightOn;
                
                if (lightOn)
                    ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
                else
                    ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
            }
            
            /* Reload the springs of the Victors. */
            
            elevatorMotors.reload();
            
            shooterMotors.reload();
            hopperMotor.reload();
            pickupMotor.reload();
            
            turretRotationMotor.reload();
            
            /* Debug output. */
            
            SmartDashboard.putDouble("gyroHeading", gyroHeading.getAngle());
            SmartDashboard.putDouble("gyroBalance", gyroBalance.getAngle());
            SmartDashboard.putDouble("accelBalance", accelBalance.getAcceleration());
            
            SmartDashboard.putDouble("encoderLeftDrive", encoderLeftDrive.get());
            SmartDashboard.putDouble("encoderRightDrive", encoderRightDrive.get());
            
            SmartDashboard.putDouble("encoderElevator", encoderElevator.get());
            SmartDashboard.putDouble("encoderTurretRotation", encoderTurretRotation.get());
            
            SmartDashboard.putInt("ups", ((RemoteCameraTCP) cameraInterface).getUPS());
        }

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