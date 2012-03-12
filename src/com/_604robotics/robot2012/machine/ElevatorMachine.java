package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
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
    
    public interface ElevatorState {
        public static final int HIGH = 0;
        public static final int MEDIUM = 1;
        public static final int LOW = 2;
        public static final int PICKUP_OKAY = 3;
    }
    
    public ElevatorMachine (PIDController controller, Encoder encoder) {
        this.controller = controller;
        this.encoder = encoder;
    }

    public boolean test (int state) {
        switch (state) {
            case ElevatorState.HIGH:
                //return this.controller.getSetpoint() == ActuatorConfiguration.ELEVATOR.HIGH && this.controller.onTarget();
                return true;
            case ElevatorState.MEDIUM:
                return this.controller.getSetpoint() == ActuatorConfiguration.ELEVATOR.MEDIUM && this.controller.onTarget();
            case ElevatorState.LOW:
                return this.controller.getSetpoint() == ActuatorConfiguration.ELEVATOR.LOW && this.controller.onTarget();
            case ElevatorState.PICKUP_OKAY:
                //return this.encoder.get() >= ActuatorConfiguration.ELEVATOR.MEDIUM;
                return true;
        }
        
        return false;
    }
    
    public boolean crank (int state) {
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
        
        if (!this.controller.isEnable())
            this.controller.enable();
        
        return (state == ElevatorState.HIGH) || this.controller.onTarget();
    }
}