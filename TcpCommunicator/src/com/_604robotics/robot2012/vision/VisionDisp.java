package com._604robotics.robot2012.vision;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import logging.Logger;

import com._604robotics.robot2012.points.Point2d;
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
	 * Set to true when the vision is lagging
	 */
	private boolean						grayscale = false;
	
	/**
	 * Set to true when the vision output isn't connected to the robot
	 */
	private boolean						disconnected = false;
	
	/**
	 * How well the target is aimed
	 */
	private double aimValue = Double.NaN;
	
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
		
		int tileSize = VisionProcessing.defaultProcessing.conf.getInt("tileSize");
		
		// make a shallow copy of all of the drawn instance variables
		BufferedImage b = image;
		ResultImage r = resultImage;
		Point2d[] c = targetCorners;
		RegressionResult[] l = targetSides;
		
		// if there isn't even an image, return
		if (b == null) {
			// for testing purposes:
			g.clearRect(0, 0, getWidth(), getHeight());
			
			drawDisconnectedIndicator(g);
			
			aimValue = 1.5*(System.currentTimeMillis()%6000 / 600.0 - 5);
			drawAimedIndicator(g);
			
			return;
		}
		
		// draw the image
		g.drawImage(b, 0, 0, null);
		if(grayscale) {
			g.setColor(new Color(.5f, .5f, .5f, .5f));
			g.fillRect(0, 0, b.getWidth(), b.getHeight());
		}

		
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
		
		if(disconnected) {
			drawDisconnectedIndicator(g);
		}
		
		drawAimedIndicator(g);
		
		if(VisionProcessing.defaultProcessing.conf.getBoolean("debug_Print"))
			Logger.log("Render time = "+(System.nanoTime() - timeStartRender)/1.0e6);
		
		// done painting
		hasPainted = true;
	}

	public void drawDisconnectedIndicator(Graphics g) {
		int distBetweenCircles = 20;
		int leftIndent = 15;
		int topIndent = 20;

		long current_long = System.currentTimeMillis()%1000 / 100;
		int  current = (int) (current_long < 5 ? current_long : 10 - current_long);
		
		for(int i = 0; i < 6; i++) {
			if(i==current)
				continue;
			
			drawCircle(g, leftIndent + i*distBetweenCircles, topIndent, 7, new Color(.0f, .0f, .0f, .7f));
		}
		
		
		drawCircle(g, leftIndent + current*distBetweenCircles, topIndent, 10, new Color(1f, 1f, 1f, .7f));
	}

	public void drawAimedIndicator(Graphics g) {
		double x = aimValue;
		
		if(x == x) {
			double greenSize = VisionProcessing.defaultProcessing.conf
					.getDouble("aimedIndicator_greenSize"); // default 3
			double orangeSize = VisionProcessing.defaultProcessing.conf
					.getDouble("aimedIndicator_orangeSize"); // default 5
			int widthOfInch = VisionProcessing.defaultProcessing.conf
					.getInt("aimedIndicator_inchWidth"); //default 40
			double outsideScalingFactor = VisionProcessing.defaultProcessing.conf
					.getDouble("aimedIndicator_outsideScalingFactor"); // default .5
			int widthOfInRangeSize = (int)(widthOfInch*greenSize);
			int widthOfOrangeSize = (int)(widthOfInch*((orangeSize - greenSize) * outsideScalingFactor + greenSize));
			
			int cx = getWidth()/2;
			
			int thisHeight = getHeight();
			
			int xi = (int)(x * widthOfInch);
			
			double absX = Math.abs(x);
			if(absX <= orangeSize || absX <= greenSize) {
				g.setColor(new Color(.0f, .0f, .0f, .3f));
				int aroundMeterBuffer = 15;
				g.fillRoundRect(cx - widthOfOrangeSize - aroundMeterBuffer, thisHeight - 120 - aroundMeterBuffer,
						widthOfOrangeSize*2 + aroundMeterBuffer*2, 80 + aroundMeterBuffer*2,
						10, 10);
			}
			
			
			if(xi > widthOfInRangeSize) {
				xi = (int) ((xi - widthOfInRangeSize) * outsideScalingFactor) + widthOfInRangeSize;
			} else if(xi < -widthOfInRangeSize) {
				xi = (int) ((xi + widthOfInRangeSize) * outsideScalingFactor) - widthOfInRangeSize;
			}
			
			if(absX <= greenSize) {
				g.setColor(Color.green);
			} else if(absX <= orangeSize) {
				g.setColor(Color.orange);
			} else {
				g.setColor(Color.red);
			}
			
			
			if(x < 0) {
				g.fillRect(cx+xi, thisHeight - 105, -xi, 31);
			} else {
				g.fillRect(cx, thisHeight - 105, xi, 31);
			}

			Font font = new Font("Helvetica", Font.BOLD, 40);
			g.setFont(font);
			
			String str = String.format("%.1f", x);
			int strHalfWidth = g.getFontMetrics().stringWidth(str)/2;
			int strOffset = cx - strHalfWidth;
			
			
			g.drawString(str, strOffset, thisHeight - 40);
			

			g.setColor(Color.blue);
			g.drawLine(cx, thisHeight - 120, cx, thisHeight - 75);
			
			g.setColor(Color.green);
			g.drawLine(cx + widthOfInRangeSize, thisHeight - 120, cx + widthOfInRangeSize, thisHeight - 60);
			g.drawLine(cx - widthOfInRangeSize, thisHeight - 120, cx - widthOfInRangeSize, thisHeight - 60);

			if(orangeSize > 0) {
				g.setColor(Color.red);
				g.drawLine(cx + widthOfOrangeSize, thisHeight - 120, cx + widthOfOrangeSize, thisHeight - 60);
				g.drawLine(cx - widthOfOrangeSize, thisHeight - 120, cx - widthOfOrangeSize, thisHeight - 60);
			}
		}
	}
	
	private void drawCircle(Graphics g, int x, int y, int r, Color color) {
		int x1 = x-r, y1 = y-r;
		
		g.setColor(color);
		g.fillOval(x1, y1, r*2, r*2);
		g.setColor(Color.gray);
		g.drawOval(x1, y1, r*2, r*2);
	}

	public void setGrayscale(boolean val) {
		grayscale = val;
		repaint();
	}

	public void setDisconnected(boolean val) {
		if(val == disconnected)
			return;
		disconnected = val;
		repaint();
	}

	public void setAimValue(double x) {
		aimValue = x;
	}
}
