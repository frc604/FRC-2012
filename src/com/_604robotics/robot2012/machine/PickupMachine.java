package com._604robotics.robot2012.machine;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Machine to control the pneumatic pickup.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class PickupMachine implements StrangeMachine {
    private final DoubleSolenoid pickup;
    private final 
    
    private int targetState = PickupState.DISCORD;
    
    public interface PickupState {
        public static final int HARMONY = 0;
        public static final int DISCORD = 1;
    }
    
    public PickupMachine (DoubleSolenoid pickup) {
        this.pickup = pickup;
    }

    public int getState() {
        
    }

    public int getTargetState() {
        
    }

    public void strive(int state) {
        
    }
}
