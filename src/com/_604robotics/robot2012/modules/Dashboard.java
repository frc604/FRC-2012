package com._604robotics.robot2012.modules;

import com._604robotics.robotnik.module.Module;
import com._604robotics.robotnik.trigger.TriggerMap;
import com._604robotics.robotnik.trigger.sources.DashboardTriggerChoice;

public class Dashboard extends Module {
    public Dashboard () {
        final DashboardTriggerChoice driveMode = new DashboardTriggerChoice("Drive Mode");
        this.set(new TriggerMap() {{
            add("Tank Drive", driveMode.addDefault("Tank Drive"));
            add("Arcade Drive", driveMode.add("Arcade Drive"));
        }});
    }
}
