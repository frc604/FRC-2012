package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ActuatorConfiguration;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class RingLightWorker implements Worker {
    public void work () {
        /*
         * Make sure the ring light stays on.
         */
        Robot.ringLight.set(ActuatorConfiguration.RING_LIGHT.ON);
    }
}
