package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.utils.StrangeMachine;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Machine to control the elevator.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class ElevatorMachine implements StrangeMachine {
    private int lastState = ElevatorState.MEDIUM;
    private boolean withinTolerance = false;
    
    private DoubleSolenoid.Value hoodPosition = ActuatorConfiguration.SOLENOID_SHOOTER.UPPER_ANGLE;

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
    public ElevatorMachine () {
        
    }
    
    public boolean test (int state) {
        switch (state) {
            case ElevatorState.HIGH:
                if (Robot.encoderElevator.get() >= ActuatorConfiguration.ELEVATOR.TOLERANCE.HIGH)
                    this.withinTolerance = true;
                return this.lastState == ElevatorState.HIGH && this.withinTolerance && Robot.encoderElevator.get() >= ActuatorConfiguration.ELEVATOR.DEADBAND.HIGH;
            case ElevatorState.MEDIUM:
                if (Robot.encoderElevator.get() >= ActuatorConfiguration.ELEVATOR.TOLERANCE.MEDIUM_LOWER && Robot.encoderElevator.get() <= ActuatorConfiguration.ELEVATOR.TOLERANCE.MEDIUM_UPPER)
                    this.withinTolerance = true;
                return this.lastState == ElevatorState.MEDIUM && this.withinTolerance && Robot.encoderElevator.get() >= ActuatorConfiguration.ELEVATOR.DEADBAND.MEDIUM_LOWER && Robot.encoderElevator.get() <= ActuatorConfiguration.ELEVATOR.DEADBAND.MEDIUM_UPPER;
            case ElevatorState.LOW:
                if (Robot.encoderElevator.get() <= ActuatorConfiguration.ELEVATOR.TOLERANCE.LOW)
                    this.withinTolerance = true;
                return this.lastState == ElevatorState.LOW && this.withinTolerance && Robot.encoderElevator.get() <= ActuatorConfiguration.ELEVATOR.DEADBAND.LOW;
            case ElevatorState.PICKUP_OKAY:
                return this.lastState != ElevatorState.LOW && Robot.encoderElevator.get() >= ActuatorConfiguration.ELEVATOR.DEADBAND.MEDIUM_LOWER;
        }
        
        return false;
    }
    
    public boolean crank (int state) {
        if (this.lastState != state) {
            this.withinTolerance = false;
            Robot.pidElevator.reset();
            
            if (state == ElevatorState.HIGH) {
                Robot.solenoidShooter.set(this.hoodPosition);
            } else if (this.lastState == ElevatorState.HIGH) {
                this.hoodPosition = Robot.solenoidShooter.get();
                Robot.solenoidShooter.set(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);
            }
            
            this.lastState = state;
        }
        
        switch (state) {
            case ElevatorState.HIGH:
                Robot.pidElevator.setSetpoint(ActuatorConfiguration.ELEVATOR.HIGH);
                break;
            case ElevatorState.MEDIUM:
                Robot.pidElevator.setSetpoint(ActuatorConfiguration.ELEVATOR.MEDIUM);
                break;
            case ElevatorState.LOW:
                Robot.pidElevator.setSetpoint(ActuatorConfiguration.ELEVATOR.LOW);
                break;
            default:
                Robot.pidElevator.disable();
                return false;
        }
        
        boolean ret = this.test(state);
        
        if (ret) {
            if (Robot.pidElevator.isEnable())
                Robot.pidElevator.disable();
        } else {
            if (!Robot.pidElevator.isEnable())
                Robot.pidElevator.enable();
        }
        
        return ret;
    }
    
    public void setHoodPosition (DoubleSolenoid.Value hoodPosition) {
        this.hoodPosition = hoodPosition;
    }
}