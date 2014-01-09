package com._604robotics.robot2012.modes;

import com._604robotics.robotnik.coordinator.Coordinator;
import com._604robotics.robotnik.coordinator.connectors.Binding;
import com._604robotics.robotnik.coordinator.connectors.DataWire;
import com._604robotics.robotnik.module.ModuleManager;
import com._604robotics.robotnik.prefabs.controller.joystick.JoystickController;
import com._604robotics.robotnik.prefabs.trigger.TriggerOr;
import com._604robotics.robotnik.prefabs.trigger.TriggerToggle;
import com._604robotics.robotnik.trigger.TriggerAccess;

public class TeleopMode extends Coordinator {
    private final JoystickController leftDriveStick = new JoystickController(1);
    private final JoystickController rightDriveStick = new JoystickController(2);
    private final JoystickController manipulator = new JoystickController(3);
    
    public TeleopMode () {
        leftDriveStick.axisY.setFactor(-1);
        rightDriveStick.axisY.setFactor(-1);
        manipulator.axisY.setFactor(-1);
        
        leftDriveStick.axisX.setDeadband(0.2);
        leftDriveStick.axisY.setDeadband(0.2);
        rightDriveStick.axisY.setDeadband(0.2);
        manipulator.axisY.setDeadband(0.2);
    }
    
    protected void apply(ModuleManager modules) {
        /* Driver */
        {
            /* Driving */
            {
                this.bind(new Binding(modules.getModule("Drive").getAction("Tank Drive"), modules.getModule("Dashboard").getTrigger("Tank Drive")));
                this.fill(new DataWire(modules.getModule("Drive").getAction("Tank Drive"), "left", leftDriveStick.axisY));
                this.fill(new DataWire(modules.getModule("Drive").getAction("Tank Drive"), "right", rightDriveStick.axisY));
                
                this.bind(new Binding(modules.getModule("Drive").getAction("Arcade Drive"), modules.getModule("Dashboard").getTrigger("Arcade Drive")));
                this.fill(new DataWire(modules.getModule("Drive").getAction("Arcade Drive"), "left", leftDriveStick.axisX));
                this.fill(new DataWire(modules.getModule("Drive").getAction("Arcade Drive"), "right", rightDriveStick.axisY));
            }
            
            /* Shifting */
            {
                final TriggerToggle toggle = new TriggerToggle(new TriggerOr(new TriggerAccess[] {
                    leftDriveStick.buttons.Button1, rightDriveStick.buttons.Button1
                }), false);
                this.bind(new Binding(modules.getModule("Shifter").getAction("Low Gear"), toggle.off));
                this.bind(new Binding(modules.getModule("Shifter").getAction("High Gear"), toggle.on));
            }
            
            /* Pickup */
            {
                final TriggerToggle toggle = new TriggerToggle(new TriggerOr(new TriggerAccess[] {
                    leftDriveStick.buttons.Button4, rightDriveStick.buttons.Button4,
                    leftDriveStick.buttons.Button5, rightDriveStick.buttons.Button5
                }), false);
                this.bind(new Binding(modules.getModule("Pickup").getAction("Stow"), toggle.off));
                this.bind(new Binding(modules.getModule("Pickup").getAction("Deploy"), toggle.on));
            }
            
            /* Feeder */
            {
                this.bind(new Binding(modules.getModule("Feeder").getAction("Suck"), new TriggerOr(new TriggerAccess[] {
                    leftDriveStick.buttons.Button3, rightDriveStick.buttons.Button3
                })));
                this.bind(new Binding(modules.getModule("Feeder").getAction("Spit"), new TriggerOr(new TriggerAccess[] {
                    leftDriveStick.buttons.Button2, rightDriveStick.buttons.Button2
                })));
            }
        }
        
        /* Manipulator */
        {
            /* Elevator */
            {
                this.bind(new Binding(modules.getModule("Elevator").getAction("Low"), manipulator.buttons.Button2));
                this.bind(new Binding(modules.getModule("Elevator").getAction("High"), manipulator.buttons.Button3));
            }
            
            /* Shooter */
            {
                this.fill(new DataWire(modules.getModule("Shooter").getAction("Shoot"), "power", manipulator.axisY));
            }
            
            /* Hopper */
            {
                this.bind(new Binding(modules.getModule("Hopper").getAction("Up"), manipulator.buttons.Button1));
                this.bind(new Binding(modules.getModule("Hopper").getAction("Down"), new TriggerOr(new TriggerAccess[] {
                    manipulator.buttons.Button4, manipulator.buttons.Button5
                })));
            }
        }
    }
}
