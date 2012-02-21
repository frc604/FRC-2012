package com._604robotics.robot2012.vision;

public class Quad {
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

	Point2d topLeft, topRight, bottomLeft, bottomRight;
	
}
