package com._604robotics.robot2012.vision;

import logging.Logger;

import com._604robotics.robot2012.points.Point2d;
import com._604robotics.robot2012.points.Point3d;

/**
 * This code does the 2D-to-3D calculations
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class DistanceCalculations {
	
	
	/**
	 * The size of the Axis camera, in pixels
	 */
	public static final double	cameraPixelHeight	= 480, cameraPixelWidth = 640;
	
	/**
	 * The untransformed corners of the image
	 */
	private Point2d	cameraCorner_0_0	= new Point2d(0, 0), cameraCorner_640_480 = new Point2d(cameraPixelWidth,
												cameraPixelHeight);
	
	/**
	 * The transformed corners of the image
	 */
	private Point2d	cameraCorner_topLeft	= transformPoint(cameraCorner_0_0),
			cameraCorner_bottomRight = transformPoint(cameraCorner_640_480);
	// 320 / (tan(47 / 2 degrees));
	
	/**
	 * If a target corner is estimated to be less than 5 pixels away from the edge, the target is reported
	 * as unreliable
	 */
	private double	nearSideTolerance		= 5 / VisionProcessing.defaultProcessing.conf.getDouble("kx");
																				
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
		

		double expectedW = VisionProcessing.defaultProcessing.conf.getDouble("targetWidth") / z;
		double actualW = q.getAvgWidth();
		
		double wRatio = actualW / expectedW;
		
		System.out.println(wRatio);
		
		if (wRatio < 0) {
			wRatio = 0;
		}
		
		if (wRatio > 1) {
			wRatio = 1;
		}
		Logger.log(wRatio);
		
		return Math.acos(wRatio) * (dyRatio < 1 ? -1 : 1);
	}
	
	/**
	 * A method that tries to find the most likely location for the vision target to lie in 3D space
	 * 
	 * @param quad	a quadrilateral with corners indicating the corners of the target
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
			double targetPixHeight = VisionProcessing.defaultProcessing.conf.getDouble("ky") / quad.getAvgHeight();
			double frac = (targetPixHeight + plusOrMinus) / (targetPixHeight - plusOrMinus) - 1;
			t.setZUncertainty(frac * t.getZ());
			
			t.setXUncertainty(t.getZ() / VisionProcessing.defaultProcessing.conf.getDouble("kx")); // approximately 1 pixel of uncertainty
			t.setYUncertainty(t.getZ() / VisionProcessing.defaultProcessing.conf.getDouble("ky")); // approximately 1 pixel of uncertainty
			
			t.setAngleUncertainty(.25); // constant, for now; unknown
			
			if (quad.getMinX() <= cameraCorner_topLeft.x + nearSideTolerance) {
				t.setXUncertainty(9001);
				t.setAngleUncertainty(9001);
			}
			if (quad.getMaxX() >= cameraCorner_bottomRight.x - nearSideTolerance) {
				t.setXUncertainty(9001);
				t.setAngleUncertainty(9001);
			}
			
			if (quad.getMinY() <= cameraCorner_bottomRight.y + nearSideTolerance) {
				t.setYUncertainty(9001);
				t.setAngleUncertainty(9001);
			}
			if (quad.getMaxY() >= cameraCorner_topLeft.y - nearSideTolerance) {
				t.setYUncertainty(9001);
				t.setAngleUncertainty(9001);
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
		double z = VisionProcessing.defaultProcessing.conf.getDouble("targetHeight") / q.getAvgHeight();
		// xs = kx * x / z
		// x = xs * z / kx
		double avgx = q.getAvgX();
		double avgy = q.getAvgY();
		

		double x = avgx * z;
		double y = avgy * z;
		
		return new Point3d(x, y, z);
	}
	
	/**
	 * Credit goes to Colin Aitken <cacolinerd@gmail.com> for figuring out the equations for nX and nY
	 * 
	 * @param p	the Point2d to transform
	 * @return the transformed point (based on camera coords and rotUpDown into values indicating x/z and y/z)
	 */
	private Point2d transformPoint(Point2d p) {
		double x = (p.x - cameraPixelWidth / 2) / VisionProcessing.defaultProcessing.conf.getDouble("kx"),
		y = (cameraPixelHeight / 2 - p.y) / VisionProcessing.defaultProcessing.conf.getDouble("ky");
		
		double rotUpDown = Math.toRadians(-VisionProcessing.defaultProcessing.conf.getDouble("camAngle"));
		
		double cos = Math.cos(rotUpDown);
		double sin = Math.sin(rotUpDown);
		
		double nX = x / (cos - y * sin);
		double nY = (y * cos + sin) / (cos - y * sin);
		
		return new Point2d(nX, nY);
	}
}
