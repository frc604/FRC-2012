package com._604robotics.robot2012.vision;

import java.awt.image.Raster;

public class Img {
	int[] dat;
	int w, h;



	/**
	 * @param dat
	 * @param w
	 * @param h
	 */
	public Img(int[] dat, int w, int h) {
		super();
		this.dat = dat;
		this.w = w;
		this.h = h;
	}

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
	public Img(Raster raster) {
		this(raster, new int[raster.getWidth()*raster.getHeight()]);
		
	}

	/**
	 * @param w
	 * @param h
	 */
	public Img(int w, int h) {
		this(new int[w*h], w, h);
	}

	public int get(int x, int y) {
		if(x < 0 || y < 0 || x >= w || y >= h) {
			return 0;
		}

		return dat[x + y*w];
	}

	public boolean set(int x, int y, int k) {
		if(x < 0 || y < 0 || x >= w || y >= h) {
			return false;
		}
		dat[x + y*w] = k;
		return false;
	}
}