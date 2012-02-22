package com._604robotics.utils;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * Wrapper class to constrain the output of a Gyro to 360 degrees, looping.
 * 
 * @author  Michael Smith <mdsmt@gmail.com>
 */
public class Gyro360 extends Gyro implements PIDSource {
    /**
     * Initializes a new Gyro360 on the specified PWM port.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   port    The PWM port the gyro is plugged into. Must be 1 or 2!
     */
    public Gyro360 (int port) {
        super(port);
    }
    
    /**
     * Initializes a new Gyro360 on the specified PWM port on the specified
     * module port.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   slot    The module slot the gyro is plugged into.
     * @param   port    The PWM port the gyro is plugged into. Must be 1 or 2!
     */
    public Gyro360 (int slot, int port) {
        super(slot, port);
    }
    
    /**
     * Initializes a new Gyro360 on the specified AnalogChannel.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   channel The AnalogChannel the gyro is plugged into.
     */
    public Gyro360 (AnalogChannel channel) {
        super(channel);
    }
    
    /**
     * Gets the angle of the gyro, constrained to 360 degrees.
     * 
     * @return  The angle of the gyro, constrained to 360 degrees.
     */
    public double getAngle () {
        return super.getAngle() % 360D;
    }
    
    /**
     * Implements the pidGet() function in the type PIDSource, allowing this
     * class to be used as such.
     * 
     * @return  The angle of the gyro, constrained to 360 degrees.
     */
    public double pidGet () {
        return super.pidGet() % 360D;
    }
}