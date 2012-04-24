package com._604robotics.utils;

import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class EncoderRateSensor extends Encoder {
    private int countsPerRevolution = 250;
    private double previousTime = 0D;
    
    public double measurement = 0D;
    
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
    public EncoderRateSensor (final int aSlot, final int aChannel, final int bSlot, final int bChannel, boolean reverseDirection) {
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
    public EncoderRateSensor (final int aSlot, final int aChannel, final int bSlot, final int bChannel) {
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
    public EncoderRateSensor (final int aSlot, final int aChannel, final int bSlot, final int bChannel, boolean reverseDirection, final EncodingType encodingType) {
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
    public EncoderRateSensor (final int aSlot, final int aChannel, final int bSlot, final int bChannel, final int indexSlot, final int indexChannel, boolean reverseDirection) {
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
    public EncoderRateSensor (final int aSlot, final int aChannel, final int bSlot, final int bChannel, final int indexSlot, final int indexChannel) {
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
    public EncoderRateSensor (final int aChannel, final int bChannel, boolean reverseDirection) {
        super(aChannel, bChannel, reverseDirection);
    }

    /**
     * Encoder constructor.
     * Construct a Encoder given a and b channels assuming the default module.
     * @param aChannel The a channel digital input channel.
     * @param bChannel The b channel digital input channel.
     */
    public EncoderRateSensor (final int aChannel, final int bChannel) {
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
    public EncoderRateSensor (final int aChannel, final int bChannel, boolean reverseDirection, final EncodingType encodingType) {
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
    public EncoderRateSensor (final int aChannel, final int bChannel, final int indexChannel, boolean reverseDirection) {
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
    public EncoderRateSensor (final int aChannel, final int bChannel, final int indexChannel) {
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
    public EncoderRateSensor (DigitalSource aSource, DigitalSource bSource, boolean reverseDirection) {
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
    public EncoderRateSensor (DigitalSource aSource, DigitalSource bSource) {
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
    public EncoderRateSensor (DigitalSource aSource, DigitalSource bSource, boolean reverseDirection, final EncodingType encodingType) {
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
    public EncoderRateSensor (DigitalSource aSource, DigitalSource bSource, DigitalSource indexSource, boolean reverseDirection) {
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
    public EncoderRateSensor (DigitalSource aSource, DigitalSource bSource, DigitalSource indexSource) {
        super(aSource, bSource, indexSource);
    }
    
    public void setCountsPerRevolution (int countsPerRevolution) {
        this.countsPerRevolution = countsPerRevolution;
    }
    
    public void sample () {
        final double newTime = Timer.getFPGATimestamp();
        final int counts = this.getRaw();
        this.reset();
        
        this.measurement = (60D / this.countsPerRevolution) * counts / (newTime - this.previousTime);
        this.previousTime = newTime;
    }
    
    public double getRate () {
        return this.measurement;
    }
    
    public double pidGet () {
        return this.measurement;
    }
}