package com._604robotics.robot2012.control;

import com._604robotics.robot2012.learning.AveragingTutor;
import com._604robotics.robot2012.learning.Tutor;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.XboxController.Axis;
import com._604robotics.utils.XboxController.Button;

/**
 * TODO: Document?
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class LearningControlMode extends ControlMode {
    private final Tutor tutor = new AveragingTutor();
    
    public void init() {
        
    }
    
    public boolean step() {
        double leftStickY = theRobot.driveController.getAxis(Axis.LEFT_STICK_Y);
        double rightStickY = theRobot.driveController.getAxis(Axis.RIGHT_STICK_Y);
    
        if (Math.abs(leftStickY) > 0.2) {
            theRobot.driveTrain.tankDrive(leftStickY, leftStickY);
            
            Target[] targets = theRobot.cameraInterface.getTargets();
            Target target = null;
            
            for (int i = 0; i < targets.length; i++) {
                if (target == null || targets[i].getY() < target.getY())
                    target = targets[i];
            }
            
            if (target != null) {
                tutor.configure(target.getZ());
                System.out.println("Distance reconfigured to " + target.getZ() + ".");
            } else {
                System.out.println("WARNING: No target detected!");
            }
        } else {
            theRobot.speedProvider.reset();
        }
        
        if (theRobot.driveController.getButton(Button.B)) {
            theRobot.speedProvider.setSetSpeed(tutor.shoot());
            theRobot.speedProvider.apply();
            
            System.out.println("Shooting.");
        } else if (theRobot.driveController.getButton(Button.Y)) {
            tutor.feedback(1);
            System.out.println("Too high!");
        } else if (theRobot.driveController.getButton(Button.A)) {
            tutor.feedback(-1);
            System.out.println("Too low!");
        } else if (theRobot.driveController.getButton(Button.X)) {
            tutor.feedback(0);
            System.out.println("Juuust right.");
        } else if (theRobot.driveController.getToggle(Button.LT)) {
            tutor.record();
            System.out.println("Recorded.");
        } else if (Math.abs(rightStickY) > 0.2) {
            theRobot.hopperMotor.set(rightStickY);
        }
        
        theRobot.elevatorMotors.reload();
        theRobot.shooterMotors.reload();
        theRobot.hopperMotor.reload();
        theRobot.pickupMotor.reload();

        theRobot.ringLight.reload();

        theRobot.solenoidShifter.reload();
        theRobot.solenoidHopper.reload();
        
        return true;
    }
    
    public void disable() {
        
    }
}
