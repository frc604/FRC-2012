package com._604robotics.robot2012.vision;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com._604robotics.robot2012.vision.LinearRegression.BackwardsRegressionResult;
import com._604robotics.robot2012.vision.LinearRegression.RegressionResult;


/**
 * This class is used to display a camera image and some debug information along with it.
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class VisionDisp extends JPanel {
	
	/**
	 * Auto-generated serialVersionUID
	 */
	private static final long			serialVersionUID	= -2167719831931210343L;
	
	/**
	 * This value is false until this window is done painting
	 */
	boolean								hasPainted			= false;
	
	/**
	 * The background image, as received from the camera
	 */
	public BufferedImage				image;
	

	/**
	 * <p>
	 * This is the tiled image indicating which pixels are in the target.
	 * </p>
	 * 
	 * <p>
	 * It is displayed as a large mask of red and green squares.
	 * </p>
	 */
	ResultImage							resultImage;
	
	/**
	 * The corners to display on-screen
	 */
	Point2d[]							targetCorners;
	

	/**
	 * The sides of the target
	 */
	LinearRegression.RegressionResult[]	targetSides;
	
	/**
	 * A default constructor that sets this up as a 640x480 display
	 */
	public VisionDisp() {
		Dimension size = new Dimension(640, 480);
		
		// Set the size of this VisionDisp
		this.setSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		
	}
	
	/**
	 * <p>
	 * Paints this {@code VisionDisp}.
	 * </p>
	 * 
	 * <p>
	 * If available, this draws the camera image, resulting tiled red-and-green "isTarget" image, target corners, and
	 * target sides
	 * </p>
	 * 
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		
		long timeStartRender = System.nanoTime();
		
		int tileSize = VisionProcessing.defaultProcessing.conf.tileSize;
		
		// make a shallow copy of all of the drawn instance variables
		BufferedImage b = image;
		ResultImage r = resultImage;
		Point2d[] c = targetCorners;
		RegressionResult[] l = targetSides;
		
		// if there isn't even an image, return
		if (b == null)
			return;
		
		// draw the image
		g.drawImage(b, 0, 0, null);

		
		// draw the green-and-red tiled "isTarget" mask
		if (r != null) {
			Color yes = new Color(0, 255, 0, 170);
			Color no = new Color(255, 0, 0, 70);
			
			
			for (int i = 0; i < r.sW; i++) {
				for (int j = 0; j < r.sH; j++) {
					g.setColor(r.results[i + j * r.sW].hasPlus() ? yes : no);
					
					g.fillRect(i * tileSize, j
							* tileSize,
							tileSize - 1,
							tileSize - 1);
				}
			}
		}
		
		// draw the target corners
		if (c != null) {
			g.setColor(Color.blue);
			int hr = 5;
			for (int i = 0; i < c.length; i++) {
				if (c[i] == null) {
					continue;
				}
				g.fillOval((int) (c[i].x - hr), (int) (c[i].y - hr), hr * 2, hr * 2);
			}
		}
		
		// draw the target edges
		if (l != null) {
			g.setColor(Color.blue);
			for (int i = 0; i < l.length; i++) {
				if (l[i] == null) {
					continue;
				}
				if (l[i] instanceof BackwardsRegressionResult) {
					g.drawLine((int) l[i].b, 0, (int) (l[i].m * 480 + l[i].b), 480);
				} else {
					g.drawLine(0, (int) l[i].b, 640, (int) (l[i].m * 640 + l[i].b));
				}
			}
		}
		
		System.out.println("Render time = "+(System.nanoTime() - timeStartRender)/1.0e6);
		
		// done painting
		hasPainted = true;
		
	}
}
