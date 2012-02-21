package com._604robotics.robot2012.vision;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import com._604robotics.robot2012.vision.LinearRegression.BackwardsRegressionResult;
import com._604robotics.robot2012.vision.LinearRegression.RegressionResult;


public class Disp extends JFrame {
	
	/**
	 * 
	 */
	private static final long			serialVersionUID	= -2167719831931210343L;
	
	BufferedImage						bi;
	
	Point2d[]							corners;
	boolean								hasPainted			= false;
	
	LinearRegression.RegressionResult[]	lines;
	ResultImage							res;
	
	public Disp() {
		super("Vision");
		
		this.setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void paint(Graphics g) {
		BufferedImage b = bi;
		ResultImage r = res;
		Point2d[] c = corners;
		RegressionResult[] l = lines;
		
		if (b == null || r == null)
			return;
		
		g.drawImage(b, 0, 0, null);
		
		Color yes = new Color(0, 255, 0, 170);
		Color no = new Color(255, 0, 0, 70);
		
		for (int i = 0; i < r.sW; i++) {
			for (int j = 0; j < r.sH; j++) {
				g.setColor(r.results[i + j * r.sW].hasPlus() ? yes : no);
				
				g.fillRect(i * VisionProcessing.Step, j * VisionProcessing.Step, VisionProcessing.Step - 1, VisionProcessing.Step - 1);
			}
		}
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
		hasPainted = true;
		
	}
}
