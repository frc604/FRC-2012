package com._604robotics.robot2012.tunetheelevator;

import com._604robotics.utils.EncoderPIDSource;
import com._604robotics.robot2012.ActuatorConfiguration;
import com._604robotics.robot2012.PortConfiguration;
import com._604robotics.utils.DualVictor;
import com._604robotics.utils.UpDownPIDController;
import com._604robotics.utils.XboxController;
import com._604robotics.utils.XboxController.Axis;
import com._604robotics.utils.XboxController.Button;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TuneTheElevator extends SimpleRobot {
    private final DualVictor elevator = new DualVictor(PortConfiguration.Motors.ELEVATOR_LEFT, PortConfiguration.Motors.ELEVATOR_RIGHT);
    private final EncoderPIDSource encoder = new EncoderPIDSource(PortConfiguration.Encoders.ELEVATOR_A, PortConfiguration.Encoders.ELEVATOR_B);
    private final XboxController controller = new XboxController(1);
    
    private final UpDownPIDController pidElevator;
    
    public TuneTheElevator () {
        encoder.start();
        
        pidElevator = new UpDownPIDController(new UpDownPIDController.Gains(SmartDashboard.getDouble("Up P", 0D), SmartDashboard.getDouble("Up I", 0D), SmartDashboard.getDouble("Up D", 0D)), new UpDownPIDController.Gains(SmartDashboard.getDouble("Down P", 0D), SmartDashboard.getDouble("Down I", 0D), SmartDashboard.getDouble("Down D", 0D)), encoder, elevator);
        
        pidElevator.setSetpoint(SmartDashboard.getDouble("Setpoint", 0D));
        pidElevator.setOutputRange(ActuatorConfiguration.ELEVATOR_POWER_MIN, ActuatorConfiguration.ELEVATOR_POWER_MAX);
        pidElevator.setSetpoint(0D);
        
        SmartDashboard.putDouble("Setpoint", pidElevator.getSetpoint());

        SmartDashboard.putDouble("Up P", pidElevator.getUpGains().P);
        SmartDashboard.putDouble("Up I", pidElevator.getUpGains().I);
        SmartDashboard.putDouble("Up D", pidElevator.getUpGains().D);
        
        SmartDashboard.putDouble("Down P", pidElevator.getDownGains().P);
        SmartDashboard.putDouble("Down I", pidElevator.getDownGains().I);
        SmartDashboard.putDouble("Down D", pidElevator.getDownGains().D);
    }
    
    public void autonomous () {
        
    }

    public void operatorControl () {
        while (isEnabled() && isOperatorControl()) {
            if (controller.getButton(Button.A))
                encoder.reset();
            
            if (controller.getToggle(Button.B))
                pidElevator.disable();
            
            if (controller.getButton(Button.B)) {
                pidElevator.enable();

                pidElevator.setSetpoint(SmartDashboard.getDouble("Setpoint", 0D));
                pidElevator.setUpGains(new UpDownPIDController.Gains(SmartDashboard.getDouble("Up P", 0D), SmartDashboard.getDouble("Up I", 0D), SmartDashboard.getDouble("Up D", 0D)));
                pidElevator.setDownGains(new UpDownPIDController.Gains(SmartDashboard.getDouble("Down P", 0D), SmartDashboard.getDouble("Down I", 0D), SmartDashboard.getDouble("Down D", 0D)));
            } else {
                elevator.set(controller.getAxis(Axis.RIGHT_STICK_Y));
            }
            
            SmartDashboard.putDouble("Current Position", encoder.get());
        }
        
        pidElevator.disable();
    }
    
    public void disabled () {
        
    }
}