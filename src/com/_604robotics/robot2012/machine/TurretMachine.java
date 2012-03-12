package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.rotation.RotationProvider;
import edu.wpi.first.wpilibj.PIDController;

/**
 * Machine to control the turret.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class TurretMachine implements StrangeMachine {
    private final PIDController controller;
    private final RotationProvider provider;
    
    private int turretSidewaysPosition = 0;
    
    public interface TurretState {
        public static final int SIDEWAYS = 0;
        public static final int AIMED = 1;
        public static final int FORWARD = 2;
        public static final int LEFT = 3;
        public static final int RIGHT = 4;
    }
    
    public TurretMachine (PIDController controller, RotationProvider provider) {
        this.controller = controller;
        this.provider = provider;
    }

    public boolean test (int state) {
        switch (state) {
            case TurretState.SIDEWAYS:
                return this.controller.getSetpoint() == this.turretSidewaysPosition && this.controller.onTarget();
            case TurretState.AIMED:
                return this.controller.onTarget();
            case TurretState.FORWARD:
                return this.controller.getSetpoint() == ActuatorConfiguration.TURRET_POSITION.FORWARD && this.controller.onTarget();
            case TurretState.LEFT:
                return this.controller.getSetpoint() == ActuatorConfiguration.TURRET_POSITION.LEFT && this.controller.onTarget();
            case TurretState.RIGHT:
                return this.controller.getSetpoint() == ActuatorConfiguration.TURRET_POSITION.RIGHT && this.controller.onTarget();
        }
        
        return false;
    }
    
    public boolean crank (int state) {
        switch (state) {
            case TurretState.SIDEWAYS:
                this.controller.setSetpoint(this.turretSidewaysPosition);
                break;
            case TurretState.AIMED:
                return this.provider.update();
            case TurretState.FORWARD:
                this.controller.setSetpoint(ActuatorConfiguration.TURRET_POSITION.FORWARD);
                break;
            case TurretState.LEFT:
                this.controller.setSetpoint(ActuatorConfiguration.TURRET_POSITION.LEFT);
                break;
            case TurretState.RIGHT:
                this.controller.setSetpoint(ActuatorConfiguration.TURRET_POSITION.RIGHT);
                break;
            default:
                this.controller.disable();
                return false;
        }
        
        if (!this.controller.isEnable())
            this.controller.enable();
        
        return this.controller.onTarget();
    }
    
    public void setTurretSidewaysPosition (int turretSidewaysPosition) {
        this.turretSidewaysPosition = turretSidewaysPosition;
    }
}