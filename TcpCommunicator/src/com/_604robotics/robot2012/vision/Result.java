package com._604robotics.robot2012.vision;

/**
 * TODO - javadoc
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public abstract class Result {
	public boolean hasPlus() {
		return false;
	}
	public boolean plusAt(int x, int y) {
		return false;
	}
	

	public static class AntiResult extends Result {
	}
	public static class PlusResult extends Result {
		int w;
		byte[] dat;
	
		public PlusResult(int w, byte[] dat) {
			this.w = w;
			this.dat = dat;
		}
	
		public boolean hasPlus() {
			return true;
		}
		public boolean plusAt(int x, int y) {
			return dat[x + w*y] > VisionProcessing.defaultProcessing.conf.sensitivity;
		}
	
	}
}