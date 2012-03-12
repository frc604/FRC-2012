package com._604robotics.robot2012.vision;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * This code does the 2D-to-3D calculations
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class DistanceCalculations {
	
	
	public static final double	cameraPixelHeight	= 480, cameraPixelWidth = 640;
	
	/**
	 * A simple main() function added for debugging
	 * 
	 * TODO - delete this
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// about 46 in
		// Quad [topLeft=Point2d [x=194.8762734332836, y=230.03676794085487], topRight=Point2d [x=555.0300833785902,
		// y=249.79778560385398],
		// bottomLeft=Point2d [x=215.42447837542025, y=434.28831770391776], bottomRight=Point2d [x=533.6907841935421,
		// y=443.64852472363634]]
		Quad q = new Quad(new Point2d(194.9, 230), new Point2d(555, 249.8), new Point2d(215.4, 434.3), new Point2d(
				553.7, 443.6));
		
		DistanceCalculations dc = new DistanceCalculations();
		
		System.out.println(dc.getApproximationOfTarget(q));
	}
	
	private Point2d	cameraCorner_0_0	= new Point2d(0, 0), cameraCorner_640_480 = new Point2d(cameraPixelWidth,
												cameraPixelHeight);
	
	private Point2d	cameraCorner_topLeft	= transformPoint(cameraCorner_0_0),
			cameraCorner_bottomRight = transformPoint(cameraCorner_640_480);
	// 320 / (tan(47 / 2 degrees));
	double			FOV						= 47;								// degrees
																				
	double			kx						= 700;								// was 736, then 605; then excel said
	// 695-705
	double			ky						= 530;								// was 736, then 605; then excel said
	private double	nearSideTolerance		= 5 / kx;
	// 514
	double			rotUpDown				= Math.toRadians(-28.5);			// -26
																				
	double			targetHeight			= 24;								// inches
																				
	double			targetWidth				= 18;								// inches
																				
	/**
	 * This function gets the direction the target is facing, relative to the camera. It is imperfect, and half-assumes
	 * a simple orthographic projection (which is not quite like real life). If it causes issues (which the accuracy of
	 * this function doesn't need to be very high), we can fix it later.
	 * 
	 * @return the resulting angle in radians.
	 */
	public double getAngleOfTarget(Quad q, double z) {
		// based on width/angles/etc of trapezoid
		
		double dy1 = q.topLeft.y - q.bottomLeft.y;
		double dy2 = q.topRight.y - q.bottomRight.y;
		
		double dyRatio = dy1 / dy2;
		

		double expectedW = targetHeight / z;
		double actualW = q.getAvgWidth();
		
		double wRatio = actualW / expectedW;
		
		if (wRatio < 0) {
			wRatio = 0;
		}
		
		if (wRatio > 1) {
			wRatio = 1;
		}
		if (VisionProcessing.defaultProcessing.conf.debug_Print) {
			System.out.println(wRatio);
		}
		
		return Math.acos(wRatio) * (dyRatio > 1 ? -1 : 1);
	}
	
	/**
	 * @param quad - a quadrilateral with corners indicating the corners of the target
	 * @return a Target as an estimation of
	 */
	public Target getApproximationOfTarget(Quad quad) {
		
		Quad qTrans = new Quad(null, null, null, null);
		
		// transform the quad based on vertical orientation
		
		qTrans.bottomLeft = transformPoint(quad.bottomLeft);
		qTrans.bottomRight = transformPoint(quad.bottomRight);
		qTrans.topLeft = transformPoint(quad.topLeft);
		qTrans.topRight = transformPoint(quad.topRight);
		
		Target t = new Target();
		
		Point3d p = getRelXYZOfTarget(qTrans);
		t.setPoint(p);
		t.setAngle(getAngleOfTarget(qTrans, p.z));
		
		// Uncertainties
		{
			double plusOrMinus = .5;
			double targetPixHeight = kx / quad.getAvgHeight();
			double frac = (targetPixHeight + plusOrMinus) / (targetPixHeight - plusOrMinus) - 1;
			t.setZUncertainty(frac * t.getZ());
			
			t.setXUncertainty(t.getZ() / kx); // approximately 1 pixel of uncertainty
			t.setYUncertainty(t.getZ() / ky); // approximately 1 pixel of uncertainty
			
			t.setAngleUncertainty(.25); // constant, for now; unknown
			
			if (quad.getMinX() <= cameraCorner_topLeft.x + nearSideTolerance) {
				t.setXUncertainty(9001);
			}
			if (quad.getMaxX() >= cameraCorner_bottomRight.x - nearSideTolerance) {
				t.setXUncertainty(9001);
			}
			
			if (quad.getMinY() <= cameraCorner_bottomRight.y + nearSideTolerance) {
				t.setYUncertainty(9001);
			}
			if (quad.getMaxY() >= cameraCorner_topLeft.y - nearSideTolerance) {
				t.setYUncertainty(9001);
			}
			
		}
		
		return t;
	}
	
	
	/**
	 * 
	 * 
	 * Remember that this requires the camera to be "perfectly" flat, and the targets to be "perfectly" vertical. A new
	 * function will probably need to be created for use on the robot. That, or we'll need to manipulate the points
	 * based on camera angle.
	 * 
	 * 
	 * @return a Point3d holding the X, Y, and Z of the target, relative to the camera.
	 * 
	 */
	public Point3d getRelXYZOfTarget(Quad q) {
		double z = targetHeight / q.getAvgHeight();
		// xs = kx * x / z
		// x = xs * z / kx
		double avgx = q.getAvgX();
		double avgy = q.getAvgY();
		

		double x = avgx * z;
		double y = avgy * z;
		
		return new Point3d(x, y, z);
	}
	
	/**
	 * Credit goes to Colin Aitkens <TODO - email> for figuring out the equations for nX and nY
	 * 
	 * @param p - the Point2d to transform
	 * @return the transformed point (based on camera coords and rotUpDown into values indicating x/z and y/z)
	 */
	private Point2d transformPoint(Point2d p) {
		double x = (p.x - cameraPixelWidth / 2) / kx, y = (cameraPixelHeight / 2 - p.y) / ky;
		
		double cos = cos(rotUpDown);
		double sin = sin(rotUpDown);
		
		double nX = x / (cos - y * sin);
		double nY = (y * cos + sin) / (cos - y * sin);
		
		return new Point2d(nX, nY);
	}
}
