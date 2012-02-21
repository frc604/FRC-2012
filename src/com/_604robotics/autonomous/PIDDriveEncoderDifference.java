package com._604robotics.autonomous;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * This class implements a PIDSource, based on the difference of values between
 * two encoders.
 * 
 * @author Aaron Wang <aaronw94@gmail.com>
 */
public class PIDDriveEncoderDifference implements PIDSource {
    private final Encoder leftEncoder;
    private final Encoder rightEncoder;
    
    /**
     * Initializes a new PIDDriveEncoderDifference, based on the given encoders.
     * 
     * @param   leftEncoder     The left encoder to monitor the value of.
     * @param   rightEncoder    The right encoder to monitor the value of.
     */
    public PIDDriveEncoderDifference(Encoder leftEncoder, Encoder rightEncoder) {
        this.leftEncoder = leftEncoder;
        this.rightEncoder = rightEncoder;
    }

    /**
     * Gets the difference between the two encoder values, as an output to a
     * PID controller.
     * 
     * @return  The difference between the two encoder values.
     */
    public double pidGet() {
        return leftEncoder.get() - rightEncoder.get();
    }
}