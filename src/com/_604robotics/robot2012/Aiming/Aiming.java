package com._604robotics.robot2012.Aiming;

import com.sun.squawk.util.MathUtils;
import frc.vision.Target;


/**
 * TODO: Test, and make stuff better.
 * 
 * What should go here?
 * ====================
 *  - Distance calculation code.
 *  - Angle calculation code.
 *  - Aiming code.
 * 
 */

public class Aiming {
	
	public final static Aiming defaultAiming = new Aiming();
	
	double FOV = 47;//degrees
	double pixelsWide = 640;
        double pixelsHigh = 480;
	double kx = 1/(Math.tan(FOV*Math.PI / 180) / pixelsWide * 2);
	double ky = kx;
	double heightOftarget = 18; //inches
	double widthOftarget = 24; //inches


	//may potentially be implemented later:
	private void getSpacialRelationshipToTarget(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		//TODO - fill in stuff
		
		//return dist, angle, angleOfTarget
	}
        
	/**
	 * 
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
	 * 
	 * @return a Point3d holding the X, Y, and Z of the target, relative to the camera.
	 * 
	 */
	public Point3d getRelXYZOfTarget(double x1, double y1, double w, double h) {
		double z = ky * heightOftarget / h;
		//xs = kx * x / z
		//x = xs * z / kx
		double avgx = x1 + w/2;
		double avgy = y1 + h/2;

		double x = avgx * z / kx;
		double y = avgy * z / ky;
		
		return new Point3d(x, y, z);
	}

	public Point3d getRelXYZOfTarget(Target t) {
            return this.getRelXYZOfTarget(t.x1, t.y1, t.w, t.h);
        }

	/**
	 * This function gets the direction the target is facing, relative to the camera.
	 * It is imperfect, and half-assumes a simple orthographic projection (which is not quite like real life).
	 * If it causes issues (which the accuracy of this function doesn't need to be very high), we can fix it later.
	 *
	 * @return the resulting angle in radians.
	 */
	public double getAngleOfTarget(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double z) {
		//based on width/angles/etc of trapezoid

		double dy1 = y1 - y3;
		double dy2 = y2 - y4;

		double dyRatio = dy1/dy2;


		double expectedW = kx * widthOftarget / z;
		double actualW = (x1 + x2 - x3 - x4)/2;

		double wRatio = actualW/expectedW;

		if(wRatio < 0)
			wRatio = 0;

		if(wRatio > 1)
			wRatio = 1;

		return MathUtils.acos(wRatio);
	}

	public PointAndAngle3d getAngleAndRelXYZOfTarget(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		Point3d p = getRelXYZOfTarget(-1, -1, -1, -1);//TODO - fix
		double angle = getAngleOfTarget(x1, y1, x2, y2, x3, y3, x4, y4, p.z);
		
		return new PointAndAngle3d(p, angle);
	}

	void transformPoint(Point2d pt, double angleOfCamera) {
		// TODO - actually transform the point
	}
}
