package com._604robotics.robot2012.vision;

/**
 * This class stores one tile of "is in target" data. If there are no matches for the target, a {@link AntiResult} is used.
 * If there are matching pixels, a {@link PlusResult} is used.
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public abstract class Result {
	/**
	 * The sensitivity of color acceptance (from -128 to +127)
	 */
	static int sensitivity;
	
	
	/**
	 * @return whether there are any pixels matching the color of the target or not
	 */
	public boolean hasPlus() {
		return false;
	}
	
	/**
	 * @param x	the X coordinate (within the tile, not the image)
	 * @param y	the Y coordinate (within the tile, not the image)
	 * @return whether or not the pixel at the given location matches the Target color
	 */
	public boolean plusAt(int x, int y) {
		return false;
	}
	

	/**
	 * A result indicating that it is unlikely that the target lies in the indicated tile
	 *
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	public static class AntiResult extends Result {
		
	}

	/**
	 * A result indicating that it is likely that the target lies in the indicated tile
	 *
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	public static class PlusResult extends Result {
		/**
		 * The size of this tile
		 */
		int tileSize;
		
		/**
		 * An array of bytes that hold values for how likely it is that the target lies in a given pixel in the target.
		 * The sensitivity can be tuned with {@link Result.sensitivity}, which is set from the value in the {@link Config}
		 */
		byte[] dat;
	
		/**
		 * A simple constructor to make a PlusResult.
		 * 
		 * @param tileSize	the size of this tile
		 * @param dat	the array of bytes indicating how well the pixel matches the target.
		 */
		public PlusResult(int tileSize, byte[] dat) {
			this.tileSize = tileSize;
			this.dat = dat;
		}
	
		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.Result#hasPlus()
		 */
		public boolean hasPlus() {
			return true;
		}
		
		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.Result#plusAt(int, int)
		 */
		public boolean plusAt(int x, int y) {
			return dat[x + tileSize*y] > sensitivity;
		}
	
	}
}