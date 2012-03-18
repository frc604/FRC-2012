package com._604robotics.robot2012.rotation;

/**
 * Based on external feedback, aims the turret at the target.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface RotationProvider {
    /**
     * Sets the "default" position, if no targets can be located.
     */
    public abstract void setDefaultPosition (double defaultPosition);
    
    /**
     * Updates the aiming of the turret.
     */
    public abstract boolean update ();
}
