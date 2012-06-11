package com._604robotics.robot2012.control.modes.hybrid;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.configuration.ButtonConfiguration;
import com._604robotics.robot2012.control.modes.ControlMode;

/**
 *
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class WaitingControlMode implements ControlMode {
    public void init() {

    }

    public boolean step() {
        System.out.println("Waiting!");
        try {
            Thread.sleep(20);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return !Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ABORT) && !Robot.leftKinect.getRawButton(ButtonConfiguration.Kinect.ENABLE);
    }

    public void disable() {
        
    }
    
    public String getName () {
        return "Waiting for Kinect";
    }
}
