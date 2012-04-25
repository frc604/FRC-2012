/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com._604robotics.robot2012.firing;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class EncoderSpeedsForDist {
    
    private static final double switchDist = 100;
    
    private static final EncoderSpeedsForDist inMapping = new EncoderSpeedsForDist();
    private static final EncoderSpeedsForDist bounceMapping = new EncoderSpeedsForDist();
    
    //y = 1.169E-07x4 + 9.779E-05x3 - 2.648E-02x2 + 3.325E+00x + 8.691E+01
    private double   _quart = 1.169E-07,
                     _cubic = 9.779E-05,
                     _quad  = - 2.648E-02,
                     _linear= 3.325E+00,
                     _const = 8.691E+01;
    
    static {
        //y = 1.169E-07x4 + 1.114E-04x3 - 1.738E-02x2 + 2.047E+00x + 1.635E+02
        inMapping._quart = 1.169E-07;
        inMapping._cubic = 1.114E-04;
        inMapping._quad  = - 1.738E-02;
        inMapping._linear= 2.047E+00;
        inMapping._const = 1.635E+02;
        
        
        //y = 1.169E-07x4 + 9.779E-05x3 - 2.648E-02x2 + 3.325E+00x + 8.691E+01
        bounceMapping._quart = 1.169E-07;
        bounceMapping._cubic = 9.779E-05;
        bounceMapping._quad  = 2.648E-02;
        bounceMapping._linear= 3.325E+00;
        bounceMapping._const = 8.691E+01;
    }
    
    
    public static double getSpeedForDist(double dist, EncoderSpeedsForDist mapping) {
        return mapping._quart     * fastPow(dist, 4)
               +mapping._cubic    * fastPow(dist, 3)
               +mapping._quad     * fastPow(dist, 2)
               +mapping._linear   * dist
               +mapping._const;
    }
    
    
    public static double getSpeedForDist(double dist) {
        return getSpeedForDist(dist, dist < switchDist ? bounceMapping : inMapping);
    }
    
    
	
	/**
	 * Adapted from {@link http://martin.ankerl.com/2007/10/04/optimized-pow-approximation-for-java-and-c-c/}
	 * 
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
}
