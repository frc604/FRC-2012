package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

/**
 * Machine to control the pneumatic pickup.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class PickupMachine implements StrangeMachine {
    private final DoubleSolenoid pickup;
    private final Timer switchTimer = new Timer();
    
    public interface PickupState {
        public static final int OUT = 0;
        public static final int IN = 1;
    }
    
    public PickupMachine (DoubleSolenoid pickup) {
        this.pickup = pickup;
    }
    
    public boolean test (int state) {
        switch (state) {
            case PickupState.OUT:
                return this.pickup.get() == ActuatorConfiguration.SOLENOID_PICKUP.OUT && this.switchTimer.get() >= 1;
            case PickupState.IN:
                return this.pickup.get() == ActuatorConfiguration.SOLENOID_PICKUP.IN && this.switchTimer.get() >= 1;
        }
    
        return false;
    }

    public boolean crank(int state) {
        switch (state) {
            case PickupState.OUT:
                if (this.pickup.get() == ActuatorConfiguration.SOLENOID_PICKUP.IN)
                    this.switchTimer.reset();
                this.pickup.set(ActuatorConfiguration.SOLENOID_PICKUP.OUT);
                break;
            case PickupState.IN:
                if (this.pickup.get() == ActuatorConfiguration.SOLENOID_PICKUP.OUT)
                    switchTimer.reset();
                this.pickup.set(ActuatorConfiguration.SOLENOID_PICKUP.IN);
                break;
            default:
                return false;
        }
        
        return this.switchTimer.get() >= 1;
    }
}