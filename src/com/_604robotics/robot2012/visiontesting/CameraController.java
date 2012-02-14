package com._604robotics.robot2012.visiontesting;

import com._604robotics.robot2012.visiontesting.configuration.CameraConfiguration;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraController implements Runnable {
    public boolean enabled = false;
    public boolean log = false;
    
    public boolean inRange = false;
    
    public ParticleAnalysisReport target;
    
    public double offsetAngleX = 0F;
    public double offsetAngleY = 0F;
    
    public int frames = 0;
    public int fps = 0;
    
    private final AxisCamera camera;
    
    public CameraController () {
        this.camera = AxisCamera.getInstance();
    }
    
    public CameraController (String cameraIP) {
        this.camera = AxisCamera.getInstance(cameraIP);
    }
    
    public void run () {
        this.inRange = false;
        this.camera.writeResolution(CameraConfiguration.IMAGE_RESOLUTION);
        
        ColorImage image;
        
        BinaryImage binImage;
        BinaryImage convexHull;
        BinaryImage cherryPicked;
        
        int fps = 0;
        int errorCount = 0;
        
        Timer second = new Timer();
        second.start();
        
        while (this.enabled) {
            if (this.camera.freshImage()) {
                try {
                    image = this.camera.getImage();

                    binImage = image.thresholdHSL(CameraConfiguration.THRESHOLD_MIN_HUE, CameraConfiguration.THRESHOLD_MAX_HUE, CameraConfiguration.THRESHOLD_MIN_SATURATION, CameraConfiguration.THRESHOLD_MAX_SATURATION, CameraConfiguration.THRESHOLD_MIN_LUMINANCE, CameraConfiguration.THRESHOLD_MAX_LUMINANCE);
                    convexHull = binImage.convexHull(true);
                    cherryPicked = convexHull.removeSmallObjects(true, 3);
                    
                    if (cherryPicked.getNumberParticles() == 0) {
                        this.inRange = false;
                    } else {
                        this.target = cherryPicked.getParticleAnalysisReport(0);
                        this.offsetAngleX = Math.toDegrees(MathUtils.atan((this.target.center_mass_x - CameraConfiguration.HALF_IMAGE_WIDTH) / CameraConfiguration.HORIZONTAL_SCALE_FACTOR));
                        this.offsetAngleY = 0; // TODO: Implement!
                        //this.offsetAngleY = Math.toDegrees(MathUtils.atan(this.target.center_mass_y - CameraConfiguration.HALF_IMAGE_HEIGHT / CameraConfiguration.HORIZONTAL_SCALE_FACTOR));
                        
                        this.inRange = true;
                    }
                    
                    image.free();
                    binImage.free();
                    convexHull.free();
                    cherryPicked.free();
                    
                    this.frames++;
                    
                    if (second.get() >= 1) {
                        this.fps = fps;
                        fps = 0;

                        second.reset();
                    }
                    
                    fps++;
                } catch (AxisCameraException ex) {
                    SmartDashboard.putInt("TOTAL VISION ERRORS", errorCount);
                    SmartDashboard.putString("LAST VISION ERROR", ex.getMessage());
                    
                    System.err.println("VISION ERROR (AxisCameraException) #" + errorCount + ": " + ex.getMessage());
                } catch (NIVisionException ex) {
                    SmartDashboard.putInt("TOTAL VISION ERRORS", errorCount);
                    SmartDashboard.putString("LAST VISION ERROR", ex.getMessage());
                    
                    System.err.println("VISION ERROR (NIVisionException) #" + errorCount + ": " + ex.getMessage());
                }
            }
        }
    }
}
