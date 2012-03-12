/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012.balancing;

/**
 *
 * @author kevin
 */
public class Balancing {
    
    public double getSpeedforBalance(double balGyroReading) {
        double speed = .2;
        double angles = 12;
        
        if(balGyroReading < -angles)
            return -speed;
        if(balGyroReading > angles)
            return -speed;
        return 0;
    }
    
}
