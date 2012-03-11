package com._604robotics.robot2012.vision;


/**
 * This represents a point in 3d space
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Point3d {
	/**
	 * the x value
	 */
	public double x;
	
	/**
	 * the y value
	 */
	public double y;
	
	/**
	 * the z value
	 */
	public double z;

	/**
	 * @param x - the x value
	 * @param y - the y value
	 * @param z - the z value
	 */
	public Point3d(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
	/**
	 * @return - the x value
	 */
	public double getX() {
		return x;
	}

	
	/**
	 * @param x - the x value
	 */
	public void setX(double x) {
		this.x = x;
	}

	
	/**
	 * @return - the y value
	 */
	public double getY() {
		return y;
	}

	
	/**
	 * @param y - the y value
	 */
	public void setY(double y) {
		this.y = y;
	}

	
	/**
	 * @return - the y value
	 */
	public double getZ() {
		return z;
	}

	
	/**
	 * @param z - the y value
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	
}
