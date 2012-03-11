package com._604robotics.robot2012.vision;

/**
 * <p>
 * This class represents a physical vision Target with four main attributes (x, y, z, angle). As well, there are
 * estimated uncertainties attached to all of these numbers.
 * </p>
 * 
 * <p>
 * To get the position of the hoop, use the DistanceCalculations class.
 * </p>
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Target implements Comparable<Target> {
	
	/**
	 * The distance from the center of the target to the Y (vertical) value of the hoop.
	 */
	public static final double	RelHoopY	= -11;	// inches
	/**
	 * The distance from the center of the target to the Z (depth) value of the hoop.
	 */
	public static final double	RelHoopZ	= +15;	// inches
													
	/**
	 * This is the angle of the target, relative to the camera. </br>
	 * 
	 * </br>
	 * 
	 * (angle)							</br>
	 * ......(Target)					</br>
	 * ......./							</br>
	 * ....../							</br>
	 * ...../							</br>
	 * ..../ - - - - - - - |> (Camera)	</br>
	 * .../								</br>
	 * ../								</br>
	 * ./								</br>
	 * /
	 * </br>
	 * 
	 * this value is expressed in radians.
	 */
	public double				angle;
	

	/**
	 * 
	 * This is the uncertainty of the angle of the target.
	 * 
	 * This is interpreted as a plus or minus to the angle.
	 * 
	 * Again, this is expressed in radians
	 * 
	 * 
	 */
	public double				angle_uncertainty;
	

	/**
	 * x, y, and z represent the 3-d position of the target
	 * 
	 * x will be positive when the target appears to be right of the center of the camera. y will be positive when the
	 * target appears to be above of the center of the camera. z will always be negative (see <a
	 * href="http://en.wikipedia.org/wiki/Right-hand_rule">Wikipedia: Right-hand rule</a>). As the absolute value of z
	 * increases, so does the distance from the camera to the target.
	 * 
	 * To determine the approximate accuracy of these values, check [x, y, z]_accuracy.
	 * 
	 * The units of these measures are in inches.
	 */
	public double				x, y, z;
	

	/**
	 * These are the uncertainties of the x, y, and z positions of the target.
	 * 
	 * These are interpreted as pluses and minuses to the x, y, and z values.
	 * 
	 * Again, these are in inches.
	 */
	public double				x_uncertainty, y_uncertainty, z_uncertainty;
	
	
	/**
	 * A blank constructor to easily make a Target
	 */
	public Target() {
		
	}
	
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param angle
	 */
	public Target(double x, double y, double z, double angle) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
	}
	
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param x_uncertainty
	 * @param y_uncertainty
	 * @param z_uncertainty
	 * @param angle
	 * @param angle_uncertainty
	 */
	public Target(double x, double y, double z, double x_uncertainty, double y_uncertainty, double z_uncertainty, double angle, double angle_uncertainty) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.x_uncertainty = x_uncertainty;
		this.y_uncertainty = y_uncertainty;
		this.z_uncertainty = z_uncertainty;
		this.angle = angle;
		this.angle_uncertainty = angle_uncertainty;
	}
	
	/**
	 * @param point
	 * @param angle
	 */
	public Target(Point3d point, double angle) {
		this(point.x, point.y, point.z, angle);
	}
	
	/**
	 * @return the position of the hoop accounting for the fact that the center of the hoop is not at the center of the
	 *         target
	 */
	public Point3d getHoopPosition() {
		return new Point3d(x + Math.sin(angle) * RelHoopZ, y + RelHoopY, z + Math.cos(angle) * RelHoopZ);
	}
	
	/**
	 * @return the reflected position of the hoop accounting for the fact that the center of the hoop is not at the
	 *         center of the target. This is useful bounces
	 */
	public Point3d getReflectedHoopPosition() {
		return getReflectedHoopPosition(1);
	}
	
	/**
	 * @param bounceFactor a number that scales the changes in the x and z distances due to correction for hoop
	 *        position. In a idealized collision, this is equal to the inverse of its coefficient of restitution.
	 *        However, with spin, this number should be less.
	 * @return the reflected position of the hoop accounting for the fact that the center of the hoop is not at the
	 *         center of the target. This is useful bounces
	 */
	public Point3d getReflectedHoopPosition(double bounceFactor) {
		return new Point3d(x - Math.sin(angle) * RelHoopZ * bounceFactor, y + RelHoopY, z - Math.cos(angle) * RelHoopZ * bounceFactor);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Target [x=" + x + ", y=" + y + ", z=" + z + ", angle=" + angle + "]";
	}


	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Target that) {
		if(this.y > that.y)
			return -1;
		else if(this.y < that.y)
			return 1;
		return 0;
	}
}
