package com._604robotics.robot2012.measurespeedcorrelation;

import com._604robotics.robot2012.configuration.PortConfiguration;
import com._604robotics.utils.DualVictor;
import com._604robotics.utils.EncoderSamplingRate;
import com._604robotics.utils.XboxController;
import com._604robotics.utils.XboxController.Button;
import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.Encoder.PIDSourceParameter;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.microedition.io.Connector;

public class MeasureSpeedCorrelation extends SimpleRobot {
    private final DualVictor victor = new DualVictor(PortConfiguration.Motors.SHOOTER_LEFT, PortConfiguration.Motors.SHOOTER_RIGHT);
    private final EncoderSamplingRate encoder = new EncoderSamplingRate(3, 4);
    private final XboxController controller = new XboxController(1);
    
    private final PIDController pid;
    
    public MeasureSpeedCorrelation () {
        encoder.setDistancePerPulse(1);
        encoder.setPIDSourceParameter(PIDSourceParameter.kRate);
        encoder.setSamplingRate(20);
        encoder.setAveragePoints(10);
        encoder.start();
        
        SmartDashboard.putDouble("Motor Speed", SmartDashboard.getDouble("Motor Speed", 0D));
        
        pid = new PIDController(SmartDashboard.getDouble("P", -0.05), SmartDashboard.getDouble("I", -0.001), SmartDashboard.getDouble("D", 0D), encoder, victor);
        
        SmartDashboard.putDouble("Setpoint", pid.getSetpoint());
        
        SmartDashboard.putDouble("P", pid.getP());
        SmartDashboard.putDouble("I", pid.getI());
        SmartDashboard.putDouble("D", pid.getD());
    }
    
    public void autonomous () {
        
    }
    
    public void operatorControl () {
        try {
            FileConnection log = (FileConnection) Connector.open("file://" + System.currentTimeMillis() + ".txt"); 
            log.create();
            
            OutputStream logstream = log.openOutputStream();
            PrintStream logger = new PrintStream(logstream);
            
            String line = "Timestamp (in milliseconds),Input,Rate\n";

            logger.println(line);
            //System.out.println(line);
            
            while (isEnabled() && isOperatorControl()) {
                encoder.sample();
                
                if (controller.getToggle(Button.B))
                    pid.reset();
                
                if (controller.getButton(Button.B)) {
                    pid.setSetpoint(SmartDashboard.getDouble("Setpoint", 0D));
                    pid.setPID(SmartDashboard.getDouble("P", 0D), SmartDashboard.getDouble("I", 0D), SmartDashboard.getDouble("D", 0D));
                    
                    pid.enable();
                } else if (controller.getButton(Button.A)) {
                    pid.disable();
                    victor.set(SmartDashboard.getDouble("Motor Speed", 0D) * -1);
                } else {
                    pid.disable();
                    victor.set(0D);
                }
                
                SmartDashboard.putBoolean("PID Enabled", pid.isEnable());
                SmartDashboard.putDouble("Shooter Clicks", encoder.get());
                SmartDashboard.putDouble("Current Input", victor.get());
                SmartDashboard.putDouble("Current Rate", encoder.getRate());
                SmartDashboard.putDouble("Current Raw Rate", encoder.getRawRate());
                
                line = "," + System.currentTimeMillis() + "," + victor.get() + "," + encoder.getRate() + "\n";
                
                logger.println(line);
                System.out.println(line);
            }

            log.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        pid.disable();
    }
    
    public void disabled () {
        
    }
}
