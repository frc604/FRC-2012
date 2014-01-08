package com._604robotics.robot2012;

import com._604robotics.robot2012.modes.AutonomousMode;
import com._604robotics.robot2012.modes.TeleopMode;
import com._604robotics.robot2012.modules.Regulator;
import com._604robotics.robot2012.modules.Shifter;
import com._604robotics.robotnik.Robot;
import com._604robotics.robotnik.coordinator.ModeMap;
import com._604robotics.robotnik.module.ModuleMap;

public class Robot2012 extends Robot {
    public Robot2012 () {
        this.set(new ModuleMap() {{
            add("Regulator", new Regulator());
            add("Shifter", new Shifter());
        }});
        
        this.set(new ModeMap() {{
            setAutonomousMode(new AutonomousMode());
            setTeleopMode(new TeleopMode());
        }});
    }
}
