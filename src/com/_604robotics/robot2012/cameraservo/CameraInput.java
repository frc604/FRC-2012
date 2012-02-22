package com._604robotics.robot2012.cameraservo;

import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.camera.RemoteCameraTCP;
import com._604robotics.robot2012.vision.Target;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.PIDSource;

public class CameraInput implements PIDSource {
    private final CameraInterface cameraInterface;
    
    public CameraInput (CameraInterface cameraInterface) {
        this.cameraInterface = cameraInterface;
    }
    
    public double pidGet () {
        Target[] targets = cameraInterface.getTargets();
        
        System.out.println(" - UPS: " + ((RemoteCameraTCP) cameraInterface).getUPS() + " - ");
        
        if (targets.length == 0)
            return 0D;
        
        for (int i = 0; i < targets.length; i++) {
            System.out.println("x: " + targets[0].x + ", y: " + targets[0].y + ", z: " + targets[0].z + ", angle: " + targets[0].angle);
            //System.out.println("x_uncertainty: " + targets[0].x_uncertainty + ", y_uncertainty: " + targets[0].y_uncertainty + ", z_uncertainty: " + targets[0].z_uncertainty + ", angle_uncertainty: " + targets[0].angle_uncertainty);
        }
        
        double ret = targets[0].x / targets[0].z;
        
        System.out.println(ret);
        System.out.println("--------------------------------");
        
        return ret;
    }
}