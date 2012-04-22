package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.utils.StrangeMachine;
import edu.wpi.first.wpilibj.Timer;

/**
 * Machine to control the pneumatic pickup.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class PickupMachine implements StrangeMachine {
    private final Timer switchTimer = new Timer();
    
    /**
     * Possible states the pickup could be in.
     */
    public interface PickupState {
        public static final int OUT = 0;
        public static final int IN = 1;
    }
    
    /**
     * Initializes a new PickupMachine.
     * 
     * @param   pickup  The solenoid of the pickup to control.
     */
    public PickupMachine () {
        this.switchTimer.start();
    }
    
    public boolean test (int state) {
        switch (state) {
            case PickupState.OUT:
                return Robot.solenoidPickup.get() == ActuatorConfiguration.SOLENOID_PICKUP.OUT && this.switchTimer.get() >= 1;
            case PickupState.IN:
                return Robot.solenoidPickup.get() == ActuatorConfiguration.SOLENOID_PICKUP.IN && this.switchTimer.get() >= 1;
        }
    
        return false;
    }

    public boolean crank(int state) {
        switch (state) {
            case PickupState.OUT:
                if (Robot.solenoidPickup.get() != ActuatorConfiguration.SOLENOID_PICKUP.OUT)
                    this.switchTimer.reset();
                Robot.solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.OUT);
                break;
            case PickupState.IN:
                if (Robot.solenoidPickup.get() != ActuatorConfiguration.SOLENOID_PICKUP.IN)
                    switchTimer.reset();
                Robot.solenoidPickup.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
                break;
            default:
                return false;
        }
        
        return this.switchTimer.get() >= 1;
    }
}