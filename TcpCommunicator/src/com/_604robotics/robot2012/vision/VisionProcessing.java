package com._604robotics.robot2012.vision;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import logging.Logger;

import com._604robotics.robot2012.points.Point2d;
import com._604robotics.robot2012.vision.LinearRegression.RegressionResult;
import com._604robotics.robot2012.vision.config.Config;
import com._604robotics.tcpcommunicator.TcpCommunicator;
import com.charliemouse.cambozola.shared.CamStream;


/**
 * The main class for processing camera vision on our 2012 robot. This software takes in camera images from the
 * robot's camera, parses them, searches for pixels that look like shiny blue vision targets, blobs those pixels
 * together, (if they are connected), and then treats it as a quadrilateral and finds the corners.
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class VisionProcessing {
	
	/**
	 * The default VisionProcessing to use; this should be where the root of all of the vision processing is done
	 */
	public static final VisionProcessing defaultProcessing = new VisionProcessing();
	
	/**
	 * Constants indicating the Left, Top, Right, and Bottom sides of a target or bounding box.
	 */
	public static final int			Side_Left				= 0, Side_Top = 1, Side_Right = 2, Side_Bottom = 3;
	
	/**
	 * The Configuration file for this VisionProcessing
	 */
	public Config conf = Config.readDefaultConfig();
	
	/**
	 * This function determines the distances from a side to the points on the target, in a direction perpendicular to
	 * the side in question.
	 * 
	 * @param ri	the ResultImage to process
	 * @param x1	the lowest X value to check
	 * @param y1	the lowest Y value to check
	 * @param x2	the highest X value to check
	 * @param y2	the highest Y value to check
	 * @param side	which side to check. This uses the constants such as {@link #Side_Left}.
	 * @param lenMajor	the length of the side in question
	 * @param lenMinor	the length to check perpendicular to the side in question
	 * @param xVals	the returned array of X values on the target nearest the given side.
	 * @param yVals	the returned array of Y values on the target nearest the given side.
	 */
	private static void getDistsForSide(ResultImage ri, int x1, int y1, int x2, int y2, int side, int lenMajor,
			int lenMinor, double[] xVals, double[] yVals) {
		
		// Iterate through the Major length
		for (int i = 0; i < lenMajor; i++) {
			
			// declare the x and y arrays
			xVals[i] = Double.NaN;
			yVals[i] = Double.NaN;
			
			// find the current x or y
			int x = 0, y = 0;
			if (side == Side_Left || side == Side_Right) {
				y = i + y1;
			} else { // top or bottom
				x = i + x1;
			}
			
			// Iterate through the Minor length
			for (int j = 0; j < lenMinor; j++) {
				
				// find the other one of x or y
				if (side == Side_Left) {
					x = x1 + j;
				} else if (side == Side_Top) {
					y = y1 + j;
				} else if (side == Side_Right) {
					x = x2 - j;
				} else { // bottom
					y = y2 - j;
				}
				
				// If it's a target, then add it to the list and break
				if (ri.isTarget(x, y)) {
					xVals[i] = x;
					yVals[i] = y;
					
					break;
				}
			}
		}
	}
	
	/**
	 * Get a line that best fits the sides of a given target
	 * 
	 * @param ri	the ResultImage that indicates which pixels are contained in the target
	 * @param side	an integer indicating which of the sides to pick
	 * @param guess	a bounding box that surrounds all of the pixels to check
	 * 
	 * @return the line of best fit for the given side of the target lying in the AABB
	 */
	public RegressionResult getRegressionForSide(ResultImage ri, int side, AABB guess) {
		int tileSize = conf.getInt("tileSize");
		
		int x1 = guess.x1 * tileSize - 1, x2 = guess.x2 * tileSize + tileSize, y1 = guess.y1 * tileSize - 1, y2 = guess.y2 * tileSize
		+ tileSize;
		
		int lenMajor = 0;
		int lenMinor = 0;
		
		if (side == Side_Left || side == Side_Right) {
			lenMajor = y2 - y1;
			lenMinor = x2 - x1;
		} else { // top, bottom
			lenMajor = x2 - x1;
			lenMinor = y2 - y1;
		}
		
		// using simple method...
		double[] x = new double[lenMajor];
		double[] y = new double[lenMajor];
		
		getDistsForSide(ri, x1, y1, x2, y2, side, lenMajor, lenMinor / 2, x, y);
		
		// trim off bottom and top 10%
		
		int fewPercent = x.length / 10;// +- 10%, rounded down
		for (int i = 0; i < fewPercent; i++) {
			x[i] = Double.NaN;
			x[x.length - i - 1] = Double.NaN;
		}
		
		//return the linear regression of the side
		if (side == Side_Left || side == Side_Right)
			return LinearRegression.getBackwardsRegression(x, y);
		else
			return LinearRegression.getRegression(x, y);
		
	}
	
	/**
	 * Just a simple main() function for running and testing the target tracking
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		VisionProcessing vp = defaultProcessing;
		vp.startWindow();
		vp.loopAndProcessPics();
		//vp.loopAndProcessPreSavedPics();
		
	}
	
	/**
	 * @param results	the Img to store returned data in
	 * @param i	the X coordinate
	 * @param j	the Y coordinate
	 * @param color	the blob's color
	 */
	public static void recursiveTraceBlobs(Img results, int i, int j, int color) {
		results.set(i, j, color);
		
		final int[] xlist = {1, 0, -1, 0};
		final int[] ylist = {0, 1, 0, -1};
		
		for (int k = 0; k < 4; k++) {
			if (results.get(i + xlist[k], j + ylist[k]) == -1) {
				recursiveTraceBlobs(results, i + xlist[k], j + ylist[k], color);
			}
		}
	}
	
	/**
	 * The communications with the robot
	 */
	private final TcpCommunicator	comm			= new TcpCommunicator();
	
	/**
	 * A number indicating the current frame number
	 */
	int								currentFrame	= 0;
	
	/**
	 * The display for showing the image as well as some debug data.
	 * 
	 * It shows targets in green, and sides and corners in blue.
	 */
	public final VisionDisp						display			= new VisionDisp();
	
	/**
	 * A constructor to create a new VisionProcessing
	 */
	public VisionProcessing() {
		if (conf.getBoolean("communicateToRobot")) {
			comm.up(); // TODO - live enable/disable when conf.communicateToRobot is set
		}
		if (conf.getBoolean("debug_SaveImagesToFiles")) {
			new File("target/").mkdir();
		}
		
		new Thread(imgSavr).start();
	}
	
	/**
	 * A method to create a window for viewing the camera image (potentially with debug data drawn)
	 */
	private void startWindow() {
		JFrame displayWindow = new JFrame("604 - FRC 2012 Vision");
		displayWindow.add(display);
		
		displayWindow.pack();
		displayWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		displayWindow.setVisible(true);
	}
	
	/**
	 * This function waits for images from the image stream, processes them, and then sends results to the robot.
	 * 
	 * 
	 * @throws MalformedURLException
	 */
	public void loopAndProcessPics() throws MalformedURLException {
		
		// Set the username and password used to access the camera
		Authenticator.setDefault(new com.mobvcasting.mjpegparser.HTTPAuthenticator("FRC", "FRC"));
		
		// Start streaming images from the camera
		URL url = new URL("http://10.6.4.11/mjpg/video.mjpg");
		CamStream stream = new CamStream(url, "", null, Integer.MAX_VALUE, 100, null, false);
		stream.start();
		
		BufferedImage lastImg = null;
		BufferedImage img = null;
		
		// loop through frames as they are received
		while (true) {
			
			// while a new image has not been received, wait
			while ((img = stream.getCurrent()) == lastImg) {
				if (!stream.isAlive()) {
					stream = new CamStream(url, "", null, Integer.MAX_VALUE, 100, null, false);
					stream.start();
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
				}
			}
			
			lastImg = img;
			
			// process the newly received image. If this is too slow, frames will just be dropped.
			try {
				processImage(img);
			} catch(Exception ex) {
				Logger.ex(ex);
			}
			
			currentFrame++;
			
			if (conf.getBoolean("debug_SaveImagesToFiles")) {
				imgSavr.currentFrame = currentFrame;
				imgSavr.img = img;
			}
		}
	}
	
	private static final ImgSavr imgSavr = new ImgSavr();
	private static class ImgSavr implements Runnable {
		int currentFrame;
		private BufferedImage img;
		private BufferedImage last = null;
		public void run() {
			while(true) {
				try {
					Logger.log("saving");
					BufferedImage i = img;
					if(i != last) {
						ImageIO.write(i, "jpeg", new File("target/" + currentFrame + ".jpeg"));
						last = i;
					} else
						Thread.sleep(5);
					
				} catch (Exception ex) {
					Logger.ex(ex);
				}
			}
		}
	}
	
	/**
	 * This function is just a simple debug function for testing with pre-saved images. Currently, it just reads over a
	 * loop of 50 pictures saved as target/[number].jpeg
	 */
	public void loopAndProcessPreSavedPics() throws IOException {
		
		File[] list = new File("target/").listFiles();
		for (int i = 0; true; i++) {
			
			if (i >= list.length) {
				i -= list.length;
			}
			
			try {
				processImage(ImageIO.read(list[i]));
				
				Thread.sleep(100);
			} catch (Exception ex) {}
			
		}
	}
	
	/**
	 * A simple array used to buffer each image into
	 */
	int[] imageBuffer;
	
	/**
	 * This processes the camera image and can send it to the robot (if enabled in the config file)
	 * 
	 * @param img	an image as received from the camera
	 */
	public void processImage(BufferedImage img) {
		
		double time_i = System.nanoTime();
		
		int w = img.getWidth();
		int h = img.getHeight();
		
		//double time_i2 = System.nanoTime();
		ResultImage ri = new ResultImage(w, h);
		if(imageBuffer == null)
			imageBuffer = new int[w*h];
		Img img_copy = new Img(img.getRaster(), imageBuffer);
		ri.computeResults(img_copy);
		// Logger.log("result Time = " + (System.nanoTime()-time_i2)/1000000000);
		
		// now go thru and compute targets and their AABBs
		
		Img results = new Img(ri.sW, ri.sH);
		for (int i = 0; i < ri.results.length; i++) {
			if (ri.results[i] != null && ri.results[i].hasPlus()) {
				results.dat[i] = -1;// needs blobification
			} else {
				results.dat[i] = 0;// no match
			}
		}
		
		// Blob stuff
		int colors = 0;
		for (int i = 0; i < results.w; i++) {
			for (int j = 0; j < results.h; j++) {
				if (results.get(i, j) == -1) {
					recursiveTraceBlobs(results, i, j, ++colors);
				}
			}
		}
		// This took < 1 ms during testing
		
		int[] blobSize = new int[colors];// blob 0 is 0
		AABB[] rough = new AABB[colors];// target 0 is null
		for (int i = 0; i < rough.length; i++) {
			rough[i] = new AABB(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		}
		
		for (int i = 0; i < results.w; i++) {
			for (int j = 0; j < results.h; j++) {
				int color = results.get(i, j);
				if (color > 0) {
					blobSize[color - 1]++;
					AABB t = rough[color - 1];
					
					t.x1 = min(t.x1, i);
					t.y1 = min(t.y1, j);
					t.x2 = max(t.x2, i);
					t.y2 = max(t.y2, j);
				}
			}
		}
		
		// TODO - combine blobs with intersecting AABBs?
		
		LinearRegression.RegressionResult[] linearRegressions = new LinearRegression.RegressionResult[colors * 4];
		
		Quad[] targetQuads = new Quad[colors];
		
		Target[] targets = new Target[colors];
		
		Point2d[] pts = new Point2d[colors * 4];
		int minBlobSize = conf.getInt("minBlobSize");
		for (int i = 0; i < colors; i++) {
			if (blobSize[i] >= minBlobSize) {
				
				LinearRegression.RegressionResult top = getRegressionForSide(ri, Side_Top, rough[i]);
				LinearRegression.RegressionResult bottom = getRegressionForSide(ri, Side_Bottom, rough[i]);
				LinearRegression.RegressionResult left = getRegressionForSide(ri, Side_Left, rough[i]);
				LinearRegression.RegressionResult right = getRegressionForSide(ri, Side_Right, rough[i]);
				
				Point2d topLeft = LinearRegression.solve(top, left);
				Point2d topRight = LinearRegression.solve(top, right);
				Point2d bottomLeft = LinearRegression.solve(bottom, left);
				Point2d bottomRight = LinearRegression.solve(bottom, right);
				
				pts[i * 4] = topLeft;
				pts[i * 4 + 1] = topRight;
				pts[i * 4 + 2] = bottomLeft;
				pts[i * 4 + 3] = bottomRight;
				
				Quad q = new com._604robotics.robot2012.vision.Quad(topLeft, topRight, bottomLeft, bottomRight);
				
				targets[i] = new DistanceCalculations().getApproximationOfTarget(q);
				if(conf.getBoolean("debug_Print"))
					Logger.log(targets[i]);
				if(conf.getBoolean("debug_Print"))
					Logger.log(q);
				
				targetQuads[i] = q;
				
				linearRegressions[i * 4] = top;
				linearRegressions[i * 4 + 1] = bottom;
				linearRegressions[i * 4 + 2] = right;
				linearRegressions[i * 4 + 3] = left;
			} else {
			}
		}
		
		
		//get rid of null targets...
		int targetCount = 0;
		for(int i = 0; i < colors; i++) {
			if(targets[i] != null)
				targetCount++;
		}
		Target[] targets_tmp = new Target[targetCount];
		targetCount = 0;
		for(int i = 0; i < colors; i++) {
			if(targets[i] != null) {
				targets_tmp[targetCount++] = targets[i];
			}
		}
		targets = targets_tmp;
		
		//sort based on height...
		Arrays.sort(targets);
		
		if(conf.getBoolean("debug_Print"))
			Logger.log(Arrays.toString(targets));
		
		if (conf.getBoolean("communicateToRobot")) {
			comm.writePoints(targets);
		}
		
		
		if(conf.getBoolean("debug_Print")) {
			Logger.log("Time = " + (System.nanoTime() - time_i) / 1000000000);
			
			Logger.log("--");
		}
		
		display.image = img;
		
		if (conf.getBoolean("debug_ShowDisplay")) {
			display.targetSides = linearRegressions;
			display.resultImage = ri;
			display.hasPainted = false;
			display.targetCorners = pts;
			
			
			/* Uncomment out the following lines to only compute after rendering is finished
			while (!display.hasPainted) {
				try {
					Thread.sleep(2);
				} catch (InterruptedException ex) {
				}
			}
			 */
			
		} else {
			display.targetSides = null;
			display.resultImage = null;
			display.hasPainted = false;
			display.targetCorners = null;
		}
		display.repaint();
		
	}
}
