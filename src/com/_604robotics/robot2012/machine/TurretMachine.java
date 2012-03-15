package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.rotation.RotationProvider;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;

/**
 * Machine to control the turret.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class TurretMachine implements StrangeMachine {
    private final PIDController controller;
    private final RotationProvider provider;
    private final Encoder encoder;
    private final Timer changeTimer = new Timer();
    
    private double turretSidewaysPosition = 0;
    private boolean isAimed = false;
    
    private int lastState = TurretState.SIDEWAYS;
    
    /**
     * The possible states the turret could be in.
     */
    public interface TurretState {
        public static final int SIDEWAYS = 0;
        public static final int AIMED = 1;
        public static final int FORWARD = 2;
        public static final int LEFT = 3;
        public static final int RIGHT = 4;
    }
    
    /**
     * Initializes a new TurretMachine.
     * 
     * @param   controller      The PIDController to control.
     * @param   provider        The RotationProvider to draw aiming data from.
     * @param   encoder         The encoder measuring the horizontal position
     *                          of the turret.
     */
    public TurretMachine (PIDController controller, RotationProvider provider, Encoder encoder) {
        this.controller = controller;
        this.provider = provider;
        this.encoder = encoder;
        
        this.changeTimer.start();
    }
    
    /**
     * Internal function to test whether or not we're relatively on target.
     * 
     * @param   target  The target position.
     * 
     * @return  Whether or not we're relatively on target.
     */
    private boolean onTarget (double target) {
        return Math.abs(target - this.encoder.getDistance()) <= ActuatorConfiguration.TURRET_POSITION.TOLERANCE;
    }

    public boolean test (int state) {
        switch (state) {
            case TurretState.SIDEWAYS:
                return this.controller.getSetpoint() == this.turretSidewaysPosition && onTarget(this.turretSidewaysPosition) && this.changeTimer.get() >= 3;
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
        if (this.lastState != state) {
            this.changeTimer.reset();
            this.lastState = state;
        }
        
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
    
    /**
     * Sets the position to use as "SIDEWAYS".
     * 
     * @param   turretSidewaysPosition  The position to use as "SIDEWAYS", in
     *                                  degrees.
     */
    public void setTurretSidewaysPosition (double turretSidewaysPosition) {
        this.turretSidewaysPosition = turretSidewaysPosition;
    }
}