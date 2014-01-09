package com._604robotics.robot2012.modules;

import com._604robotics.robotnik.action.Action;
import com._604robotics.robotnik.action.controllers.ElasticController;
import com._604robotics.robotnik.module.Module;
import edu.wpi.first.wpilibj.Victor;

public class Feeder extends Module {
    private final Victor motor = new Victor(4);
    
    public Feeder () {
        this.set(new ElasticController() {{
            addDefault("Off", new Action () {
                public void run () {
                    motor.stopMotor();
                }
            });
            
            add("Suck", new Action () {
                public void run () {
                    motor.set(-0.8);
                }
            });
            
            add("Spit", new Action () {
                public void run () {
                    motor.set(0.8);
                }
            });
        }});
    }
}
