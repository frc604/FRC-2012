package com._604robotics.robot2012.controlModes;

import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.configuration.AutonomousConfiguration;
import com._604robotics.robot2012.machine.ElevatorMachine.ElevatorState;
import com._604robotics.robot2012.machine.PickupMachine.PickupState;
import com._604robotics.robot2012.machine.ShooterMachine.ShooterState;
import com._604robotics.utils.SmarterDashboard;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 *
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AutonControlMode extends ControlMode {
	int step = 1;
	Timer controlTimer;
	double drivePower;
	double gyroAngle;
	boolean turnedAround = false;
	
	public boolean step() {
        theRobot.encoderShooter.sample();

        if (step > SmarterDashboard.getDouble("Auton: Max Step", AutonomousConfiguration.MAX_STEP)) {
            SmartDashboard.putInt("STOPPED AT", step);
            theRobot.driveTrain.tankDrive(0D, 0D);

            return true;
        } else {
            SmartDashboard.putInt("STOPPED AT", -1);
        }

        SmartDashboard.putInt("CURRENT STEP", step);
        SmartDashboard.putDouble("CONTROL TIMER", controlTimer.get());
        
		switch (step) {
            case 1:
                /* Put the elevator up. */

                theRobot.driveTrain.tankDrive(0D, 0D);

                if (controlTimer.get() < AutonomousConfiguration.STEP_1_ELEVATOR_TIME) {
                    if (theRobot.elevatorMachine.crank(ElevatorState.HIGH)) {
                        controlTimer.reset();
                        step++;
                    }
                } else {
                    theRobot.elevatorMachine.crank(999);
                    controlTimer.reset();
                    step++;
                }

                break;
            case 2:
                /* Shoot! */

                theRobot.driveTrain.tankDrive(0D, 0D);
                theRobot.elevatorMachine.setHoodPosition(ActuatorConfiguration.SOLENOID_SHOOTER.LOWER_ANGLE);

                if (controlTimer.get() < AutonomousConfiguration.STEP_2_SHOOT_TIME)
                    theRobot.shooterMachine.crank(ShooterState.SHOOTING);
                else if (((String) theRobot.inTheMiddle.getSelected()).equals("Yes"))
                    step++;
                else
                    step = 6;

                break;
            case 3:
                /* 
                * Turn around and face the bridge, and put the elevator
                * down.
                */

                if (controlTimer.get() <= SmarterDashboard.getDouble("Auton: Step 3", AutonomousConfiguration.STEP_3_TURN_TIME)) {
                    theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
                    gyroAngle = theRobot.gyroHeading.getAngle();

                    if (turnedAround || (gyroAngle > 179 && gyroAngle < 181)) {
                        turnedAround = true;
                        theRobot.driveTrain.tankDrive(0D, 0D);
                    } else {
                        drivePower = Math.max(0.2, 1 - gyroAngle / 180);
                        theRobot.driveTrain.tankDrive(drivePower, drivePower * -1);
                    }
                } else {
                    theRobot.driveTrain.tankDrive(0D, 0D);

                    controlTimer.reset();
                    if(theRobot.elevatorMachine.crank(ElevatorState.MEDIUM))
                        step++;
                }

                break;
            case 4:
                /* Drive forward and stop, then smash down the bridge. */

                if (controlTimer.get() <= AutonomousConfiguration.STEP_4_DRIVE_TIME) {
                    SmartDashboard.putString("STAGE", "DRIVING");
                    drivePower = Math.min(-0.2, (1 - controlTimer.get() / SmarterDashboard.getDouble("Auton: Step 4", AutonomousConfiguration.STEP_4_DRIVE_TIME)) * -1);
                    SmartDashboard.putDouble("AUTON DRIVE POWER", drivePower);
                    theRobot.driveTrain.tankDrive(drivePower, drivePower);
                } else {
                    SmartDashboard.putString("STAGE", "SMASHING!");
                    theRobot.driveTrain.tankDrive(0D, 0D);
                    theRobot.pickupMachine.crank(PickupState.OUT);

                    controlTimer.reset();
                    step++;
                }

                break;
            case 5:
                /* Wait a bit. */

                theRobot.driveTrain.tankDrive(0D, 0D);

                if (controlTimer.get() >= SmarterDashboard.getDouble("Auton: Step 5", AutonomousConfiguration.STEP_5_WAIT_TIME)) 
                    step++;

                break;
            case 6:
                /* Pull in the pickup and put the elevator down. */

                if (theRobot.elevatorMachine.test(ElevatorState.PICKUP_OKAY)) {
                    if (theRobot.pickupMachine.crank(PickupState.IN))
                        theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
                } else {
                    theRobot.elevatorMachine.crank(ElevatorState.MEDIUM);
                }

                break;
		}
        
		return true;
	}

	public void init() {
		step = 1;
		turnedAround = false;
		
		controlTimer = new Timer();
		controlTimer.start();
	}

    public void disable() {
        
    }
}
