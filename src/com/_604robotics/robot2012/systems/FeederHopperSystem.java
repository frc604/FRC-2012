package com._604robotics.robot2012.systems;

import com._604robotics.robotnik.coordinator.Coordinator;
import com._604robotics.robotnik.coordinator.connectors.Binding;
import com._604robotics.robotnik.module.ModuleManager;

public class FeederHopperSystem extends Coordinator {
    protected void apply (ModuleManager modules) {
        this.bind(new Binding(modules.getModule("Hopper").getAction("Up"), modules.getModule("Feeder").getAction("Suck").active(), true));
        this.bind(new Binding(modules.getModule("Hopper").getAction("Down"), modules.getModule("Feeder").getAction("Spit").active(), true));
    }
}
