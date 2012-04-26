package com._604robotics.robot2012.control.modes.hybrid;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
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
public class BridgeControlMode implements ControlMode {
	int step = 3;
	Timer controlTimer;
    
	double drivePower;
	double gyroAngle;
    
	boolean turnedAround = false;

	public void init() {
		step = 3;
		turnedAround = false;
        
        Robot.gyroHeading.reset();
        Shooter.hoodUp();
		
		controlTimer = new Timer();
		controlTimer.start();
	}
	
	public boolean step() {
        if (Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT))
            return false;
        
        if (step > AutonomousDashboard.maxStep) {
            AutonomousDashboard.setDone(true);
            
            Drive.drive(0D);
            
            Pickup.flipDown();
            Elevator.goUp();
            
            return false;
        } else {
            AutonomousDashboard.setStep(step);
            AutonomousDashboard.setDone(false);
        }
        
		switch (step) {
            case 3:
                /* 
                 * Turn around and face the bridge.
                 */

                Shooter.hoodDown();
                    
                if (controlTimer.get() <= AutonomousDashboard.step3) {
                    gyroAngle = Robot.gyroHeading.getAngle();

                    if (turnedAround || (gyroAngle > 179 && gyroAngle < 181)) {
                        turnedAround = true;
                    } else {
                        drivePower = Math.max(0.2, 1 - gyroAngle / 180);
                        Drive.drive(drivePower, drivePower * -1);
                    }
                } else {
                    controlTimer.reset();
                    if(Robot.elevatorMachine.test(ElevatorState.MEDIUM))
                        step++;
                }

                break;
            case 4:
                /* Drive forward and stop, then smash down the bridge. */

                if (controlTimer.get() <= AutonomousConfiguration.STEP_4_DRIVE_TIME) {
                    drivePower = Math.min(-0.2, (1 - controlTimer.get() / AutonomousDashboard.step4) * -1);
                    Drive.drive(drivePower);
                } else {
                    Drive.drive(0D);
                    Pickup.flipDown();

                    controlTimer.reset();
                    step++;
                }

                break;
            case 5:
                /* Wait a bit. */

                if (controlTimer.get() >= AutonomousDashboard.step5) {
                    AutonomousDashboard.setDone(true);
                    return false;
                }

                break;
		}
        
		return true;
	}

    public void disable() {
        Robot.pickupMotor.set(0D);
        Robot.hopperMotor.set(0D);
    }
    
    public String getName () {
        return "Bridge";
    }
}
