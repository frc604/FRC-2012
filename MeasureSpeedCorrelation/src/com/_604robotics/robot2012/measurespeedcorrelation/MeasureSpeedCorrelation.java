package com._604robotics.robot2012.measurespeedcorrelation;

import com._604robotics.robot2012.configuration.PortConfiguration;
import com._604robotics.robot2012.speedcontrol.ProcessSpeedProvider;
import com._604robotics.utils.DualVictor;
import com._604robotics.utils.EncoderSamplingRate;
import com._604robotics.utils.XboxController;
import com._604robotics.utils.XboxController.Button;
import com.sun.squawk.microedition.io.FileConnection;
import edu.wpi.first.wpilibj.Encoder.PIDSourceParameter;
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
    
    private final ProcessSpeedProvider provider;
    
    public MeasureSpeedCorrelation () {
        encoder.setDistancePerPulse(1);
        encoder.setPIDSourceParameter(PIDSourceParameter.kRate);
        encoder.setSamplingRate(20);
        encoder.setFac(SmartDashboard.getDouble("fac", 0.5));
        encoder.start();
        
        SmartDashboard.putDouble("Motor Speed", SmartDashboard.getDouble("Motor Speed", 0D));
        
        provider = new ProcessSpeedProvider(SmartDashboard.getDouble("P", -0.003), SmartDashboard.getDouble("I", 0D), SmartDashboard.getDouble("D", -0.05), encoder, victor);
        
        SmartDashboard.putDouble("Setpoint", provider.getSetSpeed());
        
        SmartDashboard.putDouble("P", provider.getP());
        SmartDashboard.putDouble("I", provider.getI());
        SmartDashboard.putDouble("D", provider.getD());
        
        SmartDashboard.putDouble("fac", encoder.getFac());
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
                encoder.setFac(SmartDashboard.getDouble("fac", encoder.getFac()));
                encoder.sample();
                
                if (controller.getToggle(Button.B))
                    provider.reset();
                
                if (controller.getButton(Button.B)) {
                    provider.setSetSpeed(SmartDashboard.getDouble("Setpoint", 0D));
                    provider.setPID(SmartDashboard.getDouble("P", 0D), SmartDashboard.getDouble("I", 0D), SmartDashboard.getDouble("D", 0D));
                    
                    provider.apply();
                } else if (controller.getButton(Button.A)) {
                    provider.reset();
                    victor.set(SmartDashboard.getDouble("Motor Speed", 0D) * -1);
                } else {
                    provider.reset();
                    victor.set(0D);
                }
                
                if (controller.getToggle(Button.LB))
                    SmartDashboard.putString("YOU ARE AT THE ", "FENDER");
                else if (controller.getToggle(Button.LT))
                    SmartDashboard.putString("YOU ARE AT THE ", "KEY");
                
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
        
        provider.reset();
    }
    
    public void disabled () {
        
    }
}
