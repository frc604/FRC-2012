package com._604robotics.robot2012.aiming;

public class PointAndAngle3d {
	double x, y, z, angle;

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param angle
	 */
	public PointAndAngle3d(double x, double y, double z, double angle) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
	}

	public PointAndAngle3d(Point3d p, double angle) {
		x = p.x;
		y = p.y;
		z = p.z;
		
		this.angle = angle;
	}
	
	
}
