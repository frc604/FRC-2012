package edu.wpi.first.wpilibj;

public class GyroHax extends Gyro {
    /**
     * Initializes a new GyroHax on the specified PWM port.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   port    The PWM port the gyro is plugged into. Must be 1 or 2!
     */
    public GyroHax (int port) {
        super(port);
    }
    
    /**
     * Initializes a new GyroHax on the specified PWM port on the specified
     * module port.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   slot    The module slot the gyro is plugged into.
     * @param   port    The PWM port the gyro is plugged into. Must be 1 or 2!
     */
    public GyroHax (int slot, int port) {
        super(slot, port);
    }
    
    /**
     * Initializes a new GyroHax on the specified AnalogChannel.
     * 
     * Note that port must be 1 or 2!
     * 
     * @param   channel The AnalogChannel the gyro is plugged into.
     */
    public GyroHax (AnalogChannel channel) {
        super(channel);
    }
    
    public AnalogChannel getAnalogChannel() {
        return this.m_analog;
    }
}
