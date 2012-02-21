package com._604robotics.robot2012;

import com._604robotics.autonomous.PIDDriveEncoderDifference;
import com._604robotics.autonomous.PIDDriveEncoderOutput;
import com._604robotics.autonomous.PIDDriveGyro;
import com._604robotics.robot2012.configuration.*;
import com._604robotics.utils.DualVictor;
import com._604robotics.utils.XboxController;
import com._604robotics.utils.XboxController.Axis;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.*;
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
    
    Victor shooterMotor;
    Victor hopperMotor;
    Victor pickupMotor;
    
    Victor turretRotationMotor;
    
    Relay ringLight;
    
    Encoder encoderLeftDrive;
    Encoder encoderRightDrive;
    
    Encoder encoderElevator;
    Encoder encoderTurretRotation;
    
    Gyro gyroHeading;
    Gyro gyroBalance;
    Accelerometer accelBalance;
    
    Compressor compressorPump;
    
    DoubleSolenoid solenoidShifter;
    DoubleSolenoid solenoidShooter;
    DoubleSolenoid solenoidPickup;
    
    PIDController pidElevator;
    PIDController pidTurretRotation;

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
        
        shooterMotor = new Victor(PortConfiguration.Motors.SHOOTER);
        hopperMotor = new Victor(PortConfiguration.Motors.HOPPER);
        pickupMotor = new Victor(PortConfiguration.Motors.PICKUP);
        
        turretRotationMotor = new Victor(PortConfiguration.Motors.TURRET_ROTATION);
        
        /* Sets up the ring light relay. */
        
        ringLight = new Relay(PortConfiguration.Relays.RING_LIGHT_PORT, PortConfiguration.Relays.RING_LIGHT_DIRECTION);
        
        /* Sets up the encoders for the drive, elevator, and turret. */
        
        encoderLeftDrive = new Encoder(PortConfiguration.Encoders.Drive.LEFT_A, PortConfiguration.Encoders.Drive.LEFT_B);
        encoderRightDrive = new Encoder(PortConfiguration.Encoders.Drive.RIGHT_A, PortConfiguration.Encoders.Drive.RIGHT_B);
        
        encoderElevator = new Encoder(PortConfiguration.Encoders.ELEVATOR_A, PortConfiguration.Encoders.ELEVATOR_B);
        encoderTurretRotation = new Encoder(PortConfiguration.Encoders.TURRET_ROTATION_A, PortConfiguration.Encoders.TURRET_ROTATION_B);
        
        /* Sets up the gyros and the accelerometer. */
        
        gyroHeading = new Gyro(PortConfiguration.Sensors.GYRO_HEADING);
        gyroBalance = new Gyro(PortConfiguration.Sensors.GYRO_BALANCE);
        accelBalance = new Accelerometer(PortConfiguration.Sensors.ACCELEROMETER);
        accelBalance.setSensitivity(SensorConfiguration.ACCELEROMETER_SENSITIVITY);

        /* Sets up the pneumatics. */
        
        compressorPump = new Compressor(PortConfiguration.Pneumatics.PRESSURE_SWITCH, PortConfiguration.Pneumatics.COMPRESSOR);
        
        solenoidShifter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHIFTER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.SHIFTER_SOLENOID.REVERSE);
        solenoidShooter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHOOTER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.SHOOTER_SOLENOID.REVERSE);
        solenoidPickup = new DoubleSolenoid(PortConfiguration.Pneumatics.PICKUP_SOLENOID.FORWARD, PortConfiguration.Pneumatics.PICKUP_SOLENOID.REVERSE);
        
        solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.LOW_POWER);
        solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
        solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
        
        /*
         * Sets up the PID controllers, and initializes inputs on the
         * SmartDashboard.
         */
        
        pidElevator = new PIDController(0D, 0D, 0D, encoderElevator, elevatorMotors);
        pidTurretRotation = new PIDController(0D, 0D, 0D, encoderTurretRotation, turretRotationMotor);

        pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
        pidTurretRotation.setOutputRange(ActuatorConfiguration.TURRET_ROTATION_POWER_MIN, ActuatorConfiguration.TURRET_ROTATION_POWER_MAX);
        
        SmartDashboard.putDouble("Elevator Setpoint", 0D);
        SmartDashboard.putDouble("Turret Setpoint", 0D);
        
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
        
        int step = (SmartDashboard.getBoolean("In the Middle?", false))
                ? 0
                : 4;

        // TODO: Move some more stuff over to the configuration file. I really don't feel like doing ot right now.
        
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
            switch (step) {
                case 0:
                    solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.OUT);
                    pidDriveStraight.enable();
                    
                    step = 1;
                    
                    break;
                case 1:
                    if (controlTimer.get() >= 6 || (encoderLeftDrive.getDistance() >= AutonomousConfiguration.FORWARD_DISTANCE && encoderRightDrive.getDistance() >= AutonomousConfiguration.FORWARD_DISTANCE)) {
                        pidDriveStraight.disable();
                        driveTrain.tankDrive(0D, 0D);
                        
                        solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
                        
                        controlTimer.reset();
                        step = 2;
                    }
                    
                    break;
                case 2:
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (controlTimer.get() >= 1) {
                        encoderLeftDrive.reset();
                        encoderRightDrive.reset();
                        
                        pidDriveBackwards.enable();
                        
                        step = 3;
                    }
                    
                    break;
                case 3:
                    if (controlTimer.get() >= 6 || (encoderLeftDrive.getDistance() <= AutonomousConfiguration.BACKWARD_DISTANCE && encoderRightDrive.getDistance() >= AutonomousConfiguration.BACKWARD_DISTANCE)) {
                        pidDriveBackwards.disable();
                        driveTrain.tankDrive(0D, 0D);
                        
                        controlTimer.reset();
                        
                        step = 4;
                    }
                    
                    break;
                case 4:
                    pidTurnAround.enable();
                    controlTimer.reset();
                    
                    step = 5;
                    
                    break;
                case 5:
                    if (controlTimer.get() >= 2 || pidTurnAround.onTarget()) {
                        pidTurnAround.disable();
                        controlTimer.stop();
                        
                        driveTrain.tankDrive(0D, 0D);
                        
                        step = 6;
                        
                        break;
                    }
                case 6:
                    // TODO: Shoot. Maybe we could have a single "Shoot" function, that could be called in both Autonomous and Teleop modes? Same goes for aiming.
                    
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
        this.driveTrain.setSafetyEnabled(true);
        //this.compressorPump.start(); /* DISABLED for testing. */

        double accelPower;
        boolean lightOn = false;
        
        manipulatorController.resetToggles();
        
        // TODO: Move over gyro stuff from other project, once it's all hammered out.

        while (isOperatorControl() && isEnabled()) {
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
            
            /* Controls the pickup mechanism. */
            
            if (driveController.getButton(ButtonConfiguration.Driver.PICKUP)) {
                solenoidShooter.set(ActuatorConfiguration.SOLENOID_PICKUP.OUT);
                pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
            } else {
                solenoidShooter.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
                pickupMotor.set(0D);
                
                if (!manipulatorController.getButton(ButtonConfiguration.Manipulator.FIRE))
                    hopperMotor.set(0D);
            }
            
            /* Aims the turret. */

            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.AIM_TURRET)) {
                // TODO: Insert camera control, aiming components, when they're done, of course.
            }
            
            /* Fires at the hoop. */
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.FIRE)) {
                // TODO: Insert firing components, when they're done, of course.
                
                hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
            }
            
            /* Toggles the shooter angle. */
            
            if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
                if (solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE)
                    solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
                else
                    solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            }
            
            /* Toggles the light. */
            
            if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_LIGHT)) {
                lightOn = !lightOn;
                
                if (lightOn)
                    ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
                else
                    ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
            }
            
            /* Automated control for the elevator and turret rotation. */
            
            pidElevator.setSetpoint(SmartDashboard.getDouble("Elevator Setpoint", 0D));
            pidTurretRotation.setSetpoint(SmartDashboard.getDouble("Turret Setpoint", 0D));
            
            SmartDashboard.putDouble("Current Elevator Setpoint", pidElevator.get());
            SmartDashboard.putDouble("Current Turret Setpoint", pidTurretRotation.get());
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.AUTO_ELEVATOR)) {
                pidElevator.enable();
                
                SmartDashboard.putString("Elevator Control", "Auto");
            } else {
                pidElevator.disable();
                elevatorMotors.set(manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
                
                SmartDashboard.putString("Elevator Control", "Manual");
            }
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.AUTO_TURRET)) {
                pidTurretRotation.enable();
                
                SmartDashboard.putString("Turret Control", "Auto");
            } else {
                pidElevator.disable();
                turretRotationMotor.set(manipulatorController.getAxis(Axis.LEFT_STICK_X));
                
                SmartDashboard.putString("Turret Control", "Manual");
            }
            
            /* Debug output. */
            
            SmartDashboard.putDouble("gyroHeading", gyroHeading.getAngle());
            SmartDashboard.putDouble("gyroHeading", gyroHeading.getAngle());
            SmartDashboard.putDouble("accelBalance", accelBalance.getAcceleration());
            
            SmartDashboard.putDouble("encoderLeftDrive", encoderLeftDrive.get());
            SmartDashboard.putDouble("encoderRightDrive", encoderRightDrive.get());
            
            SmartDashboard.putDouble("encoderElevator", encoderElevator.get());
            SmartDashboard.putDouble("encoderTurretRotation", encoderTurretRotation.get());
        }

        //compressorPump.stop(); /* DISABLED for testing. */
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