package com._604robotics.robot2012.modules;

import com._604robotics.robotnik.action.Action;
import com._604robotics.robotnik.action.ActionData;
import com._604robotics.robotnik.action.controllers.ElasticController;
import com._604robotics.robotnik.action.field.FieldMap;
import com._604robotics.robotnik.module.Module;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drive extends Module {
    private final RobotDrive drive = new RobotDrive(1, 9);
    
    public Drive () {
        this.set(new ElasticController() {{
            addDefault("Tank Drive", new Action(new FieldMap() {{
                define("left", 0D);
                define("right", 0D);
            }}) {
                public void run (ActionData data) {
                    drive.tankDrive(data.get("left"), data.get("right"));
                }

                public void end (ActionData data) {
                    drive.stopMotor();
                }
            });
            
            add("Arcade Drive", new Action(new FieldMap() {{
                define("move", 0D);
                define("rotate", 0D);
            }}) {
                public void run (ActionData data) {
                    drive.arcadeDrive(data.get("move"), data.get("rotate"));
                }

                public void end (ActionData data) {
                    drive.stopMotor();
                }
            });
        }});
    }
}
