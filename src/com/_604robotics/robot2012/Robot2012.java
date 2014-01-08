package com._604robotics.robot2012;

import com._604robotics.robot2012.modes.TeleopMode;
import com._604robotics.robot2012.modules.Drive;
import com._604robotics.robot2012.modules.Regulator;
import com._604robotics.robot2012.modules.Shifter;
import com._604robotics.robot2012.modules.Shooter;
import com._604robotics.robotnik.Robot;
import com._604robotics.robotnik.coordinator.ModeMap;
import com._604robotics.robotnik.module.ModuleMap;

public class Robot2012 extends Robot {
    public Robot2012 () {
        this.set(new ModuleMap() {{
            add("Drive", new Drive());
            add("Regulator", new Regulator());
            add("Shifter", new Shifter());
            add("Elevator", new Elevator());
            add("Shooter", new Shooter());
        }});
        
        this.set(new ModeMap() {{
            setTeleopMode(new TeleopMode());
        }});
    }
}
