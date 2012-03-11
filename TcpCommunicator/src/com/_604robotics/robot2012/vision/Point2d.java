package com._604robotics.robot2012.vision;


/**
 * This represents a Point in 2d space
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Point2d {
	
	/**
	 * The x value
	 */
	public double	x;
	
	/**
	 * The y value
	 */
	public double	y;
	
	/**
	 * @param x - the x value
	 * @param y - the y value
	 */
	public Point2d(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * @return - the x value
	 */
	public double getX() {
		return x;
	}
	
	
	/**
	 * @return - the y value
	 */
	public double getY() {
		return y;
	}
	
	
	/**
	 * @param x - the x value
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	
	/**
	 * @param y - the y value
	 */
	public void setY(double y) {
		this.y = y;
	}


	public String toString() {
		return "Point2d [x=" + x + ", y=" + y + "]";
	}
	

}
