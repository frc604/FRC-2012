package com._604robotics.robot2012.vision;

public class DistanceCalculations {
	
	double	cameraPixelWidth	= 640;
	double	FOV					= 47;																// degrees
	double	kx					= 1 / (Math.tan(FOV / 2 * Math.PI / 180) / cameraPixelWidth * 2);
	double	ky					= kx;
	double	targetHeight		= 24;																// inches
	double	targetWidth			= 18;																// inches
																									
	/**
	 * @param quad - a quadrilateral with corners indicating the corners of the target
	 * @return a Target as an estimation of 
	 */
	public Target getAngleAndRelXYZOfTarget(Quad quad) {
		Point3d p = getRelXYZOfTarget(quad);
		double angle = getAngleOfTarget(quad, p.z);
		
		return new Target(p, angle);
	}
	
	
	/**
	 * This function gets the direction the target is facing, relative to the camera. It is imperfect, and half-assumes
	 * a simple orthographic projection (which is not quite like real life). If it causes issues (which the accuracy of
	 * this function doesn't need to be very high), we can fix it later.
	 * 
	 * @return the resulting angle in radians.
	 */
	public double getAngleOfTarget(Quad q, double z) {
		// based on width/angles/etc of trapezoid
		
		double dy1 = q.topLeft.y - q.bottomLeft.y;
		double dy2 = q.topRight.y - q.bottomRight.y;
		
		double dyRatio = dy1 / dy2;
		

		double expectedW = -kx * targetHeight / z;
		double actualW = (q.topRight.x + q.bottomRight.x - q.topLeft.x - q.bottomLeft.x) / 2;
		
		double wRatio = actualW / expectedW;
		
		if (wRatio < 0) {
			wRatio = 0;
		}
		
		if (wRatio > 1) {
			wRatio = 1;
		}
		System.out.println(wRatio);
		
		return Math.acos(wRatio) * (dyRatio > 1 ? -1 : 1);
	}
	
	/**
	 * 
	 * 
	 * Remember that this requires the camera to be "perfectly" flat, and the targets to be "perfectly" vertical. A new
	 * function will probably need to be created for use on the robot. That, or we'll need to manipulate the points
	 * based on camera angle.
	 * 
	 * The points are in the following pattern:
	 * 
	 * +y ^ | 1 2 | | 3 4 +------> +x
	 * 
	 * @return a Point3d holding the X, Y, and Z of the target, relative to the camera.
	 * 
	 */
	public Point3d getRelXYZOfTarget(Quad q) {
		double z = ky * 2 * targetWidth / (q.topLeft.y + q.topRight.y - q.bottomLeft.y - q.bottomRight.y);
		// xs = kx * x / z
		// x = xs * z / kx
		double avgx = (q.topLeft.x + q.topRight.x + q.bottomLeft.x + q.bottomRight.x) / 4;
		double avgy = (q.topLeft.y + q.topRight.y + q.bottomLeft.y + q.bottomRight.y) / 4;
		

		avgx -= 640 / 2.0;
		avgy = 480 / 2.0 - avgy;
		
		double x = -avgx * z / kx;
		double y = -avgy * z / ky;
		
		return new Point3d(x, y, z);
	}
}
