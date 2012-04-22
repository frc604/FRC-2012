package com._604robotics.robot2012.control.modes.hybrid;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.robot2012.dashboard.AutonomousDashboard;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class ShootControlMode implements ControlMode {
	Timer controlTimer;

	public void init() {
        Drive.drive(0D);
        AutonomousDashboard.setDone(false);

		controlTimer = new Timer();
		controlTimer.start();
	}
	
	public boolean step() {
        if (Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT))
            return false;
        
        Elevator.goUp();
        
        if (Robot.elevatorMachine.test(ElevatorState.HIGH) || controlTimer.get() < AutonomousDashboard.step1) {
            AutonomousDashboard.setStep(2);
            
            if (controlTimer.get() < AutonomousDashboard.step2) {
                Shooter.shoot();
                Shooter.driveHopper(Shooter.isCharged());
            } else {
                Shooter.shoot(false);
                return false;
            }
        } else {
            AutonomousDashboard.setStep(1);
        }
        
		return true;
	}

    public void disable() {
        controlTimer.stop();
        
        Robot.pickupMotor.set(0D);
        Robot.hopperMotor.set(0D);
    }
    
    public String getName () {
        return "Shoot";
    }
}
