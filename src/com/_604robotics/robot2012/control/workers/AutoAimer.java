/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.firing.EncoderSpeedsForDist;
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
        System.out.println("Aiming!!!");

        if (!wasAiming) {
            Robot.gyroHeading.reset();
        
            double x = target.getX();
            double z = target.getZ();
            
            if(EncoderSpeedsForDist.getUseDemoHeight()) {
                x = target.getHoopPosition().getX();
                z = target.getHoopPosition().getZ();
            }
            
            Robot.pidAutoAim.setSetpoint(.7 * Math.toDegrees(MathUtils.atan2(x, z)));
            Robot.pidAutoAim.enable();

            aimTimer.start();
        }

        SmartDashboard.putDouble("gyro angle", Robot.gyroHeading.getAngle());
        Robot.pidOutputDrive.setForwardPower(Drive.leftPower);


        wasAiming = true;
        
        if(aimTimer.get() > 1.2)
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
    
    public static boolean isOnTarget() {
        return Math.abs(Robot.pidAutoAim.getSetpoint() - Robot.gyroHeading.getAngle()) < 5;
    }
}
