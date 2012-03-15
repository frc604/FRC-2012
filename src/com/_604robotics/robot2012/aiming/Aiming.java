package com._604robotics.robot2012.aiming;

import com.sun.squawk.util.MathUtils;
import frc.vision.Target;


/**
 * Utility class for various aiming functions and such.
 * 
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
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
	 * @param x1 x-value of the bottom left corner
         * @param y1 y-value of the bottom left corner
         * @param w width of the vision target
         * @param h height of the vision target
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
         * @param x1 x-value of the bottom left corner
         * @param y1 y-value of the bottom left corner
         * @param x2
         * @param y2
         * @param x3
         * @param y3
         * @param x4
         * @param y4
         * @param z 
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
        /**
         * Get the angle from the targets, and the relative distances of the corners of the target as perceived by the camera.
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         * @param x3
         * @param y3
         * @param x4
         * @param y4
         * @return 
         */
	public PointAndAngle3d getAngleAndRelXYZOfTarget(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
		Point3d p = getRelXYZOfTarget(-1, -1, -1, -1);//TODO - fix
		double angle = getAngleOfTarget(x1, y1, x2, y2, x3, y3, x4, y4, p.z);
		
		return new PointAndAngle3d(p, angle);
	}
        /**
         * Transform the point.
         * @param pt
         * @param angleOfCamera 
         */
	void transformPoint(Point2d pt, double angleOfCamera) {
		// TODO - actually transform the point
	}
}