package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.rotation.RotationProvider;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;

/**
 * Machine to control the turret.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class TurretMachine implements StrangeMachine {
    private final PIDController controller;
    private final RotationProvider provider;
    private final Encoder encoder;
    
    private double turretSidewaysPosition = 0;
    private boolean isAimed = false;
    
    public interface TurretState {
        public static final int SIDEWAYS = 0;
        public static final int AIMED = 1;
        public static final int FORWARD = 2;
        public static final int LEFT = 3;
        public static final int RIGHT = 4;
    }
    
    public TurretMachine (PIDController controller, RotationProvider provider, Encoder encoder) {
        this.controller = controller;
        this.provider = provider;
        this.encoder = encoder;
    }
    
    private boolean onTarget (double target) {
        return Math.abs(target - this.encoder.get()) <= ActuatorConfiguration.TURRET_POSITION.TOLERANCE;
    }

    public boolean test (int state) {
        switch (state) {
            case TurretState.SIDEWAYS:
                return this.controller.getSetpoint() == this.turretSidewaysPosition && onTarget(this.turretSidewaysPosition);
            case TurretState.AIMED:
                return this.isAimed;
            case TurretState.FORWARD:
                return this.controller.getSetpoint() == ActuatorConfiguration.TURRET_POSITION.FORWARD && onTarget(ActuatorConfiguration.TURRET_POSITION.FORWARD);
            case TurretState.LEFT:
                return this.controller.getSetpoint() == ActuatorConfiguration.TURRET_POSITION.LEFT && onTarget(ActuatorConfiguration.TURRET_POSITION.LEFT);
            case TurretState.RIGHT:
                return this.controller.getSetpoint() == ActuatorConfiguration.TURRET_POSITION.RIGHT && onTarget(ActuatorConfiguration.TURRET_POSITION.RIGHT);
        }
        
        return false;
    }
    
    public boolean crank (int state) {
        switch (state) {
            case TurretState.SIDEWAYS:
                this.controller.setSetpoint(this.turretSidewaysPosition);
                break;
            case TurretState.AIMED:
                this.isAimed = this.provider.update();
                return this.isAimed;
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
                this.isAimed = false;
                return false;
        }
        
        this.isAimed = false;
        
        if (!this.controller.isEnable())
            this.controller.enable();
        
        return this.test(state);
    }
    
    public void setTurretSidewaysPosition (double turretSidewaysPosition) {
        this.turretSidewaysPosition = turretSidewaysPosition;
    }
}