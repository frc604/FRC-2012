package com._604robotics.utils;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.GyroHax;

public class CompensatingGyro extends GyroHax {
    /**
     * Initializes a new CompensatingGyro on the specified PWM port.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   port    The PWM port the gyro is plugged into. Must be 1 or 2!
     */
    public CompensatingGyro (int port) {
        super(port);
    }
    
    /**
     * Initializes a new CompensatingGyro on the specified PWM port on the specified
     * module port.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   slot    The module slot the gyro is plugged into.
     * @param   port    The PWM port the gyro is plugged into. Must be 1 or 2!
     */
    public CompensatingGyro (int slot, int port) {
        super(slot, port);
    }
    
    /**
     * Initializes a new CompensatingGyro on the specified AnalogChannel.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   channel The AnalogChannel the gyro is plugged into.
     */
    public CompensatingGyro (AnalogChannel channel) {
        super(channel);
    }
    
    public void setAccumulatorCenter(int center) {
        this.getAnalogChannel().setAccumulatorCenter(kPwmChannels);
    }
}