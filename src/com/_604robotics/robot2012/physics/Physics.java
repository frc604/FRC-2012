package com._604robotics.robot2012.physics;

import com._604robotics.robot2012.points.Point2d;

/**
 * Used for determining launch velocities of the ball.
 * 
 * It gives velocity as a function of displacement and final vertical velocity
 * 
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Physics {
    /**
     * Returns an approximation of the power the shooter should be spun at
     * 
     * @param vel - velocity, in inches/second
     * @return the power to spin the shooter wheel at
     */
    public static double velToPow(double vel) {
        //inverse of [] would work, but following is easier
        double pow = 2.8344587e-6 *vel*vel +
                2.78953798e-4 *vel +
                .1479131112;
        
        if(pow < 0)
            return 0;
        if(pow > .4)
            return 1;
        
        return pow;
    }
    
    /**
     * This untested function might determine the firing velocity for a given
     * distance (horizontally, and vertically) and the angle of the shooter.
     *
     * @param   distH	Horizontal distance the ball must travel.
     * @param   distV   Vertical distance the ball must travel.
     * @param   slope   What slope the launcher is at.
     *
     * @return  The firing velocity
     */
    public static double getSubparFiringVelocity(double distH, double distV, double slope) {
        double g = -386;//inches per second squared

        return (Math.sqrt(2) * Math.sqrt(g * slope * slope + g)
                * Math.abs(distH) * Math.sqrt(distV - slope * distH))
                / (2 * distV - 2 * slope * distH);
    }

    /**
     * This function determines the firing velocities (and time) for a given
     * distance (horizontally, and vertically) and a vertical velocity at which
     * the ball should enter the hoop.
     *
     *
     * @param   distH           Horizontal distance the ball must travel.
     * @param   distV           Vertical distance the ball must travel.
     * @param   verticalVel	Velocity at which the ball should enter the
     *                          hoop.
     *
     * 
                SmartDashboard.putBoolean("PID Enabled", provider.());@return  A Point2d with the x and y velocities does not return the time.
     *
     */
    public static Point2d betterVersionOfgetFiringVelocity(double distH, double distV, double verticalVel) {

        double g = -386;//inches per second squared

        double verticalVel2 = verticalVel * verticalVel;

        double sqrtVal = Math.sqrt(verticalVel2 - 2 * g * distV);

        double t = (2 * distV) / (sqrtVal + verticalVel);
        double verticalVel0 = (verticalVel * sqrtVal - 2 * g * distV + verticalVel2) / (sqrtVal + verticalVel);
        double v_x = (distH * sqrtVal + verticalVel * distH) / (2 * distV);

        if (t <= 0) {
            t = -(2 * distV) / (sqrtVal - verticalVel);
            verticalVel0 = (verticalVel * sqrtVal + 2 * g * distV - verticalVel2) / (sqrtVal - verticalVel);
            v_x = -(distH * sqrtVal - verticalVel * distH) / (2 * distV);
        }

        //if(time)
        //	*time = t;
        return new Point2d(v_x, verticalVel0);
    }

    /**
     * This function guesses a good vertical velocity to enter the hoop, then
     * determines the firing velocities (and time) for a given distance
     * (horizontally, and vertically).
     *
     * @param   distH   Horizontal distance the ball must travel.
     * @param   distV   Vertical distance the ball must travel.
     *
     * @return  A Point2d with the x and y velocities does not return the time.
     */
    public static Point2d betterVersionOfgetFiringVelocity(double distH, double distV) {

        //TODO: this approximation of a "good velocity" will need to be tuned once we have a working robot
        double verticalVel = -120 - distV * .5;

        return betterVersionOfgetFiringVelocity(distH, distV, verticalVel);
    }

    /**
     * This function will determine how to fire the ball if the shooter only has
     * 2 vertical angles.
     *
     * @param   xDist       Left-right distance of the target.
     * @param   yDist       Vertical distance of the target.
     * @param   zDist       Depth distance of the target.
     * @param   robotVelX   Current velocity (x axis) of the robot.
     * @param   robotVelZ   Current velocity (z axis) of the robot
     *
     * @return  A BallFireInfo with the velocity, angle, and horizontalAngle to
     *          fire the ball at (eventually)
     */
    public static BallFireInfo GetBallFiringInfo(double xDist, double yDist, double zDist, double robotVelX, double robotVelZ) {
        //TODO - needz moar math

        return null;
    }
}