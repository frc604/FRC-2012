package com._604robotics.robot2012.vision;

public class Target {
	
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
	public double	angle;


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
	public double	angle_uncertainty;	


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
	public double	x, y, z;
	

	/**
	 * These are the uncertainties of the x, y, and z positions of the target.
	 * 
	 * These are interpreted as pluses and minuses to the x, y, and z values.
	 * 
	 * Again, these are in inches.
	 */
	public double	x_uncertainty, y_uncertainty, z_uncertainty;
	

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

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Target [x=" + x + ", y=" + y + ", z=" + z + ", angle=" + angle + "]";
	}
}
