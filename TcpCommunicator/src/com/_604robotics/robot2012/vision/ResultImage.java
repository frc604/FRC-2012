package com._604robotics.robot2012.vision;

import static java.lang.Math.max;

import com._604robotics.robot2012.vision.Result.AntiResult;
import com._604robotics.robot2012.vision.Result.PlusResult;
import com._604robotics.robot2012.vision.config.Config;

public class ResultImage {

	int imW, imH;
	int sW, sH;
	int stepW = VisionProcessing.defaultProcessing.conf.tileSize, stepH = VisionProcessing.defaultProcessing.conf.tileSize;
	public Result[] results;

	public ResultImage(int imW, int imH) {
		this.imW = imW;
		this.imH = imH;

		sW = (imW  - 1)/stepW +1;
		sH = (imH  - 1)/stepH +1;

		results = new Result[sW*sH];
	}

	public void computeResults(Img img) {
		for(int i = 0; i*stepW < imW; i++) {
			for(int j = 0; j*stepH < imH; j++) {
				int val = -128;
				
				boolean scanWholeTile = VisionProcessing.defaultProcessing.conf.scanWholeTile;
				
				if(!scanWholeTile) {
					int color = img.get(i*stepW, j*stepH);
					val = getVal(color);
					color = img.get((i+1)*stepW-1, (j)*stepH);
					val = max(getVal(color), val);
					color = img.get((i+1)*stepW, (j+1)*stepH-1);
					val = max(getVal(color)-1, val);
					color = img.get((i)*stepW, (j+1)*stepH-1);
					val = max(getVal(color), val);
					
					if(VisionProcessing.defaultProcessing.conf.checkCenter) {
						color = img.get((i)*stepW + stepW/2, (j+1)*stepH-1 + stepH/2);
						val = max(getVal(color), val);
					}
				}

				Result result = null;

				if(scanWholeTile || val > VisionProcessing.defaultProcessing.conf.sensitivity) {
					result = iterate(img, i, j);
				} else {
					result = new Result.AntiResult();
				}

				results[i + j*sW] = result;

			}
		}
	}

	private Result iterate(Img img, int i, int j) {
		byte[] l_results = new byte[stepW*stepH];

		boolean hadMatch = false;

		for(int l = 0; l < stepW; l++) {
			for(int m = 0; m < stepH; m++) {
				int l_color = img.get(i*stepW + m,  j*stepH + l);
				
				int val = getVal(l_color);
				
				l_results[l + m*stepW] = (byte) val;

				if(val > VisionProcessing.defaultProcessing.conf.sensitivity) {
					hadMatch = true;
				}
			}
		}
		if(!hadMatch)
			return new Result.AntiResult();
		return new Result.PlusResult(stepW, l_results);
	}
	

	private byte getVal(int color) {
		Config conf = VisionProcessing.defaultProcessing.conf;
		
		
		int r = (color & 0xFF0000)	>>> 16;
		int g = (color & 0xFF00)	>>> 8;
		int b = (color & 0xFF);

		//double val = b*.5 + .1*g - .8*r;//TODO - this may need moar tuning

		double dr = r - conf.color_targetR;
		double dg = g - conf.color_targetG;
		double db = b - conf.color_targetB;
		
		double val = 127 - dr*dr*conf.color_mulR - dg*dg*conf.color_mulG - db*db*conf.color_mulB;
		
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
	 * @param a - number
	 * @param exp - exponent
	 * @return a rapid approximation of a^exp
	 */
	private static double fastPow(double a, double exp) {
	    final long tmp = Double.doubleToLongBits(a);
	    final long tmp2 = (long)(exp * (tmp - 4606921280493453312L)) + 4606921280493453312L;
	    return Double.longBitsToDouble(tmp2);
	}

	public boolean isTarget(int i, int j) {
		if(i < 0 || j < 0 || i >= imW || j >= imH)
			return false;

		int i_major = i/stepW;
		int j_major = j/stepW;

		int i_minor = i%stepW;
		int j_minor = j%stepW;
		
		return results[i_major + j_major*sW].plusAt(i_minor, j_minor);
	}
}