package com._604robotics.robot2012;

import com._604robotics.robot2012.configuration.PortConfiguration;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.configuration.SensorConfiguration;
import com._604robotics.utils.XboxController;
import com._604robotics.utils.XboxController.Axis;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot2012Orange extends SimpleRobot {
    XboxController driveController;
    XboxController manipulatorController;
    
    RobotDrive driveTrain;
    
    Victor elevatorLeftMotor;
    Victor elevatorRightMotor;
    
    Victor shooterMotor;
    Victor hopperMotor;
    Victor pickupMotor;
    
    Victor turretRotationMotor;
    
    Relay ringLight;
    
    Encoder encoderLeftFrontDrive;
    Encoder encoderLeftRearDrive;
    Encoder encoderRightFrontDrive;
    Encoder encoderRightRearDrive;
    
    Encoder encoderElevator;
    Encoder encoderTurretRotation;
    
    Gyro gyroHeading;
    Gyro gyroBalance;
    Accelerometer accelBalance;
    
    Compressor compressorPump;
    
    DoubleSolenoid solenoidShifter;
    DoubleSolenoid solenoidShooter;
    DoubleSolenoid solenoidPickup;

    public Robot2012Orange() {
        this.getWatchdog().setEnabled(false);
    }

    public void robotInit () {
        driveController = new XboxController(PortConfiguration.Controllers.DRIVE);
        manipulatorController = new XboxController(PortConfiguration.Controllers.MANIPULATOR);
        
        driveTrain = new RobotDrive(new Victor(PortConfiguration.Motors.LEFT_DRIVE), new Victor(PortConfiguration.Motors.RIGHT_DRIVE));
        driveTrain.setSafetyEnabled(false);

        elevatorLeftMotor = new Victor(PortConfiguration.Motors.ELEVATOR_LEFT);
        elevatorRightMotor = new Victor(PortConfiguration.Motors.ELEVATOR_RIGHT);
        
        shooterMotor = new Victor(PortConfiguration.Motors.SHOOTER);
        hopperMotor = new Victor(PortConfiguration.Motors.HOPPER);
        pickupMotor = new Victor(PortConfiguration.Motors.PICKUP);
        
        turretRotationMotor = new Victor(PortConfiguration.Motors.TURRET_ROTATION);
        
        ringLight = new Relay(PortConfiguration.Relays.RING_LIGHT_PORT, PortConfiguration.Relays.RING_LIGHT_DIRECTION);
        
        encoderLeftFrontDrive = new Encoder(PortConfiguration.Encoders.Drive.FRONT_LEFT_A, PortConfiguration.Encoders.Drive.FRONT_LEFT_B);
        encoderRightFrontDrive = new Encoder(PortConfiguration.Encoders.Drive.FRONT_RIGHT_A, PortConfiguration.Encoders.Drive.FRONT_RIGHT_B);
        encoderLeftRearDrive = new Encoder(PortConfiguration.Encoders.Drive.REAR_LEFT_A, PortConfiguration.Encoders.Drive.REAR_LEFT_B);
        encoderRightRearDrive = new Encoder(PortConfiguration.Encoders.Drive.REAR_RIGHT_A, PortConfiguration.Encoders.Drive.REAR_RIGHT_B);
        
        encoderElevator = new Encoder(PortConfiguration.Encoders.ELEVATOR_A, PortConfiguration.Encoders.ELEVATOR_B);
        encoderTurretRotation = new Encoder(PortConfiguration.Encoders.TURRET_ROTATION_A, PortConfiguration.Encoders.TURRET_ROTATION_B);
        
        gyroHeading = new Gyro(PortConfiguration.Sensors.GYRO_HEADING);
        gyroBalance = new Gyro(PortConfiguration.Sensors.GYRO_BALANCE);
        accelBalance = new Accelerometer(PortConfiguration.Sensors.ACCELEROMETER);
        accelBalance.setSensitivity(SensorConfiguration.ACCELEROMETER_SENSITIVITY);

        compressorPump = new Compressor(PortConfiguration.Pneumatics.PRESSURE_SWITCH, PortConfiguration.Pneumatics.COMPRESSOR);
        
        solenoidShifter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHIFTER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.SHIFTER_SOLENOID.REVERSE);
        solenoidShooter = new DoubleSolenoid(PortConfiguration.Pneumatics.SHOOTER_SOLENOID.FORWARD, PortConfiguration.Pneumatics.SHIFTER_SOLENOID.REVERSE);
        solenoidPickup = new DoubleSolenoid(PortConfiguration.Pneumatics.PICKUP_SOLENOID.FORWARD, PortConfiguration.Pneumatics.PICKUP_SOLENOID.REVERSE);
        
        solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.LOW_POWER);
        solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
        solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
        
        System.out.println("Hello, ninja h4X0r.");
    }
    
    public static boolean isInRange(double xValue, double upperRange, double lowerRange) { // Self-explanatory.
        return xValue <= upperRange && xValue >= lowerRange;
    }

    public static double deadband(double xValue, double upperBand, double lowerBand, double correctedValue) {
        return (isInRange(xValue, upperBand, lowerBand))
                ? correctedValue
                : xValue;
    }

    public static double deadband(double xValue) {
        return deadband(xValue, .1745, -.1745, 0.0);
    }

    public void autonomous() {
        driveTrain.setSafetyEnabled(false);
            // TODO: TEMPORARY, until actual autonomous code is written.
        compressorPump.start();

        while (isAutonomous() && isEnabled()) {
            // TODO: Write autonomous mode code
        }

        compressorPump.stop();
    }

    public void operatorControl() {
        this.driveTrain.setSafetyEnabled(true);
        this.compressorPump.start();

        double accelPower = 0;
        boolean lightOn = false;

        // TODO: Move over gyro stuff from other project, once it's all hammered out.

        while (isOperatorControl() && isEnabled()) {
            if (driveController.getButton(ButtonConfiguration.Driver.SHIFT)) {
                solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_POWER);
                SmartDashboard.putString("Gear", "High");
            } else {
                solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.LOW_POWER);
                SmartDashboard.putString("Gear", "Low");
            }

            if (driveController.getButton(ButtonConfiguration.Driver.AUTO_BALANCE)) {
                // TODO: Replace this with stuff from the balancing code, once it's hammered out.
                
                accelPower = deadband(MathUtils.asin(accelBalance.getAcceleration())) / SensorConfiguration.ACCELEROMETER_UPPER_RADIANS * ActuatorConfiguration.ACCELEROMETER_DRIVE_POWER;
                driveTrain.tankDrive(accelPower, accelPower);
                
                SmartDashboard.putString("Drive Mode", "Balancing");
                SmartDashboard.putDouble("Accel Output", accelPower);
            } else {
                driveTrain.tankDrive(driveController.getAxis(Axis.LEFT_STICK_Y), driveController.getAxis(Axis.RIGHT_STICK_Y)); // Tank drive with left and right sticks on Xbox controller.
                SmartDashboard.putString("Drive Mode", "Manual");
            }
            
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

            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.AIM_TURRET)) {
                // TODO: Insert camera control, aiming components, when they're done, of course.
            }
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.FIRE)) {
                // TODO: Insert firing components, when they're done, of course.
                
                shooterMotor.set(ActuatorConfiguration.HOPPER_POWER);
            }
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
                if (solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE)
                    solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
                else
                    solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            }
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.TOGGLE_LIGHT)) {
                lightOn = !lightOn;
                
                if (lightOn)
                    ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
                else
                    ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
            }
            
            turretRotationMotor.set(manipulatorController.getAxis(Axis.LEFT_STICK_X));
            
            elevatorLeftMotor.set(manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
            elevatorRightMotor.set(manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
            
            SmartDashboard.putDouble("gyroHeading", gyroHeading.getAngle());
            SmartDashboard.putDouble("gyroHeading", gyroHeading.getAngle());
            SmartDashboard.putDouble("accelBalance", accelBalance.getAcceleration());
            
            SmartDashboard.putDouble("encoderLeftFrontDrive", encoderLeftFrontDrive.get());
            SmartDashboard.putDouble("encoderRightFrontDrive", encoderRightFrontDrive.get());
            SmartDashboard.putDouble("encoderLeftRearDrive", encoderLeftRearDrive.get());
            SmartDashboard.putDouble("encoderRightRearDrive", encoderRightRearDrive.get());
            
            SmartDashboard.putDouble("encoderElevator", encoderElevator.get());
            SmartDashboard.putDouble("encoderTurretRotation", encoderTurretRotation.get());
        }

        compressorPump.stop();
        driveTrain.setSafetyEnabled(false);
    }

    public void disabled() {
        compressorPump.stop();
        driveTrain.setSafetyEnabled(false);
    }
}