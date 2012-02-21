package com._604robotics.robot2012.camera;

import com._604robotics.robot2012.vision.Target;

/**
 * Represents a method for obtaining processed vision data from the camera.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface CameraInterface {
    /**
     * Launches the CameraInterface.
     */
    public abstract void begin ();
    
    /**
     * Disables the CameraInterface.
     */
    public abstract void end ();
    
    /**
     * Returns the most recently-obtained array of Target that
     * represents the visible targets.
     * 
     * @return  An array of Target that represents the visible targets.
     */
    public abstract Target[] getTargets ();
}