/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;

/**
 *
 * @author aaron
 * This class implements the default PIDOutput class provided in the WPILib API. 
 * The class determines motor power to the robot drive so that the robot will 
 * drive straight, backwards, depending on the encoder values.
 */
public class PIDEncoderOutputBackwards implements PIDOutput{

    private RobotDrive driveTrain;
    /*Constructor*/
    public PIDEncoderOutputBackwards(RobotDrive driveTrain){
        this.driveTrain = driveTrain;
    }
    
    public void pidWrite(double output) {
        driveTrain.arcadeDrive(-0.5,output);    //Robot will drive backward with half power, and swerve determined by the encoder readings.
    }
}
