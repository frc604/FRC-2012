package com._604robotics.robot2012.firing;

import com._604robotics.robot2012.configuration.FiringConfiguration;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class EncoderSpeedsForDist {
   
    private static boolean useDemoHeight = false;
    private static final double switchDist = 72;
    
    private static final EncoderSpeedsForDist inMapping = new EncoderSpeedsForDist();
    private static final EncoderSpeedsForDist bounceMapping = new EncoderSpeedsForDist();
    private static final EncoderSpeedsForDist demoBounceMapping = new EncoderSpeedsForDist();
    private static final EncoderSpeedsForDist demoInMapping = new EncoderSpeedsForDist();
    
    //y = 1.169E-07x4 + 9.779E-05x3 - 2.648E-02x2 + 3.325E+00x + 8.691E+01
    private double   _quart = 1.169E-07,
                     _cubic = 9.779E-05,
                     _quad  = - 2.648E-02,
                     _linear= 3.325E+00,
                     _const = 8.691E+01;
    
    public static void setUseDemoHeight() {
        setUseDemoHeight(true);
    }
    
    public static void setUseDemoHeight(boolean flag) {
        useDemoHeight = flag;
    }
    
    public static boolean getUseDemoHeight() {
        return useDemoHeight;
    }
    
    static {
        //y = 1.169E-07x4 + 1.114E-04x3 - 1.738E-02x2 + 2.047E+00x + 1.635E+02
        inMapping._quart = 1.169E-07;
        inMapping._cubic = 1.114E-04;
        inMapping._quad  = - 1.738E-02;
        inMapping._linear= 2.047E+00;
        inMapping._const = 1.635E+02;
        
        
        //y = 1.169E-07x4 + 9.779E-05x3 - 2.648E-02x2 + 3.325E+00x + 8.691E+01
        bounceMapping._quart = 3E-07;
        bounceMapping._cubic = 1.25E-04;
        bounceMapping._quad  = -1.8E-02;
        bounceMapping._linear= 1.4E+00;
        bounceMapping._const = 2.17E+02;
        
        //y = 2.260E+00x + 1.348E+02
        demoBounceMapping._quart = 0;
        demoBounceMapping._cubic = 0;
        demoBounceMapping._quad  = 0;
        demoBounceMapping._linear= 2.260E+00;
        demoBounceMapping._const = 1.348E+02;
        
        //y = -5.434E-04x3 + 6.005E-02x2 + 3.471E-01x + 1.510E+02
        demoInMapping._quart = 0;
        demoInMapping._cubic = -5.434E-04;
        demoInMapping._quad  = 6.005E-02;
        demoInMapping._linear= 3.471E-01;
        demoInMapping._const = 1.510E+02;

    }
    
    
    public static double getSpeedForDist(double dist, EncoderSpeedsForDist mapping) {
        double ret =  mapping._quart     * fastPow(dist, 4)
               +mapping._cubic    * fastPow(dist, 3)
               +mapping._quad     * fastPow(dist, 2)
               +mapping._linear   * dist
               +mapping._const;
        
        System.out.println("dist: " + dist);
        System.out.println("ret: " + ret);
        
        if(ret > FiringConfiguration.MAX_SPEED)
            ret = FiringConfiguration.MAX_SPEED;
        
        return ret;
    }
    
    
    public static double getSpeedForDist(double dist) {
        if(useDemoHeight)
            return 1 * getSpeedForDist(dist, demoBounceMapping);
        return 1.1 * getSpeedForDist(dist, dist < switchDist ? bounceMapping : inMapping);
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
