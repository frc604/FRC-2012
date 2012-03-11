package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.utils.DualVictor;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

/**
 * Machine to control the shooter/hopper system during firing.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterMachine implements StrangeMachine {
    private final Timer spinTimer = new Timer();
    
    private final DualVictor shooter;
    private final Victor hopper;
    
    public interface ShooterState {
        public static final int SHOOTING = 0;
    }
    
    public ShooterMachine (DualVictor shooter, Victor hopper) {
        this.shooter = shooter;
        this.hopper = hopper;
    }

    public boolean test (int state) {
        switch (state) {
            case ShooterState.SHOOTING:
                return this.hopper.get() == ActuatorConfiguration.HOPPER_POWER;
        }
        
        return false;
    }
    
    public boolean crank (int state) {
        switch (state) {
            case ShooterState.SHOOTING:
                if (this.shooter.get() != ActuatorConfiguration.SHOOTER_POWER) {
                    this.shooter.set(ActuatorConfiguration.SHOOTER_POWER);
                    
                    this.spinTimer.reset();
                    this.spinTimer.start();
                    
                    return false;
                } else if (this.spinTimer.get() >= 0.5) {
                    this.shooter.set(ActuatorConfiguration.SHOOTER_POWER);
                    this.hopper.set(ActuatorConfiguration.HOPPER_POWER);
                } else {
                    this.shooter.set(ActuatorConfiguration.SHOOTER_POWER);
                }
        }
        
        this.spinTimer.stop();
        this.shooter.set(0D);
        
        return false;
    }
}