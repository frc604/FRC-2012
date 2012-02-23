package com._604robotics.robot2012.rotation;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.Gyro360;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.PIDController;

public class NaiveRotationProvider implements RotationProvider {
    private final PIDController controller;
    private final CameraInterface cameraInterface;
    private final Gyro360 gyroHeading;
    
    public NaiveRotationProvider (PIDController controller, CameraInterface cameraInterface, Gyro360 gyroHeading) {
        this.controller = controller;
        this.cameraInterface = cameraInterface;
        this.gyroHeading = gyroHeading;
    }

    public void update () {
        Target[] targets = cameraInterface.getTargets();
        
        for (int i = 0; i < targets.length; i++) {
            System.out.println("x: " + targets[0].x + ", y: " + targets[0].y + ", z: " + targets[0].z + ", angle: " + targets[0].angle);
            System.out.println("x_uncertainty: " + targets[0].x_uncertainty + ", y_uncertainty: " + targets[0].y_uncertainty + ", z_uncertainty: " + targets[0].z_uncertainty + ", angle_uncertainty: " + targets[0].angle_uncertainty);
        }

        System.out.println("--------------------------------");

        if (targets.length != 0)
            this.controller.setSetpoint(Math.toDegrees(MathUtils.asin(targets[0].x / targets[0].z)) - gyroHeading.getAngle());
        else
            this.controller.setSetpoint(0D);
   }
}