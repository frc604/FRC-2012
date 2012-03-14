package com._604robotics.robot2012.vision;

import java.awt.image.Raster;

/**
 * A simple class for accessing 2d data in a 1d array, with bounds checking.
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Img {
	/**
	 * The array of image data
	 */
	int[] dat;
	
	
	/**
	 * The size of the image
	 */
	int w, h;



	/**
	 * A constructor to make an Img
	 * 
	 * @param dat - data array
	 * @param w - width
	 * @param h - height
	 */
	public Img(int[] dat, int w, int h) {
		super();
		this.dat = dat;
		this.w = w;
		this.h = h;
	}

	/**
	 * A constructor to make an Img
	 * 
	 * @param raster - a raster storing original image data
	 * @param buff - an array to store the image data into
	 */
	public Img(Raster raster, int[] buff) {
		w = raster.getWidth();
		h = raster.getHeight();
		dat = buff;
		
		int[] pix = new int[3];
		
		for(int i = 0; i < w; i++) {
			for(int j = 0; j < h; j++) {
				raster.getPixel(i, j, pix);
				
				dat[i + j*w] = ((pix[0])<<16) + ((pix[1])<<8) + pix[2];
			}
		}
	}
	
	
	/**
	 * A constructor to make an Img
	 * 
	 * @param raster - a raster storing original image data
	 */
	public Img(Raster raster) {
		this(raster, new int[raster.getWidth()*raster.getHeight()]);
		
	}

	/**
	 * A constructor to make an Img
	 * 
	 * @param w
	 * @param h
	 */
	public Img(int w, int h) {
		this(new int[w*h], w, h);
	}

	/**
	 * @param x - the X coordinate
	 * @param y - the Y coordinate
	 * @return an integer holding an RGB value
	 */
	public int get(int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) {
			return 0;
		}

		return dat[x + y*w];
	}

	/**
	 * @param x - the X coordinate
	 * @param y - the Y coordinate
	 * @param k - an integer holding an RGB value
	 * @return a boolean if the value was set or not
	 */
	public boolean set(int x, int y, int k) {
		if(x < 0 || y < 0 || x >= w || y >= h) {
			return false;
		}
		dat[x + y*w] = k;
		return false;
	}
}