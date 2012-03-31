package com._604robotics.utils;

import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Encoder;
import java.util.Vector;

public class EncoderSamplingRateAverage extends Encoder {
    // TODO: Cleanup, document.
    
    private final Vector buffer = new Vector();
    
    private int averagePoints = 1;
    
    private long lastMeasured = 0;
    private int samplingRate = 20;
    private int startClicks = 0;
    private int lastCount = 0;
    
    private double averagedRate = 0;
    
    /**
     * Encoder constructor.
     * Construct a Encoder given a and b modules and channels fully specified.
     * @param aSlot The a channel digital input module.
     * @param aChannel The a channel digital input channel.
     * @param bSlot The b channel digital input module.
     * @param bChannel The b channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     */
    public EncoderSamplingRateAverage (final int aSlot, final int aChannel, final int bSlot, final int bChannel, boolean reverseDirection) {
        super(aSlot, aChannel, bSlot, bChannel, reverseDirection);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b modules and channels fully specified.
     * @param aSlot The a channel digital input module.
     * @param aChannel The a channel digital input channel.
     * @param bSlot The b channel digital input module.
     * @param bChannel The b channel digital input channel.
     */
    public EncoderSamplingRateAverage (final int aSlot, final int aChannel, final int bSlot, final int bChannel) {
        super(aSlot, aChannel, bSlot, bChannel);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b modules and channels fully specified.
     * @param aSlot The a channel digital input module.
     * @param aChannel The a channel digital input channel.
     * @param bSlot The b channel digital input module.
     * @param bChannel The b channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     * @param encodingType either k1X, k2X, or k4X to indicate 1X, 2X or 4X decoding. If 4X is
     * selected, then an encoder FPGA object is used and the returned counts will be 4x the encoder
     * spec'd value since all rising and falling edges are counted. If 1X or 2X are selected then
     * a counter object will be used and the returned value will either exactly match the spec'd count
     * or be double (2x) the spec'd count.
     */
    public EncoderSamplingRateAverage (final int aSlot, final int aChannel, final int bSlot, final int bChannel, boolean reverseDirection, final EncodingType encodingType) {
        super(aSlot, aChannel, bSlot, bChannel, reverseDirection, encodingType);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b modules and channels fully specified.
     * Using the index pulse forces 4x encoding.
     * @param aSlot The a channel digital input module.
     * @param aChannel The a channel digital input channel.
     * @param bSlot The b channel digital input module.
     * @param bChannel The b channel digital input channel.
     * @param indexSlot The index channel digital input module.
     * @param indexChannel The index channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     */
    public EncoderSamplingRateAverage (final int aSlot, final int aChannel, final int bSlot, final int bChannel, final int indexSlot, final int indexChannel, boolean reverseDirection) {
        super(aSlot, aChannel, bSlot, bChannel, indexSlot, indexChannel, reverseDirection);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b modules and channels fully specified.
     * Using the index pulse forces 4x encoding.
     * @param aSlot The a channel digital input module.
     * @param aChannel The a channel digital input channel.
     * @param bSlot The b channel digital input module.
     * @param bChannel The b channel digital input channel.
     * @param indexSlot The index channel digital input module.
     * @param indexChannel The index channel digital input channel.
     */
    public EncoderSamplingRateAverage (final int aSlot, final int aChannel, final int bSlot, final int bChannel, final int indexSlot, final int indexChannel) {
        super(aSlot, aChannel, bSlot, bChannel, indexSlot, indexChannel);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels assuming the default module.
     * @param aChannel The a channel digital input channel.
     * @param bChannel The b channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     */
    public EncoderSamplingRateAverage (final int aChannel, final int bChannel, boolean reverseDirection) {
        super(aChannel, bChannel, reverseDirection);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels assuming the default module.
     * @param aChannel The a channel digital input channel.
     * @param bChannel The b channel digital input channel.
     */
    public EncoderSamplingRateAverage (final int aChannel, final int bChannel) {
        super(aChannel, bChannel);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels assuming the default module.
     * @param aChannel The a channel digital input channel.
     * @param bChannel The b channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     * @param encodingType either k1X, k2X, or k4X to indicate 1X, 2X or 4X decoding. If 4X is
     * selected, then an encoder FPGA object is used and the returned counts will be 4x the encoder
     * spec'd value since all rising and falling edges are counted. If 1X or 2X are selected then
     * a counter object will be used and the returned value will either exactly match the spec'd count
     * or be double (2x) the spec'd count.
     */
    public EncoderSamplingRateAverage (final int aChannel, final int bChannel, boolean reverseDirection, final EncodingType encodingType) {
        super(aChannel, bChannel, reverseDirection, encodingType);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels assuming the default module.
     * Using an index pulse forces 4x encoding
     * @param aChannel The a channel digital input channel.
     * @param bChannel The b channel digital input channel.
     * @param indexChannel The index channel digital input channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     */
    public EncoderSamplingRateAverage (final int aChannel, final int bChannel, final int indexChannel, boolean reverseDirection) {
        super(aChannel, bChannel, indexChannel, reverseDirection);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels assuming the default module.
     * Using an index pulse forces 4x encoding
     * @param aChannel The a channel digital input channel.
     * @param bChannel The b channel digital input channel.
     * @param indexChannel The index channel digital input channel.
     */
    public EncoderSamplingRateAverage (final int aChannel, final int bChannel, final int indexChannel) {
        super(aChannel, bChannel, indexChannel);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels as digital inputs. This is used in the case
     * where the digital inputs are shared. The Encoder class will not allocate the digital inputs
     * and assume that they already are counted.
     * @param aSource The source that should be used for the a channel.
     * @param bSource the source that should be used for the b channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     */
    public EncoderSamplingRateAverage (DigitalSource aSource, DigitalSource bSource, boolean reverseDirection) {
        super(aSource, bSource, reverseDirection);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels as digital inputs. This is used in the case
     * where the digital inputs are shared. The Encoder class will not allocate the digital inputs
     * and assume that they already are counted.
     * @param aSource The source that should be used for the a channel.
     * @param bSource the source that should be used for the b channel.
     */
    public EncoderSamplingRateAverage (DigitalSource aSource, DigitalSource bSource) {
        super(aSource, bSource);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels as digital inputs. This is used in the case
     * where the digital inputs are shared. The Encoder class will not allocate the digital inputs
     * and assume that they already are counted.
     * @param aSource The source that should be used for the a channel.
     * @param bSource the source that should be used for the b channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     * @param encodingType either k1X, k2X, or k4X to indicate 1X, 2X or 4X decoding. If 4X is
     * selected, then an encoder FPGA object is used and the returned counts will be 4x the encoder
     * spec'd value since all rising and falling edges are counted. If 1X or 2X are selected then
     * a counter object will be used and the returned value will either exactly match the spec'd count
     * or be double (2x) the spec'd count.
     */
    public EncoderSamplingRateAverage (DigitalSource aSource, DigitalSource bSource, boolean reverseDirection, final EncodingType encodingType) {
        super(aSource, bSource, reverseDirection, encodingType);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels as digital inputs. This is used in the case
     * where the digital inputs are shared. The Encoder class will not allocate the digital inputs
     * and assume that they already are counted.
     * @param aSource The source that should be used for the a channel.
     * @param bSource the source that should be used for the b channel.
     * @param indexSource the source that should be used for the index channel.
     * @param reverseDirection represents the orientation of the encoder and inverts the output values
     * if necessary so forward represents positive values.
     */
    public EncoderSamplingRateAverage (DigitalSource aSource, DigitalSource bSource, DigitalSource indexSource, boolean reverseDirection) {
        super(aSource, bSource, indexSource, reverseDirection);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels as digital inputs. This is used in the case
     * where the digital inputs are shared. The Encoder class will not allocate the digital inputs
     * and assume that they already are counted.
     * @param aSource The source that should be used for the a channel.
     * @param bSource the source that should be used for the b channel.
     * @param indexSource the source that should be used for the index channel.
     */
    public EncoderSamplingRateAverage (DigitalSource aSource, DigitalSource bSource, DigitalSource indexSource) {
        super(aSource, bSource, indexSource);
    }
    
    public void setSamplingRate (int samplingRate) {
        this.samplingRate = samplingRate;
    }
    
    public void sample () {
        final long now = System.currentTimeMillis();
        if (now - this.lastMeasured >= this.samplingRate) {
            final int clicks = this.get();
            this.lastMeasured = now;
            this.lastCount = clicks - this.startClicks;
            this.startClicks = clicks;
            
            this.buffer.addElement(Integer.toString(this.lastCount));
            while (this.buffer.size() > this.averagePoints)
                this.buffer.removeElementAt(0);
            
            int sum = 0;
            for (int i = 0; i < this.buffer.size(); i++)
                sum += Integer.parseInt((String) this.buffer.elementAt(i));
            
            this.averagedRate = ((double) sum) / this.buffer.size();
        }
    }
    
    public double getRate () {
        return this.averagedRate;
    }
    
    public double getRawRate () {
        return this.lastCount;
    }
    
    public void setAveragePoints (int averagePoints) {
        this.averagePoints = averagePoints;
    }
}