package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;
import com._604robotics.robot2012.control.models.Stinger;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class StingerWorker implements Worker {
    public void work () {
        Robot.solenoidStinger.set(
                (Stinger.down)
                    ? ActuatorConfiguration.SOLENOID_STINGER.DOWN
                    : ActuatorConfiguration.SOLENOID_STINGER.UP
        );
    }
}
