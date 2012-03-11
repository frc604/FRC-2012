package com._604robotics.robot2012.aiming;

/**
 * A class to hold a 3d point.
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 * @author Sebastian Merz <merzbasti95@gmail.com>
 * 
 */
public class PointAndAngle3d {
	double x, y, z, angle;

	/**
	 * Initializes variables for the point..
         * 
         * @param x     The x coordinate of the point.
	 * @param y     The y coordinate of the point.
	 * @param z     The z coordinate of the point.
	 * @param angle The angle the target is at from the robot.
	 */
	public PointAndAngle3d(double x, double y, double z, double angle) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
	}
        
        /**
         * Initializes variables for the point.
         * 
         * @param p Uses the values from this point to create the new point.
         * @param angle Uses this angle for the new point.
         */
	public PointAndAngle3d(Point3d p, double angle) {
		x = p.x;
		y = p.y;
		z = p.z;
		
		this.angle = angle;
	}
	
	
}
