package com._604robotics.robot2012.physics;

/**
 * Enum-ish thing of angles to shoot at.
 * 
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class ShooterAnglePick {
    // TODO - fix number values
    public static final ShooterAnglePick shooterAnglePickTop = new ShooterAnglePick(50);//given in degrees
    public static final ShooterAnglePick shooterAnglePickBottom = new ShooterAnglePick(70);//given in degrees
    public final double angleDeg, angleRad, angleSlope;
    
    /**
     * Initializes a new ShooterAnglePick.
     * 
     * @param   angleDeg    An angle, in degrees.
     */
    public ShooterAnglePick(double angleDeg) {
        this.angleDeg = angleDeg;
        angleRad = angleDeg * Math.PI / 180;
        angleSlope = Math.tan(angleRad);
    }
}