package com._604robotics.robot2012.control.modes.hybrid;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.robot2012.control.workers.AutoAimer;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;

/**
 *
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class KinectControlMode implements ControlMode {
    
    private boolean hadAimed = false;
    
	public void init() {
		System.out.println("KINECT ON");
        Shooter.setManual(false);
	}
    
	public boolean step() {
		if (Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT))
			return false;
		
		if (Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.DRIVE_ENABLED))
			Drive.drive(Robot.leftKinect.getRawAxis(2) * 0.8, Robot.rightKinect.getRawAxis(2) * 0.8);
		else
			Drive.drive(0D);
		
        if(Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.SHOOT) && Robot.elevatorMachine.test(ElevatorState.HIGH)) {
            if(!hadAimed || !AutoAimer.isOnTarget()) {
                Drive.autoAim(true);
                hadAimed = true;
                Shooter.shoot(false);
                Shooter.driveHopper(false);
            } else {
                Shooter.shoot();
                Shooter.driveHopper(Shooter.isCharged());
            }
        } else {
            Shooter.shoot(false);
            Shooter.driveHopper(false);
            
            hadAimed = false;
        }
        
        Elevator.go(Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.SHOOT));
        
        Pickup.flip(Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.PICKUP_IN));
        Pickup.suckIn(Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.SUCK));
		
		return true;
	}

    public void disable() {
        
    }
    
    public String getName () {
        return "Kinect";
    }
}
