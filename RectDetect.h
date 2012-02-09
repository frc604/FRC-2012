/*
 * RectDetect.h
 *
 * Used for detecting rectangles
 *
 */

#ifndef RECTDETECT_H_
#define RECTDETECT_H_


#define kx 0
#define ky 0
#define heightOftarget 18 //inches
#define widthOftarget 24 //inches

void getSpacialRelationshipToTarget(float* dist, float* angle, float* angleOfTarget, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
	//TODO - fill in stuff
}

void getRelXYZOfTarget(float* x, float* y, float* z, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
	z = kx * 2 * heightOfTarget / (y1 + y2 - y3 - y4);
	//xs = kx * x / z
	//x = xs * z / kx
	float avgx = (x1 + x2 + x3 + x4)/4;
	float avgy = (y1 + y2 + y3 + y4)/4;

	x = avgx * z / kx;
	y = avgy * z / ky;
}


#endif /* RECTDETECT_H_ */
