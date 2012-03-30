package com._604robotics.robot2012.points;


/**
 * This represents a Point in 2d space
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Point2d {
	
	/**
	 * The X value
	 */
	public double	x;
	
	/**
	 * The Y value
	 */
	public double	y;
	
	/**
	 * @param x	the X value
	 * @param y	the Y value
	 */
	public Point2d(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	
	/**
	 * @return	the X value
	 */
	public double getX() {
		return x;
	}
	
	
	/**
	 * @return	the Y value
	 */
	public double getY() {
		return y;
	}
	
	
	/**
	 * Sets the X value of this Point
	 * 
	 * @param x	the X value
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	
	/**
	 * Sets the Y value of this Point
	 * 
	 * @param y	the Y value
	 */
	public void setY(double y) {
		this.y = y;
	}


	public String toString() {
		return "Point2d [x=" + x + ", y=" + y + "]";
	}
	

}