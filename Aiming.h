#include <math.h>

/* 
 * TODO: Actually put something here.
 * 
 * What should go here?
 * ====================
 *  - Distance calculation code.
 *  - Angle calculation code.
 *  - Aiming code.
 * 
 */









//===== Kevin's simple versions: =====

const float FOV = 47;//degrees
const float pixelsWide = 640;
const float kx = tan(FOV*PI / 180) / pixelsWide * 2;
const float ky = kx;
#define heightOftarget 18 //inches
#define widthOftarget 24 //inches


void getSpacialRelationshipToTarget(float* dist, float* angle, float* angleOfTarget, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
	//TODO - fill in stuff
}

/*
 * returns the X, Y, and Z of the target, relative to the camera.
 *
 * Remember that this requires the camera to be "perfectly" flat, and the targets to be "perfectly" vertical.
 * A new function will probably need to be created for use on the robot. That, or we'll need to manipulate the points based on camera angle.
 *
 * The points are in the following pattern:
 *
 * +y
 * ^
 * | 1  2
 * |
 * | 3  4
 * +------> +x
 */
void getRelXYZOfTarget(float* x, float* y, float* z, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
	(*z) = ky * 2 * heightOftarget / (y1 + y2 - y3 - y4);
		//xs = kx * x / z
		//x = xs * z / kx
	float avgx = (x1 + x2 + x3 + x4)/4;
	float avgy = (y1 + y2 + y3 + y4)/4;

	(*x) = avgx * (*z) / kx;
	(*y) = avgy * (*z) / ky;
}

/*
 * This function gets the direction the target is facing, relative to the camera.
 * It is imperfect, and half-assumes a simple orthographic projection (which is not quite like real life).
 * If it causes issues (which the accuracy of this function doesn't need to be very high), we can fix it later.
 *
 * this returns the resulting angle in radians.
 */
float getAngleOfTarget(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float z) {
	//based on width/angles/etc of trapezoid

	float dy1 = y1 - y3;
	float dy2 = y2 - y4;

	float dyRatio = dy1/dy2;


	float expectedW = kx * widthOftarget / z;
	float actualW = (x1 + x2 - x3 - x4)/2;

	float wRatio = actualW/expectedW;

	if(wRatio < 0)
		wRatio = 0;

	if(wRatio > 1)
		wRatio = 1;

	return acos(wRatio);
}

void getAngleAndRelXYZOfTarget(float* angle, float* x, float* y, float* z, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
	getRelXYZOfTarget(x, y, z, x1, y1, x2, y2, x3, y3, x4, y4);
	(*angle) = getAngleOfTarget(x1, y1, x2, y2, x3, y3, x4, y4, (*z));
}


