/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com._604robotics.robot2012;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author aaron
 */
public class PIDDriveEncoderDifference implements PIDSource {
    
    private Encoder e1,e2;
    
    public PIDDriveEncoderDifference(Encoder e1,Encoder e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public double pidGet() {
        return e1.get()-e2.get();
    }
    
}
