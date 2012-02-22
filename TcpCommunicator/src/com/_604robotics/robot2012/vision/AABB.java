package com._604robotics.robot2012.vision;

/**
 * An Axis-Aligned Bounding Box. This stores two opposite corner values of a rectangle that has perfectly vertical and
 * horizontal sides.
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class AABB {
	
	public int	x1, y1, x2, y2;
	
	/**
	 * @param x1 - lowest x value on the rectangle
	 * @param y1 - lowest y value on the rectangle
	 * @param x2 - highest x value on the rectangle
	 * @param y2 - highest y value on the rectangle
	 */
	public AABB(int x1, int y1, int x2, int y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	

}
