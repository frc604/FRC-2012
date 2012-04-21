package com._604robotics.robot2012.control.workers;

import com._604robotics.robot2012.Robot;
import com._604robotics.robot2012.speedcontrol.AwesomeSpeedController;
import com._604robotics.utils.SmarterDashboard;
import com._604robotics.utils.UpDownPIDController;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class ConfigWorker implements Worker {
    // TODO: Scrape out this junk.
    public static final AwesomeSpeedController ctrl = 
            (Robot.speedProvider instanceof AwesomeSpeedController)
                ? ((AwesomeSpeedController) Robot.speedProvider)
                : null;
    
    public void work () {
        /*
         * Update speed controller inputs.
         */
        if (ctrl != null) {
            ctrl.setPIDDP(SmarterDashboard.getDouble("Shooter P", ctrl.getP()), SmarterDashboard.getDouble("Shooter I", ctrl.getI()), SmarterDashboard.getDouble("Shooter D", ctrl.getD()), SmarterDashboard.getDouble("Shooter DP", ctrl.getDP()));
            ctrl.fac = SmarterDashboard.getDouble("Shooter fac", ctrl.fac);
            ctrl.maxSpeed = SmarterDashboard.getDouble("Shooter maxSpeed", ctrl.maxSpeed);
        }
        
        /*
         * Update elevator PID gains.
         */
        Robot.pidElevator.setUpGains(new UpDownPIDController.Gains(SmarterDashboard.getDouble("Elevator Up P", 0.0085), SmarterDashboard.getDouble("Elevator Up I", 0D), SmarterDashboard.getDouble("Elevator Up D", 0.018)));
        Robot.pidElevator.setDownGains(new UpDownPIDController.Gains(SmarterDashboard.getDouble("Elevator Down P", 0.0029), SmarterDashboard.getDouble("Elevator Down I", 0.000003), SmarterDashboard.getDouble("Elevator Down P", 0.007)));
    }
}
