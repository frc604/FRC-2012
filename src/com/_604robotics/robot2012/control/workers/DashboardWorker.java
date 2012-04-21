package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class DashboardWorker implements Worker {
    public void work () {
        /*
         * Output state toggle values.
         */
        SmartDashboard.putBoolean("upHigh", Elevator.high);
        SmartDashboard.putBoolean("pickupIn", Pickup.up);
        
        /*
         * Output shooter encoder information.
         */
        SmartDashboard.putDouble("encoderShooter", Robot.encoderShooter.get());
        SmartDashboard.putDouble("Current Encoder Rate", Robot.encoderShooter.getRate());
        SmartDashboard.putDouble("Current Shooter Output", Robot.shooterMotors.get());
        SmartDashboard.putDouble("Shooter Speed", Robot.shooterMachine.getShooterSpeed());
        
        /*
         * Output elevator encoder information.
         */
        SmartDashboard.putDouble("encoderElevator", Robot.encoderElevator.get());
        SmartDashboard.putDouble("Current Elevator Setpoint", Robot.pidElevator.getSetpoint());
        SmartDashboard.putDouble("Elevator Output", Robot.pidElevator.get());
        
        /*
         * Output firing information.
         */
        SmartDashboard.putBoolean("Using Targets?", Robot.firingProvider.usingTargets());
        SmartDashboard.putBoolean("At the Fender?", Robot.firingProvider.isAtFender());
        
        SmartDashboard.putInt("ups", ((RemoteCameraTCP) Robot.cameraInterface).getUPS());

        /*
         * Output gyro information.
         */
        SmartDashboard.putDouble("gyroHeading", Robot.gyroHeading.getAngle());
    }
}
