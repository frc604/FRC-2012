/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012.balancing;

/**
 * Utility class for automated balancing assistance.
 * 
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Balancing {
    
    /**
     * Given a specific gyro reading, returns what speed you should be going
     * at.
     * 
     * @param   balGyroReading      A gyro reading.
     * 
     * @return  The speed you should going at.
     */
    public static double getSpeedforBalance(double balGyroReading) {
        double speed = .2;
        double angles = 12;
        
        if(balGyroReading < -angles)
            return -speed;
        if(balGyroReading > angles)
            return -speed;
        return 0;
    }
    
}
