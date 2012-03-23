package com._604robotics.robot2012.points;

/**
 * Represents a single point in 3D space.
 * 
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Point3d {
	public double x, y, z;
        
        /**
         * Initializes a new Point3d.
         */
        public Point3d() {
            
        }

	/**
         * Initializes a new Point3d.
         * 
	 * @param   x   The x-coordinate of the point.
	 * @param   y   The y-coordinate of the point.
	 * @param   z   The z-coordinate of the point.
	 */
	public Point3d(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
}
