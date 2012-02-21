package com._604robotics.robot2012.vision;

import static java.lang.Math.max;

import com._604robotics.robot2012.vision.Result.AntiResult;
import com._604robotics.robot2012.vision.Result.PlusResult;

public class ResultImage {

	int imW, imH;
	int sW, sH;
	int stepW = VisionProcessing.Step, stepH = VisionProcessing.Step;
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
				int val;
				if(!VisionProcessing.MegaScan) {
					int color = img.get(i*stepW, j*stepH);
					val = getVal(color);
					color = img.get((i+1)*stepW-1, (j)*stepH);
					val = max(getVal(color), val);
					color = img.get((i+1)*stepW, (j+1)*stepH-1);
					val = max(getVal(color)-1, val);
					color = img.get((i)*stepW, (j+1)*stepH-1);
					val = max(getVal(color), val);
					
					if(VisionProcessing.CheckCenter) {
						color = img.get((i)*stepW + stepW/2, (j+1)*stepH-1 + stepH/2);
						val = max(getVal(color), val);
					}
				}

				Result result = null;

				if(VisionProcessing.MegaScan || val > VisionProcessing.Sensitivity) {
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

				if(val > VisionProcessing.Sensitivity) {
					hadMatch = true;
				}
			}
		}
		if(!hadMatch)
			return new Result.AntiResult();
		return new Result.PlusResult(stepW, l_results);
	}

	private byte getVal(int color) {
		int r = (color & 0xFF0000)	>>> 16;
		int g = (color & 0xFF00)		>>> 8;
		int b = (color & 0xFF);

		//*
		double val = b*.5 + .1*g - .8*r;//TODO - this may need moar tuning

		if(val < -128)
			return -128;
		if(val > 127)
			return 127;

		return (byte)val;
		/*/
		
		if(r > 20)
			return -128;
		if(b> 240 && g > 80) {
			System.out.println(r + "\t"+ g +"\t"+b);
			return 127;
		}
		return -128;
		
		// */
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