package com._604robotics.robot2012.rotation;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.vision.Target;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;

/**
 * A slightly smarter implementation of a rotation provider, which tries to 
 * account for network delay, etc.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class SlightlySmarterRotationProvider implements RotationProvider {
    private final PIDController controller;
    private final CameraInterface cameraInterface;
    private final Encoder encoderTurret;
    
    private double defaultPosition = 0D;
    
    /**
     * Initializes a new SlightlySmarterRotationProvider.
     * 
     * @param   controller      The PIDController to control.
     * @param   cameraInterface The CameraInterface to read data from.
     * @param   encoderTurret   The turret encoder to read data from.
     */
    public SlightlySmarterRotationProvider (PIDController controller, CameraInterface cameraInterface/* , Gyro360 gyroHeading*/, Encoder encoderTurret) {
        this.controller = controller;
        this.cameraInterface = cameraInterface;
        this.encoderTurret = encoderTurret;
    }
    
    public void setDefaultPosition(double defaultPosition) {
        this.defaultPosition = defaultPosition;
    }
    
    public boolean update () {
        Target[] targets = cameraInterface.getTargets();
        
        for (int i = 0; i < targets.length; i++) {
            System.out.println("x: " + targets[0].x + ", y: " + targets[0].y + ", z: " + targets[0].z + ", angle: " + targets[0].angle);
            System.out.println("x_uncertainty: " + targets[0].x_uncertainty + ", y_uncertainty: " + targets[0].y_uncertainty + ", z_uncertainty: " + targets[0].z_uncertainty + ", angle_uncertainty: " + targets[0].angle_uncertainty);
        }

        System.out.println("--------------------------------");

        if (targets.length != 0)
            this.controller.setSetpoint((Math.toDegrees(MathUtils.atan2(targets[0].z, targets[0].x)) * -1) + encoderTurret.getDistance() - (encoderTurret.getRate() / 1000D * this.cameraInterface.getRecordedTime()));
        else if (this.controller.onTarget())
            this.controller.setSetpoint(this.defaultPosition);
        
        return false;
    }
}