package com._604robotics.robot2012.rotation;

import edu.wpi.first.wpilibj.PIDController;

/**
 * Dummy implementor of a RotationProvider, for testing purposes.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class DummyRotationProvider implements RotationProvider {
    private final PIDController controller;
    
    /**
     * Initializes a new DummyRotationProvider, giving it control over the
     * specified PIDController.
     * 
     * @param   controller  The PIDController to control.
     */
    public DummyRotationProvider (PIDController controller) {
        this.controller = controller;
    }
    
    public void update () {
        this.controller.setSetpoint(0D);
    }
}