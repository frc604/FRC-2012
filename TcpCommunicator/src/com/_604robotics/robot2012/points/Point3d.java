package com._604robotics.robot2012.points;


/**
 * This represents a point in 3d space
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Point3d {
	/**
	 * The X value
	 */
	public double x;
	
	/**
	 * The Y value
	 */
	public double y;
	
	/**
	 * The Z value
	 */
	public double z;

	/**
	 * @param x	The X value
	 * @param y	The Y value
	 * @param z	The Z value
	 */
	public Point3d(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
	/**
	 * @return	The X value
	 */
	public double getX() {
		return x;
	}

	
	/**
	 * Sets the X value of this Point
	 * 
	 * @param x	The X value
	 */
	public void setX(double x) {
		this.x = x;
	}

	
	/**
	 * @return	The Y value
	 */
	public double getY() {
		return y;
	}

	
	/**
	 * Sets the Y value of this Point
	 * 
	 * @param y	The Y value
	 */
	public void setY(double y) {
		this.y = y;
	}

	
	/**
	 * @return	The Z value
	 */
	public double getZ() {
		return z;
	}

	
	/**
	 * Sets the Z value of this Point
	 * 
	 * @param z	The Z value
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	
}
