package com._604robotics.robot2012.vision.config;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A JSlider that displays its current position and name in JLabels next to it
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public abstract class LinkedSlider extends Box implements ChangeListener {
	
	private static final long	serialVersionUID	= 1L;

	public JSlider slider;
	JLabel valLabel;
	private JLabel nameLabel;

	protected int min = 0;
	protected int max = 65535;
	protected double mul = 1;
	
	public abstract void setValue(double val);
	
	//Exponential = where the bar goes like:		[0		.01		.1		1]

	public static class ExponentialLinkedSlider extends DoubleLinkedSlider {
		
		private double valMul = 1;

		public ExponentialLinkedSlider(String name, double initial, double max) {
			super(name, Math.log10(initial*9+1)/max, 1);
			valMul = max;
			
			updateValLabel();
		}
		public ExponentialLinkedSlider(String name, double initial) {
			this(name, initial, 1);
		}

		public double getValue() {
			double rawVal = (slider.getValue()*1.0)/max;
			rawVal = (Math.pow(10, rawVal)-1)/9;
			return valMul*rawVal + min;
		}
		
		public void setValue(double val) {
			slider.setValue((int) (max*Math.log10(val*9+1)/valMul));
		}
	}
	
	
	public static class IntLinkedSlider extends LinkedSlider {

		public IntLinkedSlider(String name, int min, int max, int val) {
			super(name, min, max, val);
		}

		public int getIntValue() {
			return slider.getValue();
		}
		
		public double getValue() {
			return getIntValue();
		}

		public String getValText() {
			return String.format("%d", getIntValue());
		}

		public void setValue(double val) {
			slider.setValue((int) val);
		}
	}
	public static class DoubleLinkedSlider extends LinkedSlider {
		
		private static final int MAX = 65536;
		
		public DoubleLinkedSlider(String name, double initialValue, double mul) {
			super(name, 0, MAX, (int) Math.round(initialValue*MAX));
			this.mul = mul;
			
			updateValLabel();
		}
		public double getValue() {
			double rawVal = (slider.getValue()*1.0)/max;
			
			return mul*rawVal + min;
		}

		public void setValue(double val) {
			slider.setValue((int) ((val-min)/mul));
		}
	}
	
	
	public LinkedSlider(String name, int min, int max, int val) {
		super(BoxLayout.X_AXIS);
		
		this.min = min;
		this.max = max;
		this.mul = (max-min);
		
		slider = new JSlider(min, max, val);
		setupSlider();
		
		this.add(nameLabel = new JLabel(name));
		this.add(slider);
		this.add(valLabel = new JLabel(""));

		
		updateValLabel();
	}
	
	
	private void setupSlider() {
		Dimension size = new Dimension(320, 50);
		slider.setMaximumSize(size);
		slider.setMinimumSize(size);
		slider.addChangeListener(this);
	}
	
	public void stateChanged(ChangeEvent e) {
		updateValLabel();
	}
	
	protected void updateValLabel() {

		//nameLabel.setPreferredSize(new Dimension(100, 30));
		//valLabel.setPreferredSize(new Dimension(100, 30));
		
		setSizeOnComp(nameLabel, 100, 30);
		setSizeOnComp(valLabel, 50, 30);
		
		valLabel.setText(getValText());
	}
	
	private void setSizeOnComp(JComponent c, int w, int h) {
		Dimension d = new Dimension(w, h);
		c.setMinimumSize(d);
		c.setMaximumSize(d);
		c.setPreferredSize(d);
	}
	
	public String getValText() {
			//return String.format("%d", slider.getValue());
			return String.format("%.3f", getValue());
	}
	
	public abstract double getValue();

	
}