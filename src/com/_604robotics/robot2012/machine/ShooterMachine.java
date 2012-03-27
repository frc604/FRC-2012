package com._604robotics.robot2012.machine;

import com._604robotics.utils.StrangeMachine;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.firing.FiringProvider;
import com._604robotics.utils.DualVictor;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

/**
 * Machine to control the shooter/hopper system during firing.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterMachine implements StrangeMachine {
    // TODO: Integrate with Kevin's SpeedProvider stuff.
    
    private final Timer spinTimer = new Timer();
    private final Timer sinceTimer = new Timer();
    
    private final DualVictor shooter;
    private final Victor hopper;
    private final FiringProvider provider;
    
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
    public ShooterMachine (DualVictor shooter, Victor hopper, FiringProvider provider) {
        this.shooter = shooter;
        this.hopper = hopper;
        this.provider = provider;
        
        this.sinceTimer.start();
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
                this.shooter.set(this.provider.getSpeed());
                
                if (this.sinceTimer.get() >= 0.25) {
                    System.out.println("RESETTING TIMER");
                    
                    this.spinTimer.reset();
                    this.spinTimer.start();
                
                    this.sinceTimer.reset();
                    
                    return false;
                } else if (this.spinTimer.get() >= 0.5) {
                    System.out.println("SHOOTING NOW");
                    this.hopper.set(ActuatorConfiguration.HOPPER_POWER);
                } else {
                    System.out.println("CHARGING UP");
                }
                
                this.sinceTimer.reset();
                
                return true;
        }
        
        this.spinTimer.stop();
        this.shooter.set(0D);
        
        return false;
    }
}