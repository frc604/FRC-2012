package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * This class implements a controller with a horizontal segment, a linear
 * segment, and finally a coasting segment.
 * 
 * When a target point is set, the controller decides which direction to go to
 * get there, and then focuses on getting to that point or past it in that
 * direction. If that condition is met, the output drops to zero. Else, if
 * we're within a certain "coasting range", the output will be floored at the
 * "coasting output". Else, if we're outside a certain "horizontal range", the
 * output will be ceilinged at a certain "horizontal output". Else, the output
 * will be scaled linearly between the two outputs.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class LinearController {
    private final PIDSource source;
    private final PIDOutput output;
    
    private double horizontalRange;
    private double horizontalOutput;
    
    private double coastingRange;
    private double coastingOutput;
    
    private double target = 0D;
    private boolean direction = false;
    
    /**
     * Internal enum to make remembering which direction is which easier.
     */
    private interface Direction {
        public static final boolean FORWARD = true;
        public static final boolean REVERSE = false;
    }
    
    /**
     * Initializes a new LinearController.
     * 
     * @param   source              A PIDSource to read from.
     * @param   output              A PIDOutput to write to.
     * @param   horizontalRange     The horizontal range, as defined in the
     *                              class description.
     * @param   horizontalOutput    The horizontal output, as defined in the
     *                              class description.
     * @param   coastingRange       The coasting range, as defined in the class
     *                              description.
     * @param   coastingOutput      The coasting output, as defined in the class
     *                              description.
     */
    public LinearController (PIDSource source, PIDOutput output, double horizontalRange, double horizontalOutput, double coastingRange, double coastingOutput) {
        this.source = source;
        this.output = output;
        this.horizontalRange = horizontalRange;
        this.horizontalOutput = horizontalOutput;
        this.coastingRange = coastingRange;
        this.coastingOutput = coastingOutput;
    }
    
    /**
     * Updates the horizontal values.
     * 
     * @param   horizontalRange     The horizontal range, as defined in the
     *                              class description.
     * @param   horizontalOutput    The horizontal output, as defined in the
     *                              class description.
     */
    public void setHorizontalValues (double horizontalRange, double horizontalOutput) {
        this.horizontalRange = horizontalRange;
        this.horizontalOutput = horizontalOutput;
    }
    
    /**
     * Updates the coasting values.
     * 
     * @param   horizontalRange     The coasting range, as defined in the
     *                              class description.
     * @param   horizontalOutput    The coasting output, as defined in the
     *                              class description.
     */
    public void setCoastingRange (double coastingRange, double coastingOutput) {
        this.coastingRange = coastingRange;
        this.coastingOutput = coastingOutput;
    }
    
    /**
     * Gets the current target.
     * 
     * @return  The current target.
     */
    public double getTarget () {
        return this.target;
    }
    
    /**
     * Sets the current target.
     * 
     * @param   target  The target to move toward.
     */
    public void setTarget (double target) {
        this.target = target;
        if (this.target > this.source.pidGet())
            this.direction = Direction.FORWARD;
        else
            this.direction = Direction.REVERSE;
    }
    
    /**
     * Are we there yet?
     * 
     * Internal version that takes a pos parameter.
     * 
     * @param   pos     Our current position.
     * @return  Whether or not we're there yet.
     */
    private boolean onTarget (double pos) {
        return (this.direction == Direction.FORWARD && pos >= this.target) || (this.direction == Direction.REVERSE && pos <= this.target);
    }
    
    /**
     * Are we there yet?
     * 
     * @return  Whether or not we're there yet.
     */
    public boolean onTarget () {
        return this.onTarget(this.source.pidGet());
    }
    
    /**
     * Internal function that performs the output calculation.
     * 
     * @return  An output value, to be passed to a PIDOutput.
     */
    private double calculate () {
        double pos = this.source.pidGet();
        double dist = Math.abs(pos - this.target);
        
        if (this.onTarget())
            return 0D;
        
        if (dist <= this.coastingRange)
            return this.coastingOutput;
        
        if (dist >= this.horizontalRange)
            return this.horizontalOutput;
        
        return (this.horizontalOutput - this.coastingOutput) * ((dist - this.coastingRange) / (this.horizontalRange - this.coastingRange)) + this.coastingOutput;
    }
    
    /**
     * Updates the PIDOutput based on the latest data.
     */
    public void update () {
        this.output.pidWrite(this.calculate());
    }
}