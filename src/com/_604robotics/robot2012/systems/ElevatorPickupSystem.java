package com._604robotics.robot2012.systems;

import com._604robotics.robotnik.coordinator.Coordinator;
import com._604robotics.robotnik.coordinator.connectors.DataWire;
import com._604robotics.robotnik.module.ModuleManager;
import com._604robotics.robotnik.prefabs.trigger.TriggerAnd;
import com._604robotics.robotnik.trigger.TriggerAccess;

public class ElevatorPickupSystem extends Coordinator {
    protected void apply (ModuleManager modules) {
        this.fill(new DataWire(modules.getModule("Elevator").getAction("Low"), "pickupDown", new TriggerAnd(new TriggerAccess[] {
            modules.getModule("Pickup").getTrigger("Travelling").not(), modules.getModule("Pickup").getAction("Deploy").active()
        })));
    }
}
