package com._604robotics.robot2012.vision.config;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logging.Logger;

/**
 * A JSlider that displays its current position and name in JLabels next to it
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public abstract class LinkedSlider extends Box implements ChangeListener {
	
	/**
	 * A default serialVersionUID to remove warnings
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * The slider that the user interacts with
	 */
	public JSlider slider;
	
	/**
	 * The label that indicates the current value of the slider
	 */
	JLabel valLabel;
	
	/**
	 * The label that indicates the name of the slider
	 */
	private JLabel nameLabel;

	/**
	 * The minimum value on the slider (must be an integer)
	 */
	protected int min = 0;
	
	/**
	 * The maximum value on the slider (must be an integer)
	 */
	protected int max = 65535;
	
	/**
	 * A number to multiply all slider outputs by
	 */
	protected double mul = 1;
	
	/**
	 * A setter for the value of the slider
	 * 
	 * @param val the value to set the slider to
	 */
	public abstract void setValue(double val);
	
	//Exponential = where the bar goes like:		[0		.01		.1		1]

	/**
	 * A LinkedSlider that has an exponential scale, so it is much easier to pick small values (close to zero) while still
	 * allowing a range up to 1
	 *
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	public static class ExponentialLinkedSlider extends DoubleLinkedSlider {
		
		/**
		 * What to multiply values
		 */
		private double valMul = 1;
		
		private static final double exponent = 50;

		/**
		 * A constructor to make an ExponentialLinkedSlider
		 * 
		 * @param name	The name of the slider
		 * @param initial	The initial value
		 * @param max	The maximum value
		 */
		public ExponentialLinkedSlider(String name, double initial, double max) {
			super(name, Math.log(initial*(exponent-1)+1)/max/Math.log(exponent), 0, 1);
			valMul = max;
			
			Logger.log(""+getValue());
			
			updateValLabel();
		}
		
		/**
		 * A constructor to make an ExponentialLinkedSlider.
		 * A default max of 1 is assumed.
		 * 
		 * @param name	The name of the slider
		 * @param initial	the initial value of the slider
		 */
		public ExponentialLinkedSlider(String name, double initial) {
			this(name, initial, 1);
		}

		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.config.LinkedSlider.DoubleLinkedSlider#getValue()
		 */
		public double getValue() {
			double rawVal = (slider.getValue()*1.0)/max;
			rawVal = (Math.pow(exponent, rawVal)-1)/(exponent - 1);
			return valMul*rawVal + min;
		}
		
		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.config.LinkedSlider.DoubleLinkedSlider#setValue(double)
		 */
		public void setValue(double val) {
			slider.setValue((int) (max*Math.log(val*(exponent - 1)+1)/valMul/Math.log(exponent)));
		}
	}
	
	
	/**
	 * A LinkedSlider that can only be set to integers
	 *
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	public static class IntLinkedSlider extends LinkedSlider {

		/**
		 * A constructor
		 * 
		 * @param name	The name of the slider
		 * @param min	The minimum value
		 * @param max	The maximum value
		 * @param val	The initial value
		 */
		public IntLinkedSlider(String name, int min, int max, int val) {
			super(name, min, max, val);
		}

		/**
		 * @return the current value
		 */
		public int getIntValue() {
			return slider.getValue();
		}
		
		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.config.LinkedSlider#getValue()
		 */
		public double getValue() {
			return getIntValue();
		}

		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.config.LinkedSlider#getValText()
		 */
		public String getValText() {
			return String.format("%d", getIntValue());
		}

		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.config.LinkedSlider#setValue(double)
		 */
		public void setValue(double val) {
			slider.setValue((int) val);
		}
	}
	
	/**
	 * A LinkedSlider that can be set to floating-point values
	 *
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	public static class DoubleLinkedSlider extends LinkedSlider {
		
		/**
		 * The underlying resolution of the slider
		 */
		private static final int MAX = 65536;
		
		private final double dmin;
		
		/**
		 * A constructor for a DoubleLinkedSlider
		 * 
		 * @param name	The name of the slider
		 * @param initialValue	The initial value
		 * @param max	The maximum value that this slider can be at
		 */
		public DoubleLinkedSlider(String name, double initialValue, double min, double max) {
			super(name, 0, MAX, (int) Math.round((initialValue-min)/(max - min)*MAX));
			this.mul = (max - min);
			this.dmin = min;
			
			updateValLabel();
		}
		
		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.config.LinkedSlider#getValue()
		 */
		public double getValue() {
			double rawVal = (slider.getValue()*1.0)/max;
			
			return mul*rawVal + dmin;
		}

		/* (non-Javadoc)
		 * @see com._604robotics.robot2012.vision.config.LinkedSlider#setValue(double)
		 */
		public void setValue(double val) {
			slider.setValue((int) Math.round((val-dmin)/mul*MAX));
		}
	}
	
	
	/**
	 * A constructor for a LinkedSlider
	 * 
	 * @param name	The name of the slider
	 * @param min	The minimum value
	 * @param max	The maximum value
	 * @param val	The initial value
	 */
	public LinkedSlider(String name, int min, int max, int val) {
		super(BoxLayout.X_AXIS);
		
		this.min = min;
		this.max = max;
		this.mul = (max-min);
		System.out.printf("name = %s min = %d, max = %d, val = %d\n", name, min, max, val);
		slider = new JSlider(min, max, val);
		setupSlider();
		
		this.add(nameLabel = new JLabel(name));
		this.add(slider);
		this.add(valLabel = new JLabel(""));

		
		updateValLabel();
	}
	
	
	/**
	 * A function that sets the initial size of the slider and adds {@code this} as a ChangeListener
	 */
	private void setupSlider() {
		Dimension size = new Dimension(320, 50);
		slider.setMaximumSize(size);
		slider.setMinimumSize(size);
		slider.addChangeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		updateValLabel();
	}
	
	/**
	 * This method updates the label on the right side that displays the current value
	 */
	protected void updateValLabel() {

		//nameLabel.setPreferredSize(new Dimension(100, 30));
		//valLabel.setPreferredSize(new Dimension(100, 30));
		
		setSizeOnComp(nameLabel, 100, 30);
		setSizeOnComp(valLabel, 50, 30);
		
		valLabel.setText(getValText());
	}
	
	/**
	 * A simple method to 'force' Swing's layout manager to make a component a given size
	 * 
	 * @param c	The component to set the size on
	 * @param w	The width to set
	 * @param h	The height to set
	 */
	private void setSizeOnComp(JComponent c, int w, int h) {
		Dimension d = new Dimension(w, h);
		c.setMinimumSize(d);
		c.setMaximumSize(d);
		c.setPreferredSize(d);
	}
	
	/**
	 * This method returns a human-readable formatted number suited for the type of LinkedSlider.
	 * It is used to show the current value on the slider
	 * 
	 * @return The string that is shown in the JLabel to the right of the slider
	 */
	public String getValText() {
			//return String.format("%d", slider.getValue());
			return String.format("%.3f", getValue());
	}
	
	/**
	 * @return The current value
	 */
	public abstract double getValue();

	
}