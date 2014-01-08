package com._604robotics.robot2012.modules;

import com._604robotics.robotnik.action.Action;
import com._604robotics.robotnik.action.ActionData;
import com._604robotics.robotnik.action.controllers.StateController;
import com._604robotics.robotnik.action.field.FieldMap;
import com._604robotics.robotnik.data.Data;
import com._604robotics.robotnik.data.DataMap;
import com._604robotics.robotnik.module.Module;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;

public class Elevator extends Module {
    private final Victor motor = new Victor(3);
    private final Encoder encoder = new Encoder(1, 2);
    
    public Elevator () {
        encoder.start();
        
        this.set(new DataMap() {{
            add("Encoder Position", new Data() {
                public double run () {
                    return encoder.get();
                }
            });
        }});
        
        this.set(new StateController() {{
            addDefault("Idle", new Action() {
                public void run (ActionData data) {
                    motor.stopMotor();
                }
            });
            
            add("Low", new Action(new FieldMap() {{
                define("pickupDown", false);
            }}) {
                public void run (ActionData data) {
                    if (data.is("pickupDown"))
                        elevate(0D, 50D); // FIXME - Made-up values
                     else
                        elevate(60D, 100D); // FIXME - Made-up values
                }

                public void end (ActionData data) {
                    motor.stopMotor();
                }
            });
            
            add("High", new Action(new FieldMap() {{
                define("pickupDown", false);
            }}) {
                public void run (ActionData data) {
                    elevate(90D, 110D); // FIXME - Made-up values
                }

                public void end (ActionData data) {
                    motor.stopMotor();
                }
            });
        }});
    }
    
    private void elevate (double minimum, double maximum) {
        final double current = encoder.get();
        
        if (current < minimum)
            motor.set(0.5); // FIXME - Made-up value
        else if (current > maximum)
            motor.set(-0.5); // FIXME - Made-up value
        else
            motor.stopMotor();
    }
}
