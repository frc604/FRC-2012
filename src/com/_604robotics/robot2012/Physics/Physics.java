package com._604robotics.robot2012.Physics;

import com._604robotics.robot2012.Aiming.*;

/**
 * 	Physics.java
 *
 * Used for determining launch velocities of the ball
 * 	It gives velocity as a function of displacement and final vertical velocity
 *
 */
public class Physics {
	/**
	 *	This untested function might determine the firing velocity for a given
	 *	distance (horizontally, and vertically) and the angle of the shooter.
	 *
	 *
	 *	double  distH	horizontal distance the ball must travel
	 *	double  distV	vertical distance the ball must travel
	 *	double  slope	what slope the launcher is at
	 *
	 *	@return the firing velocity
	 *
	 */
	double getCrappyFiringVelocity(double distH, double distV, double slope) {
		double g = -386;//inches per second squared

		return (Math.sqrt(2)*Math.sqrt(g*slope*slope+g)*
				Math.abs(distH)*Math.sqrt(distV-slope*distH))
				/(2*distV-2*slope*distH);
	}

	/**
	 *	This function determines the firing velocities (and time) for a given
	 *	distance (horizontally, and vertically) and a vertical velocity at which
	 *	the ball should enter the hoop.
	 *
	 *
	 *	double  distH		horizontal distance the ball must travel
	 *	double  distV		vertical distance the ball must travel
	 *	double  verticalVel	velocity at which the ball should enter the hoop
	 *
	 *	@return a Point2d with the x and y velocities
	 *		does not return the time.
	 *
	 */
	public Point2d betterVersionOfgetFiringVelocity(double distH, double distV, double verticalVel) {

		double g = -386;//inches per second squared

		double verticalVel2 = verticalVel*verticalVel;

		double sqrtVal = Math.sqrt(verticalVel2-2*g*distV);

		double t=(2*distV)/(sqrtVal+verticalVel);
		double verticalVel0=(verticalVel*sqrtVal-2*g*distV+verticalVel2)/(sqrtVal+verticalVel);
		double v_x=(distH*sqrtVal+verticalVel*distH)/(2*distV);

		if(t <= 0) {
			t=-(2*distV)/(sqrtVal-verticalVel);
			verticalVel0=(verticalVel*sqrtVal+2*g*distV-verticalVel2)/(sqrtVal-verticalVel);
			v_x=-(distH*sqrtVal-verticalVel*distH)/(2*distV);
		}

		//if(time)
		//	*time = t;
		return new Point2d(v_x, verticalVel0);
	}

	/**
	 *	This function guesses a good vertical velocity to enter the hoop, then
	 *	determines the firing velocities (and time) for a given
	 *	distance (horizontally, and vertically).
	 *
	 *
	 *	double  distH		horizontal distance the ball must travel
	 *	double  distV		vertical distance the ball must travel
	 *
	 *
	 *	@return a Point2d with the x and y velocities
	 *		does not return the time.
	 *
	 */
	Point2d betterVersionOfgetFiringVelocity(double distH, double distV) {

		//TODO: this approximation of a "good velocity" will need to be tuned once we have a working robot
		double verticalVel = -120 - distV*.5;

		return betterVersionOfgetFiringVelocity(distH, distV, verticalVel);
	}



	/**
	 * This function will determine how to fire the ball if the shooter only has 2 vertical angles.
	 *
	 * double*				ballVel		pointer to the returned ball velocity
	 * ShooterAnglePick*	anglePick	pointer to the selected angle (use ShooterAnglePickTop and ShooterAnglePickBottom)
	 * double*				horizAngle	pointer to the returned horizontal angle. (given in radians)
	 *
	 * double				xDist		left-right distance of the target
	 * double				yDist		vertical distance of the target
	 * double				zDist		depth distance of the target
	 * double				robotVelX	current velocity (x axis) of the robot
	 * double				robotVelZ	current velocity (z axis) of the robot
	 *
	 *	@return a BallFireInfo with the velocity, angle, and horizontalAngle to fire the ball at
	 *		(eventually)
	 */
	public BallFireInfo GetBallFiringInfo(double xDist, double yDist, double zDist, double robotVelX, double robotVelZ) {

		//TODO - needs moar math

		return null;
	}
}
