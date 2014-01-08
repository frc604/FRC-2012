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
        
        leftDriveStick.axisY.setDeadband(0.2);
        rightDriveStick.axisY.setDeadband(0.2);
        manipulator.axisY.setDeadband(0.2);
    }
    
    protected void apply(ModuleManager modules) {
        /* Driver */
        {
            /* Driving */
            {
                this.fill(new DataWire(modules.getModule("Drive").getAction("Tank Drive"), "left", leftDriveStick.axisY));
                this.fill(new DataWire(modules.getModule("Drive").getAction("Tank Drive"), "right", rightDriveStick.axisY));
            }
            
            /* Shifting */
            {
                final TriggerToggle toggle = new TriggerToggle(new TriggerOr(new TriggerAccess[] {
                    leftDriveStick.buttons.Button1, rightDriveStick.buttons.Button1
                }), false);
                this.bind(new Binding(modules.getModule("Shifter").getAction("Low Gear"), toggle.off));
                this.bind(new Binding(modules.getModule("Shifter").getAction("High Gear"), toggle.on));
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
        }
    }
}
