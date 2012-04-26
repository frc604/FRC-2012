package com._604robotics.robot2012;

import com._604robotics.utils.Controller;
import com._604robotics.utils.XboxController;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class JoystickController extends Controller {
    /**
     * Initialize a new XboxController on the specified port.
     * 
     * @param   port    The USB port the controller is connected to.
     */
    public JoystickController (int port) {
        super(port);
    }
    
    /**
     * Initialize a new XboxController from the underlying Joystick.
     * 
     * @param   joystick    The Joystick to overlay the XboxController interface
     *                      on.
     */
    public JoystickController (Joystick joystick) {
        super(joystick);
    }
    
    public double getAxis (int axis) {
        if (axis == XboxController.Axis.LEFT_STICK_Y) {
            if (this.joystick.getRawButton(3))
                return 1D;
            else if (this.joystick.getRawButton(2))
                return -1D;
            else
                return 0D;
        } else if (axis == XboxController.Axis.RIGHT_STICK_Y) {
            if (this.joystick.getRawButton(11))
                return 1D;
            else if (this.joystick.getRawButton(10))
                return -1D;
            else
                return 0D;
        } else {
            return 0D;
        }
    }
}
