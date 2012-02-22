package com._604robotics.robot2012.machine;

/**
 * State manager for various components of the robot.
 * 
 * Used for coordinating switches between states involving multiple steps and
 * components.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface StrangeMachine {
    /**
     * Gets the current state of the Machine.
     * 
     * @return  The current state of the Machine.
     */
    public abstract int getState ();
    
    /**
     * Gets the state the Machine is currently striving for.
     * 
     * @return  The state the Machine is currently striving for.
     */
    public abstract int getTargetState ();
    
    /**
     * Causes the Machine to strive for the target state.
     */
    public abstract void crank ();
    
    /**
     * Sets the state that the Machine should strive for.
     * 
     * @param   state   The state that the Machine should strive for.
     */
    public abstract void strive (int state);
}