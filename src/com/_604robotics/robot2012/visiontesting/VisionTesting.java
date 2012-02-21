package com._604robotics.robot2012.visiontesting;

import com._604robotics.robot2012.Aiming.Aiming;
import com._604robotics.robot2012.Aiming.Point3d;
import com._604robotics.robot2012.visiontesting.camera.CameraInterface;
import com._604robotics.robot2012.visiontesting.camera.RemoteCameraTCP;
import com._604robotics.robot2012.visiontesting.configuration.ButtonConfiguration;
import com._604robotics.robot2012.visiontesting.configuration.CameraConfiguration;
import com._604robotics.robot2012.visiontesting.configuration.PortConfiguration;
import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionTesting extends SimpleRobot {
    public final Joystick controller = new Joystick(PortConfiguration.XBOX_CONTROLLER_PORT);
    public final Victor turret = new Victor(PortConfiguration.TURRET_MOTOR_PORT);
    
    public final AnalogChannel potentiometer = new AnalogChannel(PortConfiguration.POTENTIOMETER_PORT);
    public final Gyro gyro = new Gyro(PortConfiguration.GYRO_PORT);
    
    public final Relay light = new Relay(PortConfiguration.LIGHT_RELAY_PORT, Relay.Direction.kForward);
   
    public final PotentiometerMonitor potentiometerMonitor = new PotentiometerMonitor(potentiometer);
    public final TurretOutput turretOutput = new TurretOutput(turret);
    public final PIDController turretController = new PIDController(0.009, 0, 0.0011, potentiometerMonitor, turretOutput);
    
    public final CameraInterface cameraInterface = new RemoteCameraTCP();
    
    public final Aiming aiming = new Aiming();
    
    public VisionTesting () {
        this.getWatchdog().setEnabled(false);
    }
    
    public void autonomous () {
        light.set(Relay.Value.kOn);
        
        System.out.println("WAITING");
        
        Timer timer = new Timer();
        timer.start();
        
        while(timer.get() < 3);
        timer.stop();
        
        System.out.println("READY");
        System.out.println("CAPTURING");
        
        AxisCamera camera = AxisCamera.getInstance();
        
        while (!camera.freshImage());
        
        try {
            ColorImage img = camera.getImage();
            img.write("/img.png");
            System.out.println("CAPTURED");
            
            BinaryImage binImage = img.thresholdHSL(CameraConfiguration.THRESHOLD_MIN_HUE, CameraConfiguration.THRESHOLD_MAX_HUE, CameraConfiguration.THRESHOLD_MIN_SATURATION, CameraConfiguration.THRESHOLD_MAX_SATURATION, CameraConfiguration.THRESHOLD_MIN_LUMINANCE, CameraConfiguration.THRESHOLD_MAX_LUMINANCE);
            System.out.println(Integer.toString(binImage.getNumberParticles()));
            binImage.write("/bin.png");
            System.out.println("BINARY");
            
            BinaryImage convexHull = binImage.convexHull(true);
            convexHull.write("/convex.png");
            System.out.println("CONVEX");
            
            BinaryImage cherryPicked = convexHull.removeSmallObjects(true, 3);
            cherryPicked.write("/cherry.png");
            System.out.println("CHERRY");
            
            System.out.println(Double.toString(cherryPicked.getParticleAnalysisReport(0).center_mass_x));
            
            img.free();
            binImage.free();
            convexHull.free();
            cherryPicked.free();
            
            System.out.println("FREED");
       } catch (Exception ex) {
            System.err.println("ERROR!");
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        light.set(Relay.Value.kOff);
        System.out.println("DONE");
    }
    
    public void operatorControl () {
        Timer runTimer = new Timer();
        runTimer.start();
        
        boolean lightOn = false;
        Timer flickTimer = new Timer();
        flickTimer.start();
        
        Timer logTimer = new Timer();
        logTimer.start();
        
        double turretPower = 0;
        boolean wasAuto = false;
        
	double gyroCurrentPosition = 0;
	double gyroProcessedPosition = 0;
	double gyroLastPosition = 0;
        
        Point3d[] points;
        cameraInterface.begin();
        
        while(this.isOperatorControl() && this.isEnabled()) {
            gyroCurrentPosition = gyro.getAngle();
            gyroProcessedPosition += gyroCurrentPosition - gyroLastPosition;
            gyroLastPosition = gyroCurrentPosition;
            
            if(gyroProcessedPosition >= 360) gyroProcessedPosition -= 360;
            if(gyroProcessedPosition < 0) gyroProcessedPosition += 360;
            
            if (controller.getRawButton(ButtonConfiguration.LIGHT_SWITCH) && flickTimer.get() > 1) {
                lightOn = !lightOn;

                if (lightOn)
                    light.set(Relay.Value.kOn);
                else
                    light.set(Relay.Value.kOff);
                
                System.out.println((lightOn) ? "Light ON" : "Light OFF");
                
                flickTimer.reset();
            }

            if (controller.getRawButton(ButtonConfiguration.AUTOMATED_CONTROL)) {
                wasAuto = true;
                turretController.enable();
            } else {
                if (wasAuto) {
                    wasAuto = false;
                    turretController.disable();
                }

                turretPower = controller.getRawAxis(1); // XBOX: Left stick X-ax

                if (turretPower < -0.2 || turretPower > 0.2)
                    turret.set(turretPower * -0.4);
                else
                    turret.set(0D);
            }
            
            SmartDashboard.putDouble("gyro", gyroProcessedPosition);
            
            SmartDashboard.putDouble("potentiometer", potentiometerMonitor.pidGet());
            SmartDashboard.putDouble("setpoint", turretController.getSetpoint());
            
            points = cameraInterface.getTargets();
            
            for (int i = 0; i < points.length; i++)
                System.out.println("x: " + points[i].x + ", y: " + points[i].y + ", z: " + points[i].z);
            
            System.out.println(" - UPS: " + ((RemoteCameraTCP) cameraInterface).getUPS() + " - ");
            System.out.println("-------------------------------");
            
            if (controller.getRawButton(ButtonConfiguration.LOAD_SETPOINT_FROM_VISION))
                turretController.setSetpoint(Math.toDegrees(MathUtils.asin(points[0].x / points[0].z)) - gyroProcessedPosition);
            
            runTimer.reset();
        }
        
        logTimer.stop();
        flickTimer.stop();
        runTimer.stop();
        
        cameraInterface.end();
    }
    
    public void disabled () {
        
    }
    
    public void robotInit () {
        System.out.println("Hello, ninja h4X0r.");
    }
}