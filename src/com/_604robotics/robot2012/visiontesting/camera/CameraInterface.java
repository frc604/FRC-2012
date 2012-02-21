package com._604robotics.robot2012.visiontesting.camera;

import com._604robotics.robot2012.Aiming.Point3d;

public interface CameraInterface {
    public abstract void begin ();
    public abstract void end ();
    
    public abstract Point3d[] getTargets ();
}