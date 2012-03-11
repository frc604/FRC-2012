package com._604robotics.robot2012.vision;

import static java.lang.Math.*;

public class DistanceCalculations {
	
	
	public static void main(String[] args) {
		//about 46 in
		//Quad [topLeft=Point2d [x=194.8762734332836, y=230.03676794085487], topRight=Point2d [x=555.0300833785902, y=249.79778560385398],
		//  bottomLeft=Point2d [x=215.42447837542025, y=434.28831770391776], bottomRight=Point2d [x=533.6907841935421, y=443.64852472363634]]
		Quad q = new Quad(new Point2d(194.9, 230), new Point2d(555, 249.8), new Point2d(215.4, 434.3), new Point2d(553.7, 443.6));
		
		DistanceCalculations dc = new DistanceCalculations();
		
		System.out.println(dc.getAngleAndRelXYZOfTarget(q));
	}

	double	cameraPixelWidth	= 640;
	double	cameraPixelHeight	= 480;
	
	//320 / (tan(47 / 2 degrees));
	double	FOV					= 47;	// degrees
	double	kx					= 700;	//was 736, then 605; then excel said 695-705
	double	ky					= 530;	//was 736, then 605; then excel said 514
	double	targetHeight		= 24;	// inches
	double	targetWidth			= 18;	// inches
	
	double rotUpDown = Math.toRadians(-28.5);//-26
																									
	/**
	 * @param quad - a quadrilateral with corners indicating the corners of the target
	 * @return a Target as an estimation of 
	 */
	public Target getAngleAndRelXYZOfTarget(Quad quad) {

		Quad qTrans = new Quad(null, null, null, null);
		Quad q2 = new Quad(null, null, null, null);
		
		//transform the quad based on vertical orientation

		qTrans.bottomLeft = transformPoint(quad.bottomLeft);
		qTrans.bottomRight = transformPoint(quad.bottomRight);
		qTrans.topLeft = transformPoint(quad.topLeft);
		qTrans.topRight = transformPoint(quad.topRight);

		q2.bottomLeft = transform2Point(quad.bottomLeft);
		q2.bottomRight = transform2Point(quad.bottomRight);
		q2.topLeft = transform2Point(quad.topLeft);
		q2.topRight = transform2Point(quad.topRight);

		System.out.println(qTrans);
		System.out.println(q2);
		
		Point3d p = getRelXYZOfTarget(qTrans);
		double angle = getAngleOfTarget(qTrans, p.z);
		
		return new Target(p, angle);
	}
	
	private Point2d transform2Point(Point2d p) {
		double	x = (p.x-cameraPixelWidth/2) / kx,
		y = (cameraPixelHeight/2-p.y) / ky;
		
		return new Point2d(x, y);
	}
	
	private Point2d transformPoint(Point2d p) {
		double	x = (p.x-cameraPixelWidth/2) / kx,
				y = (cameraPixelHeight/2-p.y) / ky;

		double cos = cos(rotUpDown);
		double sin = sin(rotUpDown);

		double nX = x/(cos - y*sin);
		double nY = (y*cos + sin)/(cos - y*sin);
		
		return new Point2d(nX, nY);
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
		

		double expectedW = targetHeight / z;
		double actualW = (q.topRight.x + q.bottomRight.x - q.topLeft.x - q.bottomLeft.x) / 2;
		
		double wRatio = actualW / expectedW;
		
		//System.out.println(expected)
		
		if (wRatio < 0) {
			wRatio = 0;
		}
		
		if (wRatio > 1) {
			wRatio = 1;
		}
		if(VisionProcessing.defaultProcessing.conf.debug_Print)
			System.out.println(wRatio);
		
		return Math.acos(wRatio) * (dyRatio > 1 ? -1 : 1);
	}
	
	/*
	public double[] calculateZ(Quad q) {
		//using target height
		double z1 = ky * 2 * targetWidth / (q.topLeft.y + q.topRight.y - q.bottomLeft.y - q.bottomRight.y);
		
	}
	*/
	
	/**
	 * 
	 * 
	 * Remember that this requires the camera to be "perfectly" flat, and the targets to be "perfectly" vertical. A new
	 * function will probably need to be created for use on the robot. That, or we'll need to manipulate the points
	 * based on camera angle.
	 * 
	 * 
	 * @return a Point3d holding the X, Y, and Z of the target, relative to the camera.
	 * 
	 */
	public Point3d getRelXYZOfTarget(Quad q) {
		double z = 2 * targetHeight / (q.topLeft.y + q.topRight.y - q.bottomLeft.y - q.bottomRight.y);
		// xs = kx * x / z
		// x = xs * z / kx
		double avgx = (q.topLeft.x + q.topRight.x + q.bottomLeft.x + q.bottomRight.x) / 4;
		double avgy = (q.topLeft.y + q.topRight.y + q.bottomLeft.y + q.bottomRight.y) / 4;
		
		
		double x = avgx * z;
		double y = avgy * z;
		
		return new Point3d(x, y, z);
	}
}
