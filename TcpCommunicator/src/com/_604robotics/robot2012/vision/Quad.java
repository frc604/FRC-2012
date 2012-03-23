package com._604robotics.robot2012.vision;

import com._604robotics.robot2012.points.Point2d;

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

	public String toString() {
		return "Quad [topLeft=" + topLeft + ", topRight=" + topRight + ", bottomLeft=" + bottomLeft + ", bottomRight="
				+ bottomRight + "]";
	}

	/**
	 * @return the average width of this Quad
	 */
	public double getAvgWidth() {
		return (topRight.x+bottomRight.x-topLeft.x-bottomLeft.x)/2;
	}
	/**
	 * @return the average height of this Quad
	 */
	public double getAvgHeight() {
		return (topLeft.y+topRight.y-bottomLeft.y-bottomRight.y)/2;
	}

	/**
	 * @return the average X values of this Quad
	 */
	public double getAvgX() {
		return (topLeft.x + topRight.x + bottomLeft.x + bottomRight.x) / 4;
	}
	/**
	 * @return the average Y values of this Quad
	 */
	public double getAvgY() {
		return (topLeft.y + topRight.y + bottomLeft.y + bottomRight.y) / 4;
	}
	

	/**
	 * @return the minimum X value of this Quad
	 */
	public double getMinX() {
		return Math.min(topLeft.x, bottomLeft.x);
	}

	/**
	 * @return the maximum X value of this Quad
	 */
	public double getMaxX() {
		return Math.max(topRight.x, bottomRight.x);
	}

	/**
	 * @return the minimum Y value of this Quad
	 */
	public double getMinY() {
		return Math.min(bottomLeft.y, bottomRight.y);
	}

	/**
	 * @return the maximum Y value of this Quad
	 */
	public double getMaxY() {
		return Math.max(topLeft.y, topRight.y);
	}
}
