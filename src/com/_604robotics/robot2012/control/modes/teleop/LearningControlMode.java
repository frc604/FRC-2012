package com._604robotics.robot2012.control.modes.teleop;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.control.models.Drive;
import com._604robotics.robot2012.control.models.Shooter;
import com._604robotics.robot2012.control.modes.ControlMode;
import com._604robotics.robot2012.ai.AveragingTutor;
import com._604robotics.robot2012.ai.Tutor;
import com._604robotics.robot2012.ai.Tutor.Bounds;
import com._604robotics.robot2012.vision.Target;
import com._604robotics.utils.XboxController.Axis;
import com._604robotics.utils.XboxController.Button;
import java.util.Vector;

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
            
            Target target = Robot.cameraInterface.getSingleTarget();
            
            if (target != null) {
                tutor.configure(target.getZ());
                System.out.println("Distance reconfigured to " + target.getZ() + ".");
            } else {
                tutor.configure(tutor.getDistance() + 1);
                System.out.println("WARNING: No target detected! Setting \"distance\" to " + tutor.getDistance() + ".");
            }
        }
        
        Shooter.setManualSpeed(tutor.shoot());
        Shooter.shoot(Robot.driveController.getButton(Button.B));
        Shooter.driveHopper(Shooter.isCharged());
        
        if (Robot.driveController.getToggle(Button.Y)) {
            tutor.feedback(1);
            System.out.println("Too high!");
        } else if (Robot.driveController.getToggle(Button.A)) {
            tutor.feedback(-1);
            System.out.println("Too low!");
        } else if (Robot.driveController.getToggle(Button.X)) {
            tutor.feedback(0);
            System.out.println("Juuust right.");
        } else if (Robot.driveController.getToggle(Button.LT)) {
            tutor.record();
            System.out.println("Recorded.");
        } else if (Robot.driveController.getToggle(Button.Back)) {
            Vector data = tutor.getData();
            Bounds bounds;
            System.out.println("-----");
            for (int i = 0; i < data.size(); i++) {
                bounds = ((Bounds) data.elementAt(i));
                System.out.println(bounds.distance + ": " + bounds.min + " - " + bounds.max);
            }
            System.out.println("=====");
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
