package com._604robotics.utils;

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
     * Tests if the Machine has yet attained the target state.
     * 
     * @param   state   The target state.
     * 
     * @return  Whether or not the Machine has attained the target state.
     */
    public abstract boolean test (int state);
    
    /**
     * Causes the Machine to strive for the target state.
     * 
     * @param   state   The state to strive for.
     * 
     * @return  Whether or not the target state has been reached.
     */
    public abstract boolean crank (int state);
}