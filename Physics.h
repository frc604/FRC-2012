/*	Physics.h
 *
 * Used for determining launch velocities of the ball
 * 	It gives velocity as a function of displacement and final vertical velocity
 *
 */

/*
 *	This untested function might determine the firing velocity for a given
 *	distance (horizontally, and vertically) and the angle of the shooter.
 *
 *
 *	float  distH	horizontal distance the ball must travel
 *	float  distV	vertical distance the ball must travel
 *	float  slope	what slope the launcher is at
 *
 *	returns the firing velocity
 *
 */
float getCrappyFiringVelocity(float distH, float distV, float slope) {
	float g = -386;//inches per second squared

	return (sqrt(2)*sqrt(g*slope*slope+g)*abs(x)*sqrt(y-slope*x))/(2*y-2*slope*x);
}

/*
 *	This function determines the firing velocities (and time) for a given
 *	distance (horizontally, and vertically) and a vertical velocity at which
 *	the ball should enter the hoop.
 *
 *
 *	float *velX			pointer to the returned horizontal velocity
 *	float *velY			pointer to the returned vertical velocity
 *	float *time			pointer to the returned amount of time the ball flies
 *	float  distH		horizontal distance the ball must travel
 *	float  distV		vertical distance the ball must travel
 *	float  verticalVel	velocity at which the ball should enter the hoop
 *
 */
void BetterVersionOfgetFiringVelocity(float *velX, float *velY, float *time, float distH, float distV, float verticalVel) {

	float g = -386;//inches per second squared

	float verticalVel2 = verticalVel*verticalVel;

	float sqrtVal = sqrt(verticalVel2-2*g*y);

	float t=(2*y)/(sqrtVal+verticalVel);
	float verticalVel0=(verticalVel*sqrtVal-2*g*y+verticalVel2)/(sqrtVal+verticalVel);
	float v_x=(x*sqrtVal+verticalVel*x)/(2*y);

	if(t <= 0) {
		t=-(2*y)/(sqrtVal-verticalVel);
		verticalVel0=(verticalVel*sqrtVal+2*g*y-verticalVel2)/(sqrtVal-verticalVel);
		v_x=-(x*sqrtVal-verticalVel*x)/(2*y);
	}

	if(time)
		*time = t;
	if(velY)
		*velY = verticalVel0;
	if(velX)
		*velX = v_x;
}

/*
 *	This function guesses a good vertical velocity to enter the hoop, then
 *	determines the firing velocities (and time) for a given
 *	distance (horizontally, and vertically).
 *
 *
 *	float *velX			pointer to the returned horizontal velocity
 *	float *velY			pointer to the returned vertical velocity
 *	float *time			pointer to the returned amount of time the ball flies
 *	float  distH		horizontal distance the ball must travel
 *	float  distV		vertical distance the ball must travel
 *
 */
void BetterVersionOfgetFiringVelocity(float *velX, float *velY, float *time, float distH, float distV) {

		//TODO: this approximation of a "good velocity" will need to be tuned once we have a working robot
	float verticalVel = -120 - distV*.5;

	getFiringVelocity(velX, velY, time, distH, distV, verticalVel);
}



typedef bool ShooterAnglePick;
#define ShooterAnglePickTop true;
#define ShooterAnglePickBottom false;

/*
 * This function will determine how to fire the ball if the shooter only has 2 vertical angles.
 *
 * float*				ballVel		pointer to the returned ball velocity
 * ShooterAnglePick*	anglePick	pointer to the selected angle (use ShooterAnglePickTop and ShooterAnglePickBottom)
 * float*				horizAngle	pointer to the returned horizontal angle. (given in radians)
 *
 * float				xDist		left-right distance of the target
 * float				yDist		vertical distance of the target
 * float				zDist		depth distance of the target
 * float				robotVelX	current velocity (x axis) of the robot
 * float				robotVelZ	current velocity (z axis) of the robot
 *
 *
 */
void GetBallFiringInfo(float* ballVel, ShooterAnglePick* anglePick, float* horizAngle,
						float xDist, float yDist, float zDist, float robotVelX, float robotVelZ) {

	//TODO - needs moar math

}
