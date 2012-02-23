package com._604robotics.robot2012.rotation;

/**
 * Based on external feedback, aims the turret at the target.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public interface RotationProvider {
    /**
     * Updates the aiming of the turret.
     */
    public abstract void update ();
}
