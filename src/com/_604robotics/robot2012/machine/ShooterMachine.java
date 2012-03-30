package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.firing.FiringProvider;
import com._604robotics.robot2012.speedcontrol.SpeedProvider;
import com._604robotics.utils.StrangeMachine;
import edu.wpi.first.wpilibj.Victor;

/**
 * Machine to control the shooter/hopper system during firing.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterMachine implements StrangeMachine {
    private final Victor hopper;
    private final FiringProvider provider;
    private final SpeedProvider shooter;
    
    /**
     * The possible states the shooter could be in.
     */
    public interface ShooterState {
        public static final int SHOOTING = 0;
    }
    
    /**
     * Initializes a new ShooterMachine.
     * 
     * @param   shooter     The motors of the shooter to control.
     * @param   hopper      The motor of the hopper to control.
     */
    public ShooterMachine (Victor hopper, FiringProvider provider, SpeedProvider shooter) {
        this.hopper = hopper;
        this.provider = provider;
        this.shooter = shooter;
    }
    
    /**
     * Gets the calculated shooter speed.
     * 
     * @return  The calculated shooter speed.
     */
    public double getShooterSpeed () {
        return this.provider.getSpeed();
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
                this.shooter.setSetSpeed(this.provider.getSpeed());
                this.shooter.apply();
                
                if (this.shooter.isOnTarget(ActuatorConfiguration.SHOOTER_SPEED_TOLERANCE)) {
                    System.out.println("SHOOTING NOW");
                    this.hopper.set(ActuatorConfiguration.HOPPER_POWER);
                } else {
                    System.out.println("CHARGING UP");
                }
                
                return true;
        }
        
        return false;
    }
}