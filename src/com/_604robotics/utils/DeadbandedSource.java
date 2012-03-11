package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * Implements a PIDSource, wrapping around another PIDSource, with a deadband
 * range.
 * 
 * If we're within the deadband, it'll tell the PIDController we're at where
 * it wants to be.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class DeadbandedSource implements PIDSource {
    private final PIDSource source;
    
    private PIDController controller = null;
    
    private double upperDeadband = 0D;
    private double lowerDeadband = 0D;

    /**
     * Initializes a new DeadbandedSource.
     * 
     * @param   source  The underlying PIDSource to wrap around.
     */
    public DeadbandedSource (PIDSource source) {
        this.source = source;
    }
    
    /**
     * Sets the PIDController the source is fed into.
     * 
     * @param   controller  The PIDController the source is fed into.
     */
    public void setController (PIDController controller) {
        this.controller = controller;
    }

    /**
     * Sets the range for the deadband.
     * 
     * @param   lowerDeadband   The lower bound of the deadband.
     * @param   upperDeadband   The upper bound of the deadband.
     */
    public void setDeadband (double lowerDeadband, double upperDeadband) {
        this.lowerDeadband = lowerDeadband;
        this.upperDeadband = upperDeadband;
    }

    /**
     * Hooks into PIDSource - gets the value to send to the PIDController.
     * 
     * With a deadband!
     * 
     * @return  The value to send to the PIDController.
     */
    public double pidGet () {
        double val = this.source.pidGet();
        if (this.controller != null && val > this.lowerDeadband && val < this.upperDeadband)
            val = this.controller.getSetpoint();
        return val;
    }
}
