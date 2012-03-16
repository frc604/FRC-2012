package com._604robotics.robot2012.vision;

import static java.lang.Math.max;

import com._604robotics.robot2012.vision.config.Config;

/**
 * A result image that holds an image of how well pixels match the expected color of the vision target.
 * It is treated like a giant boolean array externally, but internally it is split up into small tiles.
 * 
 * @see Result
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class ResultImage {

	/**
	 * The size of the whole image, in pixels
	 */
	int imW, imH;
	
	/**
	 * The size of the whole image, in tiles
	 */
	int sW, sH;
	
	
	/**
	 * The size of each tile
	 */
	int tileSize = VisionProcessing.defaultProcessing.conf.getInt("tileSize");
	public Result[] results;

	/**
	 * A constructor to create a new ResultImage. To actually initialize the returned ResultImage, use {@link ResultImage}
	 * 
	 * @param imW	the width of the image
	 * @param imH	the height of the image
	 */
	public ResultImage(int imW, int imH) {
		this.imW = imW;
		this.imH = imH;

		sW = (imW  - 1)/tileSize +1;
		sH = (imH  - 1)/tileSize +1;

		results = new Result[sW*sH];
	}

	/**
	 * This method goes through an {@link Img} and finds which pixels appear to match the color of the
	 * vision target.
	 * 
	 * @param img	the image to process and find matching Target-colored pixels
	 */
	public void computeResults(Img img) {
		Config conf = VisionProcessing.defaultProcessing.conf;
		
		int sensitivity = conf.getInt("sensitivity");
		Result.sensitivity = sensitivity;
		boolean scanWholeTile = conf.getBoolean("scanWholeTile");
		boolean checkCenter = conf.getBoolean("checkCenter");

		color_targetR = conf.getInt("color_targetR");
		color_targetG = conf.getInt("color_targetG");
		color_targetB = conf.getInt("color_targetB");
		
		color_mulR = conf.getDouble("color_mulR");
		color_mulG = conf.getDouble("color_mulG");
		color_mulB = conf.getDouble("color_mulB");
		
		
		//iterate through all of the Result tiles and initialize them
		for(int i = 0; i*tileSize < imW; i++) {
			for(int j = 0; j*tileSize < imH; j++) {
				int val = -128;
				
				
				//if not scanning the whole tile, check the corners and center
				if(!scanWholeTile) {
					int color = img.get(i*tileSize, j*tileSize);
					val = getVal(color);
					color = img.get((i+1)*tileSize-1, (j)*tileSize);
					val = max(getVal(color), val);
					color = img.get((i+1)*tileSize, (j+1)*tileSize-1);
					val = max(getVal(color)-1, val);
					color = img.get((i)*tileSize, (j+1)*tileSize-1);
					val = max(getVal(color), val);
					
					if(checkCenter) {
						color = img.get((i)*tileSize + tileSize/2, (j+1)*tileSize-1 + tileSize/2);
						val = max(getVal(color), val);
					}
				}

				Result result = null;

				//if there was a match in the corners/center of this tile, or if the whole tile is Config'd to scan
				if(scanWholeTile || val > sensitivity) {
					result = iterate(img, i, j);
				} else {
					result = new Result.AntiResult();
				}

				results[i + j*sW] = result;

			}
		}
	}

	/**
	 * Scans an entire tile and returns the {@link Result}
	 * 
	 * @param img	the image to scan
	 * @param x	the tile X coordinate
	 * @param y	the tile Y coordinate
	 * @return the {@link Result}
	 */
	private Result iterate(Img img, int x, int y) {
		byte[] l_results = new byte[tileSize*tileSize];

		boolean hadMatch = false;

		for(int l = 0; l < tileSize; l++) {
			for(int m = 0; m < tileSize; m++) {
				int l_color = img.get(x*tileSize + m,  y*tileSize + l);
				
				int val = getVal(l_color);
				
				l_results[l + m*tileSize] = (byte) val;

				if(val > Result.sensitivity) {
					hadMatch = true;
				}
			}
		}
		if(!hadMatch)
			return new Result.AntiResult();
		return new Result.PlusResult(tileSize, l_results);
	}

	/**
	 * The expected color of the target
	 */
	private double color_targetR, color_targetG, color_targetB;
	
	
	/**
	 * How much to multiply the square of the errors per color channel by
	 */
	private double color_mulR, color_mulG, color_mulB;

	private byte getVal(int color) {
		int r = (color & 0xFF0000)	>>> 16;
		int g = (color & 0xFF00)	>>> 8;
		int b = (color & 0xFF);

		//double val = b*.5 + .1*g - .8*r;//TODO - this may need moar tuning

		double dr = r - color_targetR;
		double dg = g - color_targetG;
		double db = b - color_targetB;
		
		double val = 127 - dr*dr*color_mulR - dg*dg*color_mulG - db*db*color_mulB;
		
		if(val < -128)
			return -128;
		if(val > 127)
			return 127;

		return (byte)val;
		
		/*
		if(r > 20)
			return -128;
		if(b> 240 && g > 80) {
			System.out.println(r + "\t"+ g +"\t"+b);
			return 127;
		}
		return -128;
		
		// */
	}
	
	/**
	 * Adapted from {@link http://martin.ankerl.com/2007/10/04/optimized-pow-approximation-for-java-and-c-c/}
	 * 
	 * This is currently unused; in the future, it might be used in the getVal() function.
	 * 
	 * @param a	number
	 * @param exp	exponent
	 * @return a rapid approximation of a^exp
	 */
	private static double fastPow(double a, double exp) {
	    final long tmp = Double.doubleToLongBits(a);
	    final long tmp2 = (long)(exp * (tmp - 4606921280493453312L)) + 4606921280493453312L;
	    return Double.longBitsToDouble(tmp2);
	}

	/**
	 * @param x	The X coordinate, in pixels
	 * @param y	The Y coordinate, in pixels
	 * @return	
	 */
	public boolean isTarget(int x, int y) {
		if(x < 0 || y < 0 || x >= imW || y >= imH)
			return false;

		int i_major = x/tileSize;
		int j_major = y/tileSize;

		int i_minor = x%tileSize;
		int j_minor = y%tileSize;
		
		return results[i_major + j_major*sW].plusAt(i_minor, j_minor);
	}
}