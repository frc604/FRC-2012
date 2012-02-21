package com._604robotics.robot2012.autonomous;

import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 * This class implements the default PIDOutput class provided in the WPILib API.
 * The class determines motor power to the robot drive so that the robot will 
 * drive backwards, depending on the encoder values.
 * 
 * @author  Aaron Wang <aaronw94@gmail.com>
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class PIDDriveEncoderOutput implements PIDOutput {
    private final RobotDrive driveTrain;
    private final boolean inversion;

    /**
     * Initializes a new PIDDriveEncoderOutput.
     * 
     * @param   driveTrain  The RobotDrive object to control.
     * @param   inversion   Should the output be inverted?
     */
    public PIDDriveEncoderOutput(RobotDrive driveTrain, boolean inversion) {
        this.driveTrain = driveTrain;
        this.inversion = inversion;
    }

    /**
     * Initializes a new PIDDriveEncoderOutput.
     * 
     * @param   driveTrain  The RobotDrive object to control.
     */
    public PIDDriveEncoderOutput(RobotDrive driveTrain) {
        this(driveTrain, false);
    }
    
    /**
     * Robot will drive with the configured power, and swerve determined by the
     * encoder readings.
     * 
     * @param   output  The output of the PID controller.
     */
    public void pidWrite(double output) {
        driveTrain.arcadeDrive((inversion) ? AutonomousConfiguration.BACKWARD_DRIVE_POWER : AutonomousConfiguration.FORWARD_DRIVE_POWER, output);
    }
}