package com._604robotics.robot2012.rotation;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.ConvertingPIDController;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Vector;

/**
 * Implements a slow-er-ish, but more robust-ish, RotationProvider.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class SlowbroRotationProvider implements RotationProvider {
    private final ConvertingPIDController controller;
    private final CameraInterface cameraInterface;
    private final Encoder encoderTurret;
    
    private double defaultPosition = 0D;
    
    private final Timer confidenceTimer = new Timer();
    private final Timer lastTimer = new Timer();
    private final Timer steadyTimer = new Timer();
    
    /**
     * Initializes a new SlowbroRotationProvider.
     * 
     * @param   controller      The PIDController to control.
     * @param   cameraInterface The CameraInterface to read data from.
     * @param   encoderTurret   The turret encoder to read data from. 
     */
    public SlowbroRotationProvider (ConvertingPIDController controller, CameraInterface cameraInterface, Encoder encoderTurret) {
        this.controller = controller;
        this.cameraInterface = cameraInterface;
        this.encoderTurret = encoderTurret;
        
        this.confidenceTimer.start();
        this.lastTimer.start();
        this.steadyTimer.start();
    }
    
    public void setDefaultPosition(double defaultPosition) {
        this.defaultPosition = defaultPosition;
    }
    
    public boolean update () {
        Target[] targetsIn = cameraInterface.getTargets();
        Vector targets = new Vector();
        Target target = null;
        
        /* for (int i = 0; i < targets.length; i++) {
            if (target == null || Math.abs(targets[i].x) < Math.abs(target.x))
                target = targets[i];
        } */
        
        for (int i = 0; i < targetsIn.length; i++) {
            if (targetsIn[i].x_uncertainty < 9000)
                targets.addElement(targetsIn[i]);
        }
        
        if (targets.size() > 0) {
            target = (Target) targets.elementAt(0);
            System.out.println("x: " + target.x + ", y: " + target.y + ", z: " + target.z);
            System.out.println("x_uncertainty: " + target.x_uncertainty);
        }
        
        System.out.println("--------------------");
        
        if (Math.abs(this.encoderTurret.get() - this.controller.getRealSetpoint()) < 10) {
            if (this.confidenceTimer.get() >= SmartDashboard.getDouble("Confidence Threshold", 0.7)) {
                this.steadyTimer.start();
                if (target != null) {
                    this.controller.setSetpoint((Math.toDegrees(MathUtils.atan2(target.z, target.x)) * -1.0 + 90) * 0.8 + encoderTurret.getDistance());
                    this.lastTimer.reset();
                    if (Math.abs(target.x) < 1) {
                        this.controller.reset();
                        if (this.steadyTimer.get() < SmartDashboard.getDouble("Steady Threshold", 0.5))
                            this.controller.enable();
                        else
                            return true;
                    } else {
                        if (this.steadyTimer.get() > SmartDashboard.getDouble("Unsteady Threshold", 1D))
                            this.steadyTimer.reset();
                        else
                            return true;
                    }
                } else if (this.lastTimer.get() >= SmartDashboard.getDouble("Target Timeout", 1.5)) {
                    this.controller.setSetpoint(this.defaultPosition);
                }
            } else {
                this.steadyTimer.stop();
            }
        } else {
            this.confidenceTimer.reset();
        }
        
        this.controller.enable();
        
        return false;
    }
}