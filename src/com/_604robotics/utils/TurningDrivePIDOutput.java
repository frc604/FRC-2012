package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class TurningDrivePIDOutput implements PIDOutput {
    private final RobotDrive drive;
    private double forwardPower = 0D;
    
    public TurningDrivePIDOutput (RobotDrive drive) {
        this.drive = drive;
    }
    
    public void pidWrite (double output) {
        this.drive.arcadeDrive(0D, output);
    }
    
    public void setForwardPower (double forwardPower) {
        this.forwardPower = forwardPower;
    }
}
