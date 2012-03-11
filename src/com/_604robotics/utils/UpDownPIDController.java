package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * A PIDController with different gains for up and down.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class UpDownPIDController extends PIDController {
    private final PIDSource source;
    
    private Gains upGains;
    private Gains downGains;
    
    private boolean goingUp = false;
    
    /**
     * A structure containing the P, I, and D gains.
     */
    public static class Gains {
        public double P;
        public double I;
        public double D;
        
        public Gains (double P, double I, double D) {
            this.P = P;
            this.I = I;
            this.D = D;
        }
    }
    
    /**
     * Initializes a new UpDownPIDController.
     * 
     * @param   upGains     The gains to use when going up.
     * @param   downGains   The gains to use when going down.
     * @param   source      The PIDSource to plug in.
     * @param   output      The PIDOutput to plug in.
     */
    public UpDownPIDController (Gains upGains, Gains downGains, PIDSource source, PIDOutput output) {
        super(upGains.P, upGains.I, upGains.D, source, output);
        
        this.source = source;

        this.upGains = upGains;
        this.downGains = downGains;
    }
    
    /**
     * Gets the Gains for going up.
     * 
     * @return  The gains for going up.
     */
    public Gains getUpGains () {
        return this.upGains;
    }
    
    /**
     * Gets the Gains for going down.
     * 
     * @return  The gains for going down.
     */
    public Gains getDownGains () {
        return this.downGains;
    }
    
    /**
     * Updates the gains for the current direction.
     */
    public void refreshGains () {
        if (this.goingUp)
            super.setPID(this.upGains.P, this.upGains.I, this.upGains.D);
        else
            super.setPID(this.downGains.P, this.downGains.I, this.downGains.D);
        
        System.out.println(this.goingUp);
        System.out.println("P: " + super.getP() + " I: " + super.getI() + " D: " + super.getD());
    }
    
    /**
     * Sets the gains for going up.
     * 
     * @param   upGains     The gains to use when going up.
     */
    public void setUpGains (Gains upGains) {
        this.upGains = upGains;
        this.refreshGains();
    }
    
    /**
     * Sets the gains for going down.
     * 
     * @param   downGains   The gains to use when going down.
     */
    public void setDownGains (Gains downGains) {
        this.downGains = downGains;
        this.refreshGains();
    }
    
    /**
     * Sets the setpoint to go to.
     * 
     * @param   setpoint    The setpoint to go to.
     */
    public synchronized void setSetpoint(double setpoint) {
        super.setSetpoint(setpoint);
        this.goingUp = setpoint > this.source.pidGet();
    }
}