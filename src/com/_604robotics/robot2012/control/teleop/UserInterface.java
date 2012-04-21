package com._604robotics.robot2012.control.teleop;

import com._604robotics.robot2012.TheRobot;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import com._604robotics.utils.SmarterDashboard;
import com._604robotics.utils.UpDownPIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class UserInterface {
    public static final TheRobot theRobot = TheRobot.theRobot;
    
    // TODO: Scrape out this junk.
    public static final AwesomeSpeedController ctrl = 
            (theRobot.speedProvider instanceof AwesomeSpeedController)
                ? ((AwesomeSpeedController) theRobot.speedProvider)
                : null;
    
    public static boolean upHigh = false;
    public static boolean pickupIn = true;
    
    public static void readControllerInputs () {
        /*
         * Manually set whether or not we're at the fender.
         */

        if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_FENDER)) {
            theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            if (theRobot.elevatorMachine.test(ElevatorMachine.ElevatorState.HIGH)) {
                theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            }
            theRobot.firingProvider.setAtFender(true);
        } else if (theRobot.manipulatorController.getToggle(ButtonConfiguration.Manipulator.AT_KEY)) {
            theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
            if (theRobot.elevatorMachine.test(ElevatorMachine.ElevatorState.HIGH)) {
                theRobot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE);
            }
            theRobot.firingProvider.setAtFender(false);
        }
    }
    
    public static void readConfigFromSmartDashboard () {
        /*
         * Update speed controller inputs.
         */
        
        if (ctrl != null) {
            ctrl.setPIDDP(SmarterDashboard.getDouble("Shooter P", ctrl.getP()), SmarterDashboard.getDouble("Shooter I", ctrl.getI()), SmarterDashboard.getDouble("Shooter D", ctrl.getD()), SmarterDashboard.getDouble("Shooter DP", ctrl.getDP()));
            ctrl.fac = SmarterDashboard.getDouble("Shooter fac", ctrl.fac);
            ctrl.maxSpeed = SmarterDashboard.getDouble("Shooter maxSpeed", ctrl.maxSpeed);
        }
        
        /*
         * Update elevator PID gains.
         */
        
        theRobot.pidElevator.setUpGains(new UpDownPIDController.Gains(SmarterDashboard.getDouble("Elevator Up P", 0.0085), SmarterDashboard.getDouble("Elevator Up I", 0D), SmarterDashboard.getDouble("Elevator Up D", 0.018)));
        theRobot.pidElevator.setDownGains(new UpDownPIDController.Gains(SmarterDashboard.getDouble("Elevator Down P", 0.0029), SmarterDashboard.getDouble("Elevator Down I", 0.000003), SmarterDashboard.getDouble("Elevator Down P", 0.007)));
    }
    
    public static void writeDebugInformation () {
        /*
         * Output state toggle values.
         */
        
        SmartDashboard.putBoolean("upHigh", upHigh);
        SmartDashboard.putBoolean("pickupIn", pickupIn);
        
        /*
         * Output shooter encoder information.
         */
        
        SmartDashboard.putDouble("encoderShooter", theRobot.encoderShooter.get());
        SmartDashboard.putDouble("Current Encoder Rate", theRobot.encoderShooter.getRate());
        SmartDashboard.putDouble("Current Shooter Output", theRobot.shooterMotors.get());
        SmartDashboard.putDouble("Shooter Speed", theRobot.shooterMachine.getShooterSpeed());
        
        /*
         * Output elevator encoder information.
         */

        SmartDashboard.putDouble("encoderElevator", theRobot.encoderElevator.get());
        SmartDashboard.putDouble("Current Elevator Setpoint", theRobot.pidElevator.getSetpoint());
        SmartDashboard.putDouble("Elevator Output", theRobot.pidElevator.get());
        
        /*
         * Output firing information.
         */

        SmartDashboard.putBoolean("Using Targets?", theRobot.firingProvider.usingTargets());
        SmartDashboard.putBoolean("At the Fender?", theRobot.firingProvider.isAtFender());
        
        SmartDashboard.putInt("ups", ((RemoteCameraTCP) theRobot.cameraInterface).getUPS());

        /*
         * Output gyro information.
         */
        
        SmartDashboard.putDouble("gyroHeading", theRobot.gyroHeading.getAngle());
    }
    
    public static void updateDriverAssist () {
        // TODO: Implement!
    }
    
    public static void resetToggles () {
        upHigh = false;
        pickupIn = true;
    }
}
