package com._604robotics.robot2012.visiontesting.camera;

import com._604robotics.robot2012.Aiming.Aiming;
import com._604robotics.robot2012.Aiming.Point3d;
import com._604robotics.robot2012.visiontesting.configuration.CameraConfiguration;
import com._604robotics.robot2012.visiontesting.configuration.PortConfiguration;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.camera.AxisCameraException;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.NIVisionException;
import edu.wpi.first.wpilibj.image.ParticleAnalysisReport;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class OnboardCamera implements CameraInterface {
    private final Aiming aiming = new Aiming();
    private final OnboardCameraProcessor processor = new OnboardCameraProcessor(PortConfiguration.CAMERA_IP);
    
    private Thread thread;
    
    public void begin() {
        processor.enabled = true;
        
        thread = new Thread(processor);
        thread.start();
    }

    public void end() {
        processor.enabled = false;
    }

    public Point3d[] getTargets () {
        if (processor.inRange && processor.target != null) {
            Point3d point = aiming.getRelXYZOfTarget(processor.target.boundingRectLeft - CameraConfiguration.IMAGE_RESOLUTION.width / 2, processor.target.boundingRectTop - CameraConfiguration.IMAGE_RESOLUTION.height / 2, processor.target.boundingRectWidth, processor.target.boundingRectHeight);
            point.y *= -1;
            
            return new Point3d[] { point };
        } else {
            return null;
        }
    }
}

class OnboardCameraProcessor implements Runnable {
    public boolean enabled = false;
    public boolean log = false;
    
    public boolean inRange = false;
    
    public ParticleAnalysisReport target;

    public int frames = 0;
    public int fps = 0;
    
    private final AxisCamera camera;
    
    public OnboardCameraProcessor () {
        this.camera = AxisCamera.getInstance();
    }
    
    public OnboardCameraProcessor (String cameraIP) {
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