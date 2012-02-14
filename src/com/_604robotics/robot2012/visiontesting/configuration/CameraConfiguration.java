package com._604robotics.robot2012.visiontesting.configuration;

import edu.wpi.first.wpilibj.camera.AxisCamera;

public interface CameraConfiguration {
    public static final AxisCamera.ResolutionT IMAGE_RESOLUTION = AxisCamera.ResolutionT.k640x480;
    
    public static final int THRESHOLD_MIN_HUE = 64;
    public static final int THRESHOLD_MAX_HUE = 183;
    public static final int THRESHOLD_MIN_SATURATION = 114;
    public static final int THRESHOLD_MAX_SATURATION = 255;
    public static final int THRESHOLD_MIN_LUMINANCE = 82;
    public static final int THRESHOLD_MAX_LUMINANCE = 255;
    
    public static final int HALF_IMAGE_WIDTH = 320;
    public static final double HALF_HORIZONTAL_FOV = 0.4102;
    public static final double HORIZONTAL_SCALE_FACTOR = HALF_IMAGE_WIDTH / Math.tan(HALF_HORIZONTAL_FOV); // TODO: Test this.
    
    public static final int HALF_IMAGE_HEIGHT = 240;
    public static final int VERTICAL_SCALE_FACTOR = 1; // TODO: Actually find this.
}
    