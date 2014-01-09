package com._604robotics.robot2012.modules;

import com._604robotics.robotnik.action.Action;
import com._604robotics.robotnik.action.ActionData;
import com._604robotics.robotnik.action.controllers.ElasticController;
import com._604robotics.robotnik.action.field.FieldMap;
import com._604robotics.robotnik.module.Module;
import com._604robotics.robotnik.prefabs.devices.MultiOutput;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Victor;

public class Shooter extends Module {
    private final MultiOutput motor = new MultiOutput(new PIDOutput[] {
        new Victor(2), new Victor(3)
    });
    
    public Shooter () {
        this.set(new ElasticController() {{
            addDefault("Shoot", new Action(new FieldMap() {{
                define("power", 0D);
            }}) {
                public void run (ActionData data) {
                    motor.set(data.get("power"));
                }
                
                public void end (ActionData data) {
                    motor.set(0D);
                }
            });
        }});
    }
}
