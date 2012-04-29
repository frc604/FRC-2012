package com._604robotics.robot2012.machine;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.dashboard.ShooterDashboard;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.utils.StrangeMachine;

/**
 * Machine to control the shooter/hopper system during firing.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class ShooterMachine implements StrangeMachine {
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
    public ShooterMachine () {
        
    }
    
    /**
     * Gets the calculated shooter speed.
     * 
     * @return  The calculated shooter speed.
     */
    public double getShooterSpeed () {
        return Robot.firingProvider.getSpeed();
    }

    public boolean test (int state) {
        switch (state) {
            case ShooterState.SHOOTING:
                return Robot.hopperMotor.get() == ActuatorConfiguration.HOPPER_POWER;
        }
        
        return false;
    }
    
    public boolean crank (int state) {
        switch (state) {
            case ShooterState.SHOOTING:
                Robot.speedProvider.setSetSpeed(
                        (Shooter.manual)
                            ? Shooter.manualSpeed
                            : Robot.firingProvider.getSpeed()
                );
                Robot.speedProvider.apply();
                
                Shooter.setCharged(Robot.speedProvider.isOnTarget(ShooterDashboard.tolerance));
                
                if (!ShooterDashboard.ignoreHeight || Robot.elevatorMachine.test(ElevatorState.HIGH))
                    Robot.elevatorMotors.set(0.15);
                else if (ShooterDashboard.ignoreHeight && !Robot.shooterMachine.test(ElevatorState.HIGH))
                    Robot.elevatorMotors.set(-0.15);
                
                return true;
        }
        
        return false;
    }
}