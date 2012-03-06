package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import edu.wpi.first.wpilibj.PIDController;

/**
 * Machine to control the turret.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class TurretMachine implements StrangeMachine {
    private final PIDController controller;
    
    public interface TurretState {
        public static final int SIDEWAYS = 0;
    }
    
    public TurretMachine (PIDController controller) {
        this.controller = controller;
    }

    public boolean test (int state) {
        switch (state) {
            case TurretState.SIDEWAYS:
                return this.controller.getSetpoint() == ActuatorConfiguration.TURRET_SIDEWAYS_POSITION && this.controller.onTarget();
        }
        
        return false;
    }
    
    public boolean crank (int state) {
        switch (state) {
            case TurretState.SIDEWAYS:
                this.controller.setSetpoint(ActuatorConfiguration.TURRET_SIDEWAYS_POSITION);
                break;
            default:
                this.controller.disable();
                return false;
        }
        
        if (!this.controller.isEnable())
            this.controller.enable();
        
        return this.controller.onTarget();
    }
}