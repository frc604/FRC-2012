package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.utils.StrangeMachine;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;

/**
 * Machine to control the elevator.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class ElevatorMachine implements StrangeMachine {
    private final PIDController controller;
    private final Encoder encoder;
    private final DoubleSolenoid hood;
    
    private int lastState = ElevatorState.MEDIUM;
    private boolean withinTolerance = false;
    
    private DoubleSolenoid.Value hoodPosition = ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE;

    /**
     * Various possible states the elevator can be in.
     */
    public interface ElevatorState {
        public static final int HIGH = 0;
        public static final int MEDIUM = 1;
        public static final int LOW = 2;
        public static final int PICKUP_OKAY = 3;
    }
    
    /**
     * Initializes a new ElevatorMachine.
     * 
     * @param   controller  A PIDController to control.
     * @param   encoder     The encoder monitoring the elevator's vertical
     *                      position.
     */
    public ElevatorMachine (PIDController controller, Encoder encoder, DoubleSolenoid hood) {
        this.controller = controller;
        this.encoder = encoder;
        this.hood = hood;
    }
    
    public boolean test (int state) {
        switch (state) {
            case ElevatorState.HIGH:
                if (this.encoder.get() >= ActuatorConfiguration.ELEVATOR.TOLERANCE.HIGH)
                    this.withinTolerance = true;
                return this.lastState == ElevatorState.HIGH && this.withinTolerance && this.encoder.get() >= ActuatorConfiguration.ELEVATOR.DEADBAND.HIGH;
            case ElevatorState.MEDIUM:
                if (this.encoder.get() >= ActuatorConfiguration.ELEVATOR.TOLERANCE.MEDIUM_LOWER && this.encoder.get() <= ActuatorConfiguration.ELEVATOR.TOLERANCE.MEDIUM_UPPER)
                    this.withinTolerance = true;
                return this.lastState == ElevatorState.MEDIUM && this.withinTolerance && this.encoder.get() >= ActuatorConfiguration.ELEVATOR.DEADBAND.MEDIUM_LOWER && this.encoder.get() <= ActuatorConfiguration.ELEVATOR.DEADBAND.MEDIUM_UPPER;
            case ElevatorState.LOW:
                if (this.encoder.get() <= ActuatorConfiguration.ELEVATOR.TOLERANCE.LOW)
                    this.withinTolerance = true;
                return this.lastState == ElevatorState.LOW && this.withinTolerance && this.encoder.get() <= ActuatorConfiguration.ELEVATOR.DEADBAND.LOW;
            case ElevatorState.PICKUP_OKAY:
                return this.lastState != ElevatorState.LOW && this.encoder.get() >= ActuatorConfiguration.ELEVATOR.DEADBAND.MEDIUM_LOWER;
        }
        
        return false;
    }
    
    public boolean crank (int state) {
        if (this.lastState != state) {
            this.withinTolerance = false;
            this.controller.reset();
            
            if (state == ElevatorState.HIGH) {
                this.hood.set(this.hoodPosition);
            } else if (this.lastState == ElevatorState.HIGH) {
                this.hoodPosition = this.hood.get();
                this.hood.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            }
            
            this.lastState = state;
        }
        
        switch (state) {
            case ElevatorState.HIGH:
                this.controller.setSetpoint(ActuatorConfiguration.ELEVATOR.HIGH);
                break;
            case ElevatorState.MEDIUM:
                this.controller.setSetpoint(ActuatorConfiguration.ELEVATOR.MEDIUM);
                break;
            case ElevatorState.LOW:
                this.controller.setSetpoint(ActuatorConfiguration.ELEVATOR.LOW);
                break;
            default:
                this.controller.disable();
                return false;
        }
        
        boolean ret = this.test(state);
        
        if (ret) {
            if (this.controller.isEnable())
                this.controller.disable();
        } else {
            if (!this.controller.isEnable())
                this.controller.enable();
        }
        
        return ret;
    }
    
    public void setHoodPosition (DoubleSolenoid.Value hoodPosition) {
        this.hoodPosition = hoodPosition;
    }
}