package com._604robotics.robot2012.physics;

/**
 * Class representing info for firing a ball.
 * 
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 */
public class BallFireInfo {
    public ShooterAnglePick angle;
    public double speed;
    public double horizontalAngle;

    /**
     * Initializes a new BallFireInfo.
     * 
     * @param   angle               An angle.
     * @param   speed               A speed.
     * @param   horizontalAngle     A horizontal angle.
     */
    public BallFireInfo(ShooterAnglePick angle, double speed, double horizontalAngle) {
        this.angle = angle;
        this.speed = speed;
        this.horizontalAngle = horizontalAngle;
    }
}