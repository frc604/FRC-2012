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
public class Target {
	
	/**
	 * The distance from the center of the target to the Y (vertical) value of the hoop.
	 */
	public static final double	RelHoopY	= -11;	// inches
	/**
	 * The distance from the center of the target to the Z (depth) value of the hoop.
	 */
	public static final double	RelHoopZ	= -15;	// inches
													

	/**
	 * This is the angle of the target, relative to the camera. </br>
	 * 
	 * </br>
	 * 
	 * (angle) </br> ......(Target) </br> ......./ </br> ....../ </br> ...../ </br> ..../ - - - - - - - |> (Camera)
	 * </br> .../ </br> ../ </br> ./ </br> / </br>
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
	public double				angleUncertainty;
	


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
	public double				xUncertainty, yUncertainty, zUncertainty;
	
	

	/**
	 * A blank constructor to easily make a Target
	 */
	public Target() {
		
	}
	
	

	/**
	 * @param x the X coordinate of the center of the vision target
	 * @param y the Y coordinate of the center of the vision target
	 * @param z the Z coordinate of the center of the vision target
	 * @param angle
	 */
	public Target(double x, double y, double z, double angle) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
	}
	
	

	/**
	 * @param x	the X coordinate of the center of the vision target
	 * @param y	the Y coordinate of the center of the vision target
	 * @param z	the Z coordinate of the center of the vision target
	 * @param xUncertainty	the X Uncertainty
	 * @param yUncertainty	the Y Uncertainty
	 * @param zUncertainty	the Z Uncertainty
	 * @param angle	the Angle
	 * @param angleUncertainty	the Angle Uncertainty
	 */
	public Target(double x, double y, double z, double xUncertainty, double yUncertainty, double zUncertainty,
			double angle, double angleUncertainty) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.xUncertainty = xUncertainty;
		this.yUncertainty = yUncertainty;
		this.zUncertainty = zUncertainty;
		this.angle = angle;
		this.angleUncertainty = angleUncertainty;
	}
	
	

	/**
	 * @param point	the Point
	 * @param angle	the Angle
	 */
	public Target(Point3d point, double angle) {
		this(point.x, point.y, point.z, angle);
	}
	

	/**
	 * @return the angle that the vision target faces
	 */
	public double getAngle() {
		return angle;
	}
	
	

	/**
	 * @return the uncertainty of the Angle
	 */
	public double getAngleUncertainty() {
		return angleUncertainty;
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
	 *         center of the target. This is useful for bounces
	 */
	public Point3d getReflectedHoopPosition() {
		return getReflectedHoopPosition(1);
	}
	
	

	/**
	 * @param bounceFactor a number that scales the changes in the x and z distances due to correction for hoop
	 *        position. In a idealized collision, this is equal to the inverse of its coefficient of restitution.
	 *        However, with spin, this number should be less.
	 * @return the reflected position of the hoop accounting for the fact that the center of the hoop is not at the
	 *         center of the target. This is useful for bounces
	 */
	public Point3d getReflectedHoopPosition(double bounceFactor) {
		return new Point3d(x - Math.sin(angle) * RelHoopZ * bounceFactor, y + RelHoopY, z - Math.cos(angle) * RelHoopZ
				* bounceFactor);
	}
	
	

	/**
	 * @return the X coordinate of the center of the vision target
	 */
	public double getX() {
		return x;
	}
	
	

	/**
	 * @return the Uncertainty of the X coordinate
	 */
	public double getXUncertainty() {
		return xUncertainty;
	}
	
	
	/**
	 * @return the Y coordinate of the center of the vision target
	 */
	public double getY() {
		return y;
	}
	
	
	/**
	 * @return the Uncertainty of the Y coordinate
	 */
	public double getYUncertainty() {
		return yUncertainty;
	}
	
	
	/**
	 * @return the Z coordinate of the center of the vision target
	 */
	public double getZ() {
		return z;
	}
	
	
	/**
	 * @return the Uncertainty of the Z coordinate of the vision target
	 */
	public double getZUncertainty() {
		return zUncertainty;
	}
	
	
	/**
	 * @param angle the Angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	
	/**
	 * @param angleUncertainty the angleUncertainty to set
	 */
	public void setAngleUncertainty(double angleUncertainty) {
		this.angleUncertainty = angleUncertainty;
	}
	
	
	/**
	 * @param point the point to set the center of this target
	 */
	public void setPoint(Point3d point) {
		x = point.x;
		y = point.y;
		z = point.z;
	}
	
	/**
	 * @param x the X to set
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * @param xUncertainty the xUncertainty to set
	 */
	public void setXUncertainty(double xUncertainty) {
		this.xUncertainty = xUncertainty;
	}
	
	/**
	 * @param y the Y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * @param yUncertainty the yUncertainty to set
	 */
	public void setYUncertainty(double yUncertainty) {
		this.yUncertainty = yUncertainty;
	}
	
	/**
	 * @param z the Z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}
	
	/**
	 * @param zUncertainty the zUncertainty to set
	 */
	public void setZUncertainty(double zUncertainty) {
		this.zUncertainty = zUncertainty;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Target [x=" + x + ", y=" + y + ", z=" + z + ", angle=" + angle + "]";
	}
}