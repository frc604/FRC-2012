package com._604robotics.robot2012.rotation;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.ConvertingPIDController;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Date;

/**
 * A naive implementation of a RotationProvider, taking a CameraInteface and a
 * Gyro360 as inputs to process.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class SlowbroRotationProvider implements RotationProvider {
    // TODO: Test, update docs.
    
    private final ConvertingPIDController controller;
    private final CameraInterface cameraInterface;
    //private final Gyro360 gyroHeading;
    private final Encoder encoderTurret;
    
    private double defaultPosition = 0D;
    
    private long sinceWhen = 0;
    private long lastHad = 0;
    private long steadyStamp = 0;
    private long unsteadyStamp = 0;
    
    /**
     * Initializes a new NaiveRotationProvider, giving it control over the
     * specified PIDController.
     * 
     * @param   controller      The PIDController to control.
     * @param   cameraInterface The CameraInterface to read data from.
     * @param   gyroHeading     The (heading) Gyro360 to read data from.
     */
    public SlowbroRotationProvider (ConvertingPIDController controller, CameraInterface cameraInterface/* , Gyro360 gyroHeading*/, Encoder encoderTurret) {
        this.controller = controller;
        this.cameraInterface = cameraInterface;
        //this.gyroHeading = gyroHeading;
        this.encoderTurret = encoderTurret;
        
        this.sinceWhen = new Date().getTime();
        this.lastHad = new Date().getTime();
        this.steadyStamp = new Date().getTime();
        this.unsteadyStamp = new Date().getTime();
    }
    
    public void setDefaultPosition(double defaultPosition) {
        this.defaultPosition = defaultPosition;
    }
    
    public boolean update () {
        Target[] targets = cameraInterface.getTargets();
        Target target = null;
        
        //System.out.println("--------------------------------");
        
        for (int i = 0; i < targets.length; i++) {
            if (target == null || Math.abs(targets[i].x) < Math.abs(target.x))
                target = targets[i];
        }

        if (target != null) {
            //System.out.println("x: " + target.x + ", y: " + target.y + ", z: " + target.z + ", angle: " + target.angle);
            //System.out.println("x_uncertainty: " + target.x_uncertainty + ", y_uncertainty: " + target.y_uncertainty + ", z_uncertainty: " + target.z_uncertainty + ", angle_uncertainty: " + target.angle_uncertainty);
        }
        
        //System.out.println("--------------------------------");

        double p = Math.abs(this.encoderTurret.get() - this.controller.getRealSetpoint());
        
        //System.out.println("error: " + this.controller.getError());
        //System.out.println("p: " + p);
        
        if (p < 10) {//this.controller.onTarget()) {
            //this.controller.setSetpoint(Math.toDegrees(MathUtils.asin(target.x / target.z)) - gyroHeading.getAngle());
            long diff = new Date().getTime() - this.sinceWhen;
            //System.out.println("diff: " + diff);
            if (diff >= SmartDashboard.getInt("Confidence Threshold", 700)) {
                long lastDiff = new Date().getTime() - this.lastHad;
                //System.out.println("lastDiff: " + lastDiff);
                if (target != null) {
                    this.controller.setSetpoint((Math.toDegrees(MathUtils.atan2(target.z, target.x)) * -1.0 + 90) * 0.8 + encoderTurret.getDistance());
                    this.lastHad = new Date().getTime();
                    if (Math.abs(target.x) < 1) {
                        this.controller.reset();
                        if (new Date().getTime() - this.steadyStamp < SmartDashboard.getInt("Steady Threshold", 500))
                            this.controller.enable();
                        else
                            return true;
                    } else {
                        if (new Date().getTime() - this.steadyStamp > SmartDashboard.getInt("Unsteady Threshold", 1000)) {
                            this.steadyStamp = new Date().getTime();
                            System.out.println("UNSTEADY");
                        } else {
                            return true;
                        }
                    }
                    //System.out.println(Math.abs(target.x));
                } else if (lastDiff >= SmartDashboard.getInt("Target Timeout", 1500)) {
                    //this.controller.setSetpoint(this.defaultPosition);
                    System.out.println("LOST TARGETS");
                }
            }
        } else {
            this.sinceWhen = new Date().getTime();
        }
        
        this.controller.enable();
        
        return false;
    }
}