/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.vision.Target;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AutoAimer {

    public static AutoAimer autoAimer = new AutoAimer();
    
    private boolean wasAiming = false;
    private Timer aimTimer = new Timer();

    public void aim(Target target) {

        if (!wasAiming) {
            Robot.gyroHeading.reset();
        

            Robot.pidAutoAim.setSetpoint(Math.toDegrees(MathUtils.atan2(target.getX(), target.getZ())));
            Robot.pidAutoAim.enable();

            aimTimer.start();
        }

        SmartDashboard.putDouble("gyro angle", Robot.gyroHeading.getAngle());
        Robot.pidOutputDrive.setForwardPower(Drive.leftPower);


        wasAiming = true;
        
        if(aimTimer.get() > 1)
            dontAim();

    }

    public void dontAim() {
        if (wasAiming) {
            Robot.pidAutoAim.reset();
            aimTimer.stop();
            aimTimer.reset();

            wasAiming = false;
        }
    }
}
