package com._604robotics.robot2012.control.modes.teleop;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.robot2012.ai.AveragingTutor;
import com._604robotics.robot2012.ai.Tutor;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.XboxController.Axis;
import com._604robotics.utils.XboxController.Button;

/**
 * TODO: Document?
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class LearningControlMode implements ControlMode {
    private final Tutor tutor = new AveragingTutor();
    
    public void init() {
        Shooter.setManual(true);
    }
    
    public boolean step() {
        double leftStickY = Robot.driveController.getAxis(Axis.LEFT_STICK_Y);
        double rightStickY = Robot.driveController.getAxis(Axis.RIGHT_STICK_Y);
    
        if (Math.abs(leftStickY) > 0.15) {
            Drive.drive(leftStickY);
            
            Target[] targets = Robot.cameraInterface.getTargets();
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
        }
        
        Shooter.setManualSpeed(tutor.shoot());
        Shooter.shoot(Robot.driveController.getButton(Button.B));
        Shooter.driveHopper(Shooter.isCharged());
        
        if (Robot.driveController.getButton(Button.Y)) {
            tutor.feedback(1);
            System.out.println("Too high!");
        } else if (Robot.driveController.getButton(Button.A)) {
            tutor.feedback(-1);
            System.out.println("Too low!");
        } else if (Robot.driveController.getButton(Button.X)) {
            tutor.feedback(0);
            System.out.println("Juuust right.");
        } else if (Robot.driveController.getToggle(Button.LT)) {
            tutor.record();
            System.out.println("Recorded.");
        } else if (Math.abs(rightStickY) > 0.2) {
            Shooter.driveHopper(rightStickY);
        }
        
        return true;
    }
    
    public void disable() {
        Shooter.setManual(false);
    }
    
    public String getName () {
        return "Learning";
    }
}
