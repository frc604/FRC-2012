package com._604robotics.robot2012.vision;

/**
 * A class representing a Quadrilateral, with four corner points.
 * 
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 * 
 */
public class Quad {
	
	/**
	 * The points representing the corners of this quadrilateral
	 */
	Point2d	topLeft, topRight, bottomLeft, bottomRight;
	
	/**
	 * @param topLeft
	 * @param topRight
	 * @param bottomLeft
	 * @param bottomRight
	 */
	public Quad(Point2d topLeft, Point2d topRight, Point2d bottomLeft, Point2d bottomRight) {
		super();
		this.topLeft = topLeft;
		this.topRight = topRight;
		this.bottomLeft = bottomLeft;
		this.bottomRight = bottomRight;
	}
	
}
