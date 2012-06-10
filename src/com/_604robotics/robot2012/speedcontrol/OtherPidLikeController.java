package com._604robotics.robot2012.speedcontrol;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.parsing.IUtility;
import edu.wpi.first.wpilibj.util.BoundaryException;
import java.util.TimerTask;

//the following import would help, but refuses to resolve:
//import org.human.sentience.*;

/**
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class OtherPidLikeController implements IUtility {

    public static final double kDefaultPeriod = .05;

    private double m_P;			// factor for "proportional" control
    private double m_I;			// factor for "integral" control
    private double m_D;			// factor for "derivative" control
    private double m_C;		// factor for compression
    
    private double m_maximumOutput = 1.0;	// |maximum output|
    private double m_minimumOutput = -1.0;	// |minimum output|
    private double m_maximumInput = 0.0;		// maximum input - limit setpoint to this
    private double m_minimumInput = 0.0;		// minimum input - limit setpoint to this
    private boolean m_continuous = false;	// do the endpoints wrap around? eg. Absolute encoder
    private boolean m_enabled = false; 			//is the pid controller enabled
    private double m_prevError = 0.0;	// the prior sensor input (used to compute velocity)
    private double m_totalError = 0.0; //the sum of the errors for use in the integral calc
    private double m_tolerance = 0.05;	//the percetage error that is considered on target
    private double m_setpoint = 0.0;
    private double m_error = 0.0;
    private double m_result = 0.0;
    private double m_period = kDefaultPeriod;
    PIDSource m_pidInput;
    PIDOutput m_pidOutput;
    java.util.Timer m_controlLoop;

    private class PIDTask extends TimerTask {

        private OtherPidLikeController m_controller;

        public PIDTask(OtherPidLikeController controller) {
            if (controller == null) {
                throw new NullPointerException("Given PIDController was null");
            }
            m_controller = controller;
        }

        public void run() {
            m_controller.calculate();
        }
    }

    /**
     * Allocate a PID object with the given constants for P, I, D, C
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param Kc the compression
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output value
     * @param period the loop time for doing calculations. This particularly effects calculations of the
     * integral and differential terms. The default is 50ms.
     */
    public OtherPidLikeController(double Kp, double Ki, double Kd, double Kc,
            PIDSource source, PIDOutput output,
            double period) {

        if (source == null)
            throw new NullPointerException("Null PIDSource was given");
        if (output == null)
            throw new NullPointerException("Null PIDOutput was given");


        m_controlLoop = new java.util.Timer();


        m_P = Kp;
        m_I = Ki;
        m_D = Kd;
        m_C = Kc;

        m_pidInput = source;
        m_pidOutput = output;
        m_period = period;

        m_controlLoop.schedule(new OtherPidLikeController.PIDTask(this), 0L, (long) (m_period * 1000));
    }

    /**
     * Allocate a PID object with the given constants for P, I, D, C, using a 50ms period.
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param Kc the compression
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output value
     */
    public OtherPidLikeController(double Kp, double Ki, double Kd, double Kc,
            PIDSource source, PIDOutput output) {
        this(Kp, Ki, Kd, Kc, source, output, kDefaultPeriod);
    }

    /**
     * Free the PID object
     */
    public void free() {
        m_controlLoop.cancel();
        m_controlLoop = null;
    }

    /**
     * Read the input, calculate the output accordingly, and write to the output.
     * This should only be called by the PIDTask
     * and is created during initialization.
     */
    private void calculate() {
        boolean enabled;
        PIDSource pidInput;

        synchronized (this) {
            if (m_pidInput == null) {
                return;
            }
            if (m_pidOutput == null) {
                return;
            }
            enabled = m_enabled; // take snapshot of these values...
            pidInput = m_pidInput;
        }
        
        if (enabled) {
            double input = pidInput.pidGet();
            double result;
            PIDOutput pidOutput = null;

            synchronized (this) {
                m_error = m_setpoint - input;
                if (m_continuous) {
                    if (Math.abs(m_error)
                            > (m_maximumInput - m_minimumInput) / 2) {
                        if (m_error > 0) {
                            m_error = m_error - m_maximumInput + m_minimumInput;
                        } else {
                            m_error = m_error
                                    + m_maximumInput - m_minimumInput;
                        }
                    }
                }

                if (((m_totalError + m_error) * m_I < m_maximumOutput)
                        && ((m_totalError + m_error) * m_I > m_minimumOutput)) {
                    m_totalError += m_error;
                }
                
                double dError = m_error - m_prevError;
                
                // P: -0.0010
                // I: 0.0
                // D: -0.0010
                // DP: 0.0
                double pTerm = m_P * m_error;
                double iTerm = m_I * m_totalError;
                double dTerm = m_D * dError;
                
                pTerm = compress(pTerm);
                iTerm = compress(iTerm);
                dTerm = compress(dTerm);
                

                m_result = (pTerm + iTerm + dTerm);
                m_prevError = m_error;

                if (m_result > m_maximumOutput) {
                    m_result = m_maximumOutput;
                } else if (m_result < m_minimumOutput) {
                    m_result = m_minimumOutput;
                }
                pidOutput = m_pidOutput;
                result = m_result;
            }
            
            pidOutput.pidWrite(result);
        }
    }

    /**
     * Set the PIDDP Controller gain parameters.
     * Set the proportional, integral, and differential coefficients.
     * @param p Proportional coefficient
     * @param i Integral coefficient
     * @param d Differential coefficient
     * @param dp Differential/Proportional coefficient
     */
    public synchronized void setPIDC(double p, double i, double d, double c) {
        m_P = p;
        m_I = i;
        m_D = d;
        m_C = c;
    }
    
    /**
     * Set the PID Controller gain parameters.
     * Set the proportional, integral, and differential coefficients.
     * @param p Proportional coefficient
     * @param i Integral coefficient
     * @param d Differential coefficient
     */
    public synchronized void setPID(double p, double i, double d) {
        m_P = p;
        m_I = i;
        m_D = d;
    }

    /**
     * Get the Proportional coefficient
     * @return proportional coefficient
     */
    public double getP() {
        return m_P;
    }

    /**
     * Get the Integral coefficient
     * @return integral coefficient
     */
    public double getI() {
        return m_I;
    }

    /**
     * Get the Differential coefficient
     * @return differential coefficient
     */
    public synchronized double getD() {
        return m_D;
    }
    
    /**
     * Get the compression
     * @return compression
     */
    public synchronized double getC() {
        return m_C;
    }
    

    /**
     * Set the Proportional coefficient
     */
    public void setP(double p) {
        m_P = p;
    }

    /**
     * Set the Integral coefficient
     */
    public void setI(double i) {
        m_I = i;
    }

    /**
     * Set the Differential coefficient
     */
    public void setD(double d) {
        m_D = d;
    }
    
    /**
     * Set the compression
     */
    public void setC(double c) {
        m_C = c;
    }
    
    
    public double compress(double value) {
        
        double powVal = MathUtils.pow(-Math.abs(value), m_C);
        
        if(value > 0)
            return (1-powVal);
        else
            return (powVal-1);
    }
    

    /**
     * Return the current PID result
     * This is always centered on zero and constrained the the max and min outs
     * @return the latest calculated output
     */
    public synchronized double get() {
        return m_result;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     * @param continuous Set to true turns on continuous, false turns off continuous
     */
    public synchronized void setContinuous(boolean continuous) {
        m_continuous = continuous;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     */
    public synchronized void setContinuous() {
        this.setContinuous(true);
    }

    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param minimumInput the minimum value expected from the input
     * @param maximumInput the maximum value expected from the output
     */
    public synchronized void setInputRange(double minimumInput, double maximumInput) {
        if (minimumInput > maximumInput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumInput = minimumInput;
        m_maximumInput = maximumInput;
        setSetpoint(m_setpoint);
    }

    /**
     * Sets the minimum and maximum values to write.
     *
     * @param minimumOutput the minimum value to write to the output
     * @param maximumOutput the maximum value to write to the output
     */
    public synchronized void setOutputRange(double minimumOutput, double maximumOutput) {
        if (minimumOutput > maximumOutput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumOutput = minimumOutput;
        m_maximumOutput = maximumOutput;
    }

    /**
     * Set the setpoint for the PIDController
     * @param setpoint the desired setpoint
     */
    public synchronized void setSetpoint(double setpoint) {
        if (m_maximumInput > m_minimumInput) {
            if (setpoint > m_maximumInput) {
                m_setpoint = m_maximumInput;
            } else if (setpoint < m_minimumInput) {
                m_setpoint = m_minimumInput;
            } else {
                m_setpoint = setpoint;
            }
        } else {
            m_setpoint = setpoint;
        }
    }

    /**
     * Returns the current setpoint of the PIDController
     * @return the current setpoint
     */
    public synchronized double getSetpoint() {
        return m_setpoint;
    }

    /**
     * Returns the current difference of the input from the setpoint
     * @return the current error
     */
    public synchronized double getError() {
        return m_error;
    }

    public boolean isContinuous() {
        return m_continuous;
    }

    public boolean isEnabled() {
        return m_enabled;
    }

    public void setEnabled(boolean m_enabled) {
        this.m_enabled = m_enabled;
    }

    public void setError(double m_error) {
        this.m_error = m_error;
    }

    public double getMaximumInput() {
        return m_maximumInput;
    }

    public void setMaximumInput(double m_maximumInput) {
        this.m_maximumInput = m_maximumInput;
    }

    public double getMaximumOutput() {
        return m_maximumOutput;
    }

    public void setMaximumOutput(double m_maximumOutput) {
        this.m_maximumOutput = m_maximumOutput;
    }

    public double getMinimumInput() {
        return m_minimumInput;
    }

    public void setMinimumInput(double m_minimumInput) {
        this.m_minimumInput = m_minimumInput;
    }

    public double getMinimumOutput() {
        return m_minimumOutput;
    }

    public void setMinimumOutput(double m_minimumOutput) {
        this.m_minimumOutput = m_minimumOutput;
    }

    public double getPeriod() {
        return m_period;
    }

    public void setPeriod(double m_period) {
        this.m_period = m_period;
    }

    public PIDSource getPidInput() {
        return m_pidInput;
    }

    public void setPidInput(PIDSource m_pidInput) {
        this.m_pidInput = m_pidInput;
    }

    public PIDOutput getPidOutput() {
        return m_pidOutput;
    }

    public void setPidOutput(PIDOutput m_pidOutput) {
        this.m_pidOutput = m_pidOutput;
    }

    public double getPrevError() {
        return m_prevError;
    }

    public void setPrevError(double m_prevError) {
        this.m_prevError = m_prevError;
    }

    public double getResult() {
        return m_result;
    }

    public void setResult(double m_result) {
        this.m_result = m_result;
    }
    
    public double getTolerance() {
        return m_tolerance;
    }

    public double getTotalError() {
        return m_totalError;
    }

    public void setTotalError(double m_totalError) {
        this.m_totalError = m_totalError;
    }
    
    

    /**
     * Set the percentage error which is considered tolerable for use with
     * OnTarget. (Input of 15.0 = 15 percent)
     * @param percent error which is tolerable
     */
    public synchronized void setTolerance(double percent) {
        m_tolerance = percent;
    }

    /**
     * Return true if the error is within the percentage of the total input range,
     * determined by setTolerance. This assumes that the maximum and minimum input
     * were set using setInput.
     * @return true if the error is less than the tolerance
     */
    public synchronized boolean onTarget() {
        return (Math.abs(m_error) < m_tolerance / 100 *
                (m_maximumInput - m_minimumInput));
    }

    /**
     * Begin running the PIDController
     */
    public synchronized void enable() {
        m_enabled = true;
    }

    /**
     * Stop running the PIDController, this sets the output to zero before stopping.
     */
    public synchronized void disable() {
        m_pidOutput.pidWrite(0);
        m_enabled = false;
    }

    /**
     * Return true if PIDController is enabled.
     */
    public synchronized boolean isEnable() {
        return m_enabled;
    }

    /**
     * Reset the previous error,, the integral term, and disable the controller.
     */
    public synchronized void reset() {
        disable();
        m_prevError = 0;
        m_totalError = 0;
        m_result = 0;
    }
}
