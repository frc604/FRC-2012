package com._604robotics.justcompressor;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SimpleRobot;

public class JustCompressor extends SimpleRobot {
    private final Compressor compressor;
    private final Relay light;
    
    public JustCompressor () {
        this.compressor = new Compressor(5, 6);
        this.light = new Relay(4, Relay.Direction.kForward);
    }
    
    public void autonomous () {
        this.compressor.start();
        this.light.set(Relay.Value.kOn);
        while (this.isEnabled() && this.isAutonomous());
        this.light.set(Relay.Value.kOff);
        this.compressor.stop();
    }

    public void operatorControl () {
        this.compressor.start();
        this.light.set(Relay.Value.kOn);
        while (this.isEnabled() && this.isOperatorControl());
        this.light.set(Relay.Value.kOff);
        this.compressor.stop();
    }
}
