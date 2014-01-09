package com._604robotics.robot2012.modules;

import com._604robotics.robotnik.action.Action;
import com._604robotics.robotnik.action.ActionData;
import com._604robotics.robotnik.action.controllers.StateController;
import com._604robotics.robotnik.module.Module;
import com._604robotics.robotnik.trigger.Trigger;
import com._604robotics.robotnik.trigger.TriggerMap;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;

public class Pickup extends Module {
    private final DoubleSolenoid solenoid = new DoubleSolenoid(4, 3);
    private final Timer timer = new Timer();
    
    public Pickup () {
        timer.start();
        
        this.set(new TriggerMap() {{
            add("Travelling", new Trigger() {
                public boolean run () {
                    return timer.get() < 2;
                }
            });
        }});
        
        this.set(new StateController () {{
            addDefault("Stow", new Action() {
                public void begin (ActionData data) {
                    timer.reset();
                    solenoid.set(Value.kReverse);
                }
            });
            
            add("Deploy", new Action() {
                public void begin (ActionData data) {
                    timer.reset();
                    solenoid.set(Value.kForward);
                }
            });
        }});
    }
}
