package com._604robotics.robot2012.physics;

public class BallFireInfo {
    public ShooterAnglePick angle;
    public double speed;
    public double horizontalAngle;

    /**
     * @param angle
     * @param speed
     */
    public BallFireInfo(ShooterAnglePick angle, double speed, double horizontalAngle) {
        super();
        this.angle = angle;
        this.speed = speed;
        this.horizontalAngle = horizontalAngle;
    }
}