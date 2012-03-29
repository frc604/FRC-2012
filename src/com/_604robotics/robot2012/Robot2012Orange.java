// TOOD: Not let Mrs. Arrington leave without the envelope.

package com._604robotics.robot2012;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.configuration.PortConfiguration;
import com._604robotics.robot2012.firing.CameraFiringProvider;
import com._604robotics.robot2012.firing.ManualFiringProvider;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.robot2012.speedcontrol.ProcessSpeedProvider;
import com._604robotics.robot2012.speedcontrol.SpeedProvider;
import com._604robotics.utils.StrangeMachine;
import com._604robotics.utils.UpDownPIDController.Gains;
import com._604robotics.utils.*;
import com._604robotics.utils.XboxController.Axis;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main class for the 2012 robot code codenamed Orange.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * @author  Aaron Wang <aaronw94@gmail.com>
 * @author  Colin Aitken <cacolinerd@gmail.com>
 * @author  Alan Li <alanpusongli@gmail.com>
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
    
    SpringableRelay ringLight;
    
    EncoderPIDSource encoderElevator;
    EncoderSamplingRate encoderShooter;
    
    DigitalInput elevatorLimitSwitch;
    
    Gyro360 gyroHeading;
    
    Compressor compressorPump;
    
    SpringableDoubleSolenoid solenoidShifter;
    DoubleSolenoid solenoidShooter;
    DoubleSolenoid solenoidPickup;
    SpringableDoubleSolenoid solenoidHopper;
    
    UpDownPIDController pidElevator;

    StrangeMachine pickupMachine;
    StrangeMachine elevatorMachine;
    ShooterMachine shooterMachine;
    
    SendableChooser inTheMiddle;
    
    CameraInterface cameraInterface;
    
    CameraFiringProvider firingProvider;
    SpeedProvider speedProvider;
    
    boolean upHigh = false;
    boolean pickupIn = true;
    
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
        encoderShooter.setAveragePoints(10);
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
        
        /*
         * Sets up the PID controllers, and initializes inputs on the
         * SmartDashboard.
         */
        
        SmartDashboard.putDouble("Elevator Up P", 0.0085);
        SmartDashboard.putDouble("Elevator Up I", 0D);
        SmartDashboard.putDouble("Elevator Up D", 0.018);
        
        SmartDashboard.putDouble("Elevator Down P", 0.0029);
        SmartDashboard.putDouble("Elevator Down I", 0.000003);
        SmartDashboard.putDouble("Elevator Down D", 0.007);
        
        pidElevator = new UpDownPIDController(new Gains(getDouble("Elevator Up P", 0.0085), getDouble("Elevator Up I", 0D), getDouble("Elevator Up D", 0.018)), new Gains(getDouble("Elevator Down P", 0.0029), getDouble("Elevator Down I", 0.000003), getDouble("Elevator Down P", 0.007)), encoderElevator, elevatorMotors);
        
        pidElevator.setInputRange(0, 1550);
        pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
        pidElevator.setSetpoint(822);
        
        elevatorMotors.setController(pidElevator);
        
        /* Sets up the switcher for autonomous. */
        
        inTheMiddle = new SendableChooser();
        inTheMiddle.addDefault("Autonomous: In the Middle", "Yes");
        inTheMiddle.addObject("Autonomous: On the Sides", "No");
        
        SmartDashboard.putData("inTheMiddle", inTheMiddle);
        
        /* Sets up the camera inteface. */
        
        cameraInterface = new RemoteCameraTCP();
        cameraInterface.begin();
        
        /* Sets up the firing provider. */
        
        firingProvider = new CameraFiringProvider(cameraInterface, new ManualFiringProvider());
        
        /* Sets up the speed provider for the shooter. */
        
        speedProvider = new ProcessSpeedProvider(0D, 0D, 0D, encoderShooter, shooterMotors);
        
        /* Sets up the Machines. */
        
        pickupMachine = new PickupMachine(solenoidPickup);
        elevatorMachine = new ElevatorMachine(pidElevator, encoderElevator);
        shooterMachine = new ShooterMachine(hopperMotor, firingProvider, speedProvider);
        
        /* Sets up debug outputs. */
        
        SmartDashboard.getBoolean("Elevator Calibrated", false);
        
        SmartDashboard.putDouble("Confidence Threshold", 0.7);
        SmartDashboard.putDouble("Target Timeout", 1.5);
        SmartDashboard.putDouble("Steady Threshold", 0.5);
        SmartDashboard.putDouble("Unsteady Threshold", 1D);
        
        SmartDashboard.putDouble("Auton: Step 2", AutonomousConfiguration.STEP_2_SHOOT_TIME);
        SmartDashboard.putDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_TURN_TIME);
        SmartDashboard.putDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_DRIVE_TIME);
        SmartDashboard.putDouble("Auton: Step 6", AutonomousConfiguration.STEP_6_WAIT_TIME);
        SmartDashboard.putDouble("Auton: Max Step", AutonomousConfiguration.MAX_STEP);
        
        /* Because we can. */
        
        System.out.println("Hello, ninja h4X0r.");
    }
    
    /**
     * Resets the motors.
     * 
     * @param   driveToo    Reset the drive train too?
     */
    public void resetMotors (boolean driveToo) {
        if (driveToo)
            driveTrain.tankDrive(0D, 0D);

        speedProvider.reset();
        
        elevatorMotors.reload();
        shooterMotors.reload();
        hopperMotor.reload();
        ringLight.reload();
    }
    
    /**
     * Resets the motors, but not the drive train.
     */
    public void resetMotors () {
        this.resetMotors(false);
    }
    
    /**
     * Automated drive for autonomous mode.
     * 
     * If in middle, drive forward, knock down bridge, turn around.
     * 
     * Else, or then, go ahead and try to score.
     */
    public void autonomous() {
        compressorPump.start();
        
        int step = 1;
        
        double drivePower;
        double gyroAngle;
        
        boolean turnedAround = false;
        
        boolean kinect = false;
        boolean abort = false;
        
        /* Reset stuff. */
        
        upHigh = false;
        pickupIn = true;
        
        /* Set stuff up. */
        
        Timer controlTimer = new Timer();
        controlTimer.start();

        Timer calibrationTimer = new Timer();
        calibrationTimer.start();
        
        elevatorMotors.set(0D);
        
        gyroHeading.reset();
        
        while (isAutonomous() && isEnabled()) {
            kinect = leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
            abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
            
            if (kinect || abort)
                break;
            
            if (step > getDouble("Auton: Max Step", AutonomousConfiguration.MAX_STEP)) {
                SmartDashboard.putInt("STOPPED AT", step);
                this.resetMotors();

                continue;
            } else {
                SmartDashboard.putInt("STOPPED AT", -1);
            }
            
            /* Handle the main logic. */
            
            SmartDashboard.putInt("CURRENT STEP", step);
            SmartDashboard.putDouble("CONTROL TIMER", controlTimer.get());
            
            switch (step) {
                case 1:
                    /* Put the elevator up. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (elevatorMachine.crank(ElevatorState.HIGH))
                        step++;
                    
                    break;
                case 2:
                    /* Shoot! */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    // TODO - sense drops in encoder values as shot balls
                    if (controlTimer.get() < AutonomousConfiguration.STEP_2_SHOOT_TIME)
                        shooterMachine.crank(ShooterState.SHOOTING);
                    else if (((String) inTheMiddle.getSelected()).equals("Yes"))
                        step++;
                    
                    break;
                case 3:
                    /* Turn around and face the bridge. */
                    
                    if (controlTimer.get() <= getDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_TURN_TIME)) {
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
                case 4:
                    /* Put the elevator down. */
                    
                    if (elevatorMachine.crank(ElevatorState.MEDIUM))
                        step++;
                    
                    break;
                case 5:
                    /* Drive forward and stop, then smash down the bridge. */
                    
                    if (controlTimer.get() <= AutonomousConfiguration.STEP_5_DRIVE_TIME) {
                        SmartDashboard.putString("STAGE", "DRIVING");
                        drivePower = Math.min(-0.2, (1 - controlTimer.get() / getDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_DRIVE_TIME)) * -1);
                        SmartDashboard.putDouble("AUTON DRIVE POWER", drivePower);
                        driveTrain.tankDrive(drivePower, drivePower);
                    } else {
                        SmartDashboard.putString("STAGE", "SMASHING!");
                        driveTrain.tankDrive(0D, 0D);
                        pickupMachine.crank(PickupState.OUT);
                        
                        controlTimer.reset();
                        step++;
                    }
                    
                    break;
                case 6:
                    /* Wait a bit. */
                    
                    driveTrain.tankDrive(0D, 0D);
                    
                    if (controlTimer.get() >= getDouble("Auton: Step 6", AutonomousConfiguration.STEP_6_WAIT_TIME)) 
                        step++;
                    
                    break;
                case 7:
                    /* Pull in the pickup. */
                    
                    pickupMachine.crank(PickupState.IN);
                    
                    break;
            }
            
            this.resetMotors();
        }
        
        System.out.println("BROKEN OUT OF AUTON");
        System.out.println("isAutonomous(): " + isAutonomous() + ", isEnabled(): " + isEnabled() + ", abort: " + abort + ", kinect: " + kinect);
        
        while (isAutonomous() && isEnabled() && !abort && !kinect) {
            kinect = leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
            abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
            
            System.out.println("WAITING FOR KINECT");
        
            this.resetMotors(true);
        }
        
        if (kinect) {
            System.out.println("KINECT ON");
        
            while (isAutonomous() && isEnabled() && !abort) {
                abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);
                
                if (abort)
                    break;
                
                if (pickupMachine.crank(PickupState.OUT) && elevatorMachine.crank(ElevatorState.LOW))
                    break;
                
                ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
                this.resetMotors(true);
            }
            
            ringLight.set(ActuatorConfiguration.RING_LIGHT.OFF);
            
            while (isAutonomous() && isEnabled() && !abort) {
                abort = leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT);

                if (abort)
                    break;
                
                if (leftKinect.getRawButton(ButtonConfiguration.Kinect.DRIVE_ENABLED))
                    driveTrain.tankDrive(leftKinect.getRawAxis(2) * -0.8, rightKinect.getRawAxis(2) * -0.8);
                else
                    driveTrain.tankDrive(0D, 0D);

                if (leftKinect.getRawButton(ButtonConfiguration.Kinect.SHOOT) && elevatorMachine.crank(ElevatorState.HIGH)) {
                    shooterMachine.crank(ShooterState.SHOOTING);
                } else {
                    if (leftKinect.getRawButton(ButtonConfiguration.Kinect.PICKUP_IN)) {
                        if (elevatorMachine.crank(ElevatorState.MEDIUM))
                            pickupMachine.crank(PickupState.IN);
                    } else {
                        if (pickupMachine.crank(PickupState.OUT))
                            elevatorMachine.crank(ElevatorState.LOW);
                    }
                }

                if (leftKinect.getRawButton(ButtonConfiguration.Kinect.SUCK) && pickupMachine.test(PickupState.OUT)) {
                    pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                    hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                } else {
                    pickupMotor.set(0D);
                    hopperMotor.set(0D);
                }
                
                this.resetMotors();
            }
        }
        
        this.resetMotors();
        
        pickupMotor.set(0D);
        hopperMotor.set(0D);
        
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
        
        while (isOperatorControl() && isEnabled()) {
            pidElevator.setUpGains(new Gains(getDouble("Elevator Up P", 0.0085), getDouble("Elevator Up I", 0D), getDouble("Elevator Up D", 0.018)));
            pidElevator.setDownGains(new Gains(getDouble("Elevator Down P", 0.0029), getDouble("Elevator Down I", 0.000003), getDouble("Elevator Down P", 0.007)));
            
            if (driveController.getToggle(ButtonConfiguration.Driver.DISABLE_ELEVATOR))
                elevatorMotors.setDisabled(!elevatorMotors.getDisabled());
            
            if (!elevatorLimitSwitch.get())
                encoderElevator.reset();
            
            if (Math.abs(manipulatorController.getAxis(Axis.RIGHT_STICK_Y)) > 0)
                hopperMotor.set(manipulatorController.getAxis(Axis.RIGHT_STICK_Y));
            
            /* Controls the gear shift. */
            
            if (driveController.getButton(ButtonConfiguration.Driver.SHIFT))
                solenoidShifter.set(ActuatorConfiguration.SOLENOID_SHIFTER.HIGH_GEAR);
            
            /* Drive train controls. */

            if (driveController.getButton(ButtonConfiguration.Driver.TINY_FORWARD)) {
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
            
            /* Manually set whether or not we're at the fender. */
            
            if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_FENDER))
                firingProvider.setAtFender(true);
            else if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_KEY))
                firingProvider.setAtFender(false);
            
            /* Toggle the "default" height between "up high" and "down low". */
            
            if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.UP))
                upHigh = true;
            else if (manipulatorController.getButton(ButtonConfiguration.Manipulator.Elevator.DOWN))
                upHigh = false;
            
            /* Toggle the pickup state between "up" and "down". */
            
            if (driveController.getToggle(ButtonConfiguration.Driver.TOGGLE_PICKUP))
                pickupIn = !pickupIn;
            
            SmartDashboard.putBoolean("upHigh", upHigh);
            SmartDashboard.putBoolean("pickupIn", pickupIn);
            
            /*
             * If upHigh is true, then raise the elevator. If pickupIn is true,
             * then make sure the elevator is up high enough, and lift up the
             * pickup.
             * 
             * Else, check the "default" position. If it is up high, then lower
             * the pickup and raise the elevator simultaneously. If it is down
             * low, then make sure the pickup is down first, and then lower the
             * elevator.
             */
            
            if (driveController.getButton(ButtonConfiguration.Driver.CALIBRATE)) {
                if (pickupMachine.crank(PickupState.OUT)) {
                    elevatorMotors.setDisabled(false);
                    elevatorMotors.set(-0.4);
                }
            } else {
                if (upHigh) {
                    if (manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT) || elevatorMachine.crank(ElevatorState.HIGH)) {
                        SmartDashboard.putString("Ready to Shoot", "Yes");

                        /*
                         * Toggles the shooter angle.
                         */

                        if (manipulatorController.getToggle(ButtonConfiguration.Manipulator.TOGGLE_ANGLE)) {
                            if (solenoidShooter.get() == ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE) {
                                solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
                            } else {
                                solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
                            }
                        }

                        if (manipulatorController.getButton(ButtonConfiguration.Manipulator.SHOOT)) {
                            System.out.println("GET READY U GUIZE");
                            shooterMachine.crank(ShooterState.SHOOTING);
                        }
                    }
                } else {
                    if (pickupIn)
                        elevatorMachine.crank(ElevatorState.MEDIUM);
                    else if (pickupMachine.test(PickupState.OUT))
                        elevatorMachine.crank(ElevatorState.LOW);
                }

                if (pickupIn) {
                    if (elevatorMachine.test(ElevatorState.PICKUP_OKAY) || elevatorMotors.getDisabled())
                        pickupMachine.crank(PickupState.IN);
                } else {
                    if (pickupMachine.crank(PickupState.OUT)) {
                        /*
                        * If the pickup is down and the elevator is at rest,
                        * then allow the user to trigger the pickup mechanism.
                        */

                        if (elevatorMachine.test(ElevatorState.LOW)) {
                            /* Controls the pickup mechanism. */

                            if (manipulatorController.getButton(ButtonConfiguration.Driver.PICKUP)) {
                                pickupMotor.set(ActuatorConfiguration.PICKUP_POWER);
                                hopperMotor.set(ActuatorConfiguration.HOPPER_POWER);
                                elevatorMotors.set(ActuatorConfiguration.ELEVATOR_PICKUP_POWER);

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
        
            if (!manipulatorController.getButton(ButtonConfiguration.Driver.PICKUP) && settleState != 2) {
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
            
            speedProvider.reset();
            
            elevatorMotors.reload();
            shooterMotors.reload();
            hopperMotor.reload();
            pickupMotor.reload();
            
            ringLight.reload();
            
            solenoidShifter.reload();
            solenoidHopper.reload();
            
            /* Debug output. */
            
            SmartDashboard.putDouble("Shooter Speed", shooterMachine.getShooterSpeed());
            SmartDashboard.putBoolean("Using Targets?", firingProvider.usingTargets());
            SmartDashboard.putBoolean("At the Fender?", firingProvider.isAtFender());
            
            SmartDashboard.putDouble("gyroHeading", gyroHeading.getAngle());
            
            SmartDashboard.putInt("ups", ((RemoteCameraTCP) cameraInterface).getUPS());
            
            SmartDashboard.putDouble("encoderElevator", encoderElevator.get());
            SmartDashboard.putDouble("Current Elevator Setpoint", pidElevator.getSetpoint());
            SmartDashboard.putDouble("Elevator Output", pidElevator.get());
        }

        pidElevator.disable();
        
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
        
        while (!isEnabled()) {
            if (!elevatorLimitSwitch.get()) {
                System.out.println("CALIBRATED ELEVATOR");
                SmartDashboard.getBoolean("Elevator Calibrated", true);
                encoderElevator.reset();
            }
        }
    }
}