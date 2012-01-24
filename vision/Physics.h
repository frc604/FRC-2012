
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
void getFiringVelocity(float *velX, float *velY, float *time, float distH, float distV, float verticalVel) {

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