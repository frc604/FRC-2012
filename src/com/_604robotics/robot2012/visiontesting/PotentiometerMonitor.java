package com._604robotics.robot2012.visiontesting;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.PIDSource;

public class PotentiometerMonitor implements PIDSource {
    private AnalogChannel potentiometer;
    
    public static double PotentiometerToDegrees( double voltage) {
        if(voltage <= 0.168)
            return (voltage - 0.09) / 0.078 * -80D + 170D;
        else
            return (voltage - 0.168) / 0.149 * -55D + 90D;
    }
    
    public PotentiometerMonitor (AnalogChannel potentiometer) {
        this.potentiometer = potentiometer;
    }
    
    public double pidGet () {
	return PotentiometerToDegrees(this.potentiometer.getVoltage());
    }
}
