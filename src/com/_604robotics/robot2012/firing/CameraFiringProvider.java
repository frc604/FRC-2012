package com._604robotics.robot2012.firing;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.camera.CameraInterface;
import com._604robotics.robot2012.configuration.FiringConfiguration;
import com._604robotics.robot2012.control.workers.AutoAimer;
import com._604robotics.robot2012.vision.Target;

public class CameraFiringProvider implements FiringProvider {
    private final CameraInterface camera;
    private final FiringProvider fallback;
    
    private boolean enabled = true;
    private boolean physicsEnabled = true;
    
    private boolean atFender = false;
    private boolean manuallySet = false;
    private boolean usedTargets = false;
    
    public CameraFiringProvider (CameraInterface camera, FiringProvider fallback) {
        this.camera = camera;
        this.fallback = fallback;
    }
    
    public void setEnabled (boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setPhysicsEnabled (boolean physicsEnabled) {
        this.physicsEnabled = physicsEnabled;
    }
    
    public double getSpeed () {
        
        if (!this.manuallySet)
            this.fallback.setAtFender(this.atFender);
        
        if (!this.enabled) {
            this.usedTargets = false;
            return this.fallback.getSpeed();
        }
        
        Target target = Robot.cameraInterface.getSingleTarget();
        
        if (target == null) {// || this.atFender) {
            this.usedTargets = false;
            return this.fallback.getSpeed();
        } else {
            
            if (!this.manuallySet) {
                this.atFender = target.z <= FiringConfiguration.FENDER_DISTANCE_THRESHOLD;
                this.fallback.setAtFender(this.atFender);
            }
            
            
            if (this.physicsEnabled) {
                double z = target.z;
                if(EncoderSpeedsForDist.getUseDemoHeight()) {
                    z = target.getHoopPosition().z;
                    System.out.println("Z = " + z);
                }
                //System.out.println("using speed: " + EncoderSpeedsForDist.getSpeedForDist(z));
                
                this.usedTargets = true;
                //return Physics.getSubparFiringVelocity(target.z, FiringConfiguration.TOP_HOOP_HEIGHT - FiringConfiguration.SHOOTER_HEIGHT, FiringConfiguration.SHOOTER_SLOPE);
                return EncoderSpeedsForDist.getSpeedForDist(z);
            } else {

                this.usedTargets = false;
                return this.fallback.getSpeed();
            }
        }
    }

    public boolean isAtFender() {
        return this.atFender;
    }
    
    public void setAtFender (boolean atFender) {
        this.atFender = atFender;
        this.fallback.setAtFender(atFender);
        
        //this.manuallySet = true;
    }
    
    public boolean usingTargets () {
        return this.usedTargets;
    }
}
