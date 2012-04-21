package com._604robotics.robot2012.control.modes.hybrid;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.control.models.Elevator;
import com._604robotics.robot2012.control.models.Pickup;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.utils.SmarterDashboard;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AutonomousControlMode implements ControlMode {
    // TODO: Split this up into Shoot and Basket modes. As-is, it's broken.
    
	int step = 1;
	Timer controlTimer;
    
	double drivePower;
	double gyroAngle;
    
	boolean turnedAround = false;
    boolean done = false;

	public void init() {
		step = 1;
		turnedAround = false;
        
        Robot.gyroHeading.reset();
        Shooter.hoodUp();
		
		controlTimer = new Timer();
		controlTimer.start();
	}
	
	public boolean step() {
        if (Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT))
            return false;
        
        if (step > SmarterDashboard.getDouble("Auton: Max Step", AutonomousConfiguration.MAX_STEP)) {
            SmartDashboard.putInt("STOPPED AT", step);
            
            Drive.drive(0D);
            
            Pickup.flipDown();
            Elevator.goUp();
            
            return false;
        } else {
            SmartDashboard.putInt("STOPPED AT", -1);
        }

        SmartDashboard.putInt("CURRENT STEP", step);
        SmartDashboard.putDouble("CONTROL TIMER", controlTimer.get());
        
		switch (step) {
            case 1:
                /* Put the elevator up. */
                
                Drive.drive(0D);
                Elevator.goUp();

                if (Robot.elevatorMachine.test(ElevatorState.HIGH) || controlTimer.get() < AutonomousConfiguration.STEP_1_ELEVATOR_TIME) {
                    controlTimer.reset();
                    step++;
                }

                break;
            case 2:
                /* Shoot! */

                if (controlTimer.get() < AutonomousConfiguration.STEP_2_SHOOT_TIME)
                    Robot.shooterMachine.crank(ShooterState.SHOOTING);
                else if (((String) Robot.inTheMiddle.getSelected()).equals("Yes"))
                    step++;
                else
                    return false;

                break;
            case 3:
                /* 
                 * Turn around and face the bridge, and put the elevator
                 * down.
                 */

                Elevator.goDown();
                Shooter.hoodDown();
                    
                if (controlTimer.get() <= SmarterDashboard.getDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_TURN_TIME)) {
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
                    SmartDashboard.putString("STAGE", "DRIVING");
                    
                    drivePower = Math.min(-0.2, (1 - controlTimer.get() / SmarterDashboard.getDouble("Auton: Step 4", AutonomousConfiguration.STEP_4_DRIVE_TIME)) * -1);
                    SmartDashboard.putDouble("AUTON DRIVE POWER", drivePower);
                    Drive.drive(drivePower);
                } else {
                    SmartDashboard.putString("STAGE", "SMASHING, OLD CHAP!");
                    
                    Drive.drive(0D);
                    Pickup.flipDown();

                    controlTimer.reset();
                    step++;
                }

                break;
            case 5:
                /* Wait a bit. */

                if (controlTimer.get() >= SmarterDashboard.getDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_WAIT_TIME)) 
                    return false;

                break;
		}
        
		return true;
	}

    public void disable() {
        Robot.pickupMotor.set(0D);
        Robot.hopperMotor.set(0D);
    }
    
    public String getName () {
        return "Autonomous";
    }
}
