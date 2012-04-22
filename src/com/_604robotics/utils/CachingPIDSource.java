package com._604robotics.utils;

import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class CachingPIDSource implements PIDSource {
    private double cache = 0D;
    
    public void cache (double cache) {
        this.cache = cache;
    }
    
    public double pidGet () {
        return this.cache;
    }
}
