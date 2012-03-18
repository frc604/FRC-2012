package com._604robotics.robot2012.autonomous;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * Driving shim for the gyro-based PID-turning controller thing.
 * 
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class PIDDriveGyro implements PIDOutput {
    private final RobotDrive driveTrain;
    
    /**
     * Initializes a new PIDDriveGyro, based on the given RobotDrive.
     * 
     * @param   driveTrain  The RobotDrive object to control.
     */
    public PIDDriveGyro(RobotDrive driveTrain) {
        this.driveTrain = driveTrain;
    }
    
    /**
     * Writes the output from the PIDController to the RobotDrive, in the form
     * of a turn value.
     * 
     * @param   output  The output of the PIDController.
     */
    public void pidWrite(double output) {
        this.driveTrain.arcadeDrive(0D, output);
    }
}