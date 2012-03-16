package com._604robotics.robot2012.vision.config;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com._604robotics.robot2012.vision.VisionProcessing;

/**
 * <p>
 * This class creates a window for configuring various aspects of the Vision program, such as target
 * color, target sensitivity, and other values found in {@link Config}.
 * </p>
 * 
 * <p>
 * The name of this class is officially "Configger", a common mispronunciation of the word "Configure".
 * It comes from nounifying the verb form of the shortened word "Config".
 * </p>
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Configger {
	
	JFrame frame = new JFrame("604 FRCVision Configger");
	
	/**
	 * <p>
	 * This pane holds the entire configuration UI
	 * </p>
	 * 
	 * <p>
	 * The main UI Element of the Configger window
	 * 
	 * The tabs are:
	 * 	- Color Tuner
	 * 		- R, G, B multipliers
	 * 		- Sensitivity
	 * 	- Tile Tuner
	 * 		- CheckCenter
	 * 		- MegaScan
	 * 		- Min Blob Size
	 * 		- Step
	 * 	- Debug
	 * 		- Communicate to robot
	 * 		- Save images to files
	 * 		- Show Debug Camera Display
	 * 		- Print Debug Info
	 * </p>
	 */
	JTabbedPane tabbedPane = new JTabbedPane();
	
	/**
	 * The tabs in the tabbedPane
	 */
	Box colorTunerTab = new Box(BoxLayout.Y_AXIS), tileTunerTab = new Box(BoxLayout.Y_AXIS), debugTab = new Box(BoxLayout.Y_AXIS);
	
	JLabel colorLabel = new JLabel("r=000\tg=000\tb=000   ");
	
	/**
	 * A simple main() method to make the Configger a runnable program
	 */
	public static void main(String[] args) {
		new Configger();
	}
	
	/**
	 * This constructor of the Configger initializes everything and sets the Configger as visible.
	 */
	public Configger() {
		tabbedPane.addTab("Color Tuner", colorTunerTab);
		tabbedPane.addTab("Tile Tuner", tileTunerTab);
		tabbedPane.addTab("Debug", debugTab);
		
		Config conf = VisionProcessing.defaultProcessing.conf;
		
		setupColorTuner(conf);
		setupTileTuner(conf);
		setupDebugTab(conf);
		
		//tileTunertab
		
		final VisionProcessing vp = VisionProcessing.defaultProcessing;
		
		Box ui = new Box(BoxLayout.Y_AXIS);
		ui.add(tabbedPane);
		ui.add(vp.display);
		ui.add(colorLabel);
		
		Runnable colorUpdater = new Runnable(){
			public void run() {
				while(true) {
					try {
						Point p = vp.display.getMousePosition();
						
						
						
						int rgb = vp.display.image.getRGB(p.x, p.y);
						
						int r = (rgb & 0xFF0000)	>>> 16;
						int g = (rgb & 0xFF00)	>>> 8;
						int b = (rgb & 0xFF);
						
						colorLabel.setText(String.format("r=%d\tg=%d\tb=%d", r, g, b));
					} catch (Exception ex) {
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		};
		
		Thread tc = new Thread(colorUpdater);
		tc.start();

		JButton acceptButton = new JButton("Accept");
		JButton revertButton = new JButton("Revert");

		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					VisionProcessing.defaultProcessing.conf.saveDefaultConfig();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
				///lastConf = VisionProcessing.defaultProcessing.conf;
			}
		});
		revertButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				revertToConf();
			}
		});
		
		Box acceptRevertBox = new Box(BoxLayout.X_AXIS);

		acceptRevertBox.add(new JPanel());
		acceptRevertBox.add(acceptButton);
		acceptRevertBox.add(revertButton);
		
		ui.add(acceptRevertBox);
		
		Runnable r = new Runnable() {
			public void run() {
				try {
					//vp.loopAndProcessPreSavedPics();
					vp.loopAndProcessPics();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		};
		
		/*
		 * r	e
		 * 46	43
		 * 63	59
		 * 85	86-87
		 * 101	104
		 */
		
		Thread t = new Thread(r);
		t.start();
		
		frame.add(ui);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//TODO - save on exit
		//frame.pack();
		Dimension tabbedPaneSize = new Dimension(400, 300);
		tabbedPane.setSize(tabbedPaneSize);
		tabbedPane.setMinimumSize(tabbedPaneSize);
		tabbedPane.setMaximumSize(tabbedPaneSize);
		tabbedPane.setPreferredSize(tabbedPaneSize);
		
		
		
		frame.pack();
		
		frame.setVisible(true);
	}
	
	/**
	 * Sets up the Debug tab
	 * 
	 * @param conf	The Config to configure
	 */
	private void setupDebugTab(Config conf) {
		communicate = new JCheckBox("Communicate to Robot", conf.getBoolean("communicateToRobot"));
		saveImgs = new JCheckBox("Save Images", conf.getBoolean("debug_SaveImagesToFiles"));
		showDebugCam = new JCheckBox("Show Debug Cam", conf.getBoolean("debug_ShowDisplay"));
		printDebug = new JCheckBox("Print Debug", conf.getBoolean("debug_Print"));
		debugTab.add(communicate);
		debugTab.add(saveImgs);
		debugTab.add(showDebugCam);
		debugTab.add(printDebug);
		
		communicate.addActionListener(refresher);
		saveImgs.addActionListener(refresher);
		showDebugCam.addActionListener(refresher);
		printDebug.addActionListener(refresher);
	}
	
	/**
	 * Sets up the Tile Tuner tab
	 * 
	 * @param conf	The Config to configure
	 */
	private void setupTileTuner(Config conf) {
		centerCheck = new JCheckBox("Check Center", conf.getBoolean("checkCenter"));
		scanWholeTile = new JCheckBox("Scan Whole Tile", conf.getBoolean("scanWholeTile"));
		
		minBlobSize = new LinkedSlider.IntLinkedSlider("Min. Blob Size", 1, 200, conf.getInt("minBlobSize"));
		tileSize = new LinkedSlider.IntLinkedSlider("Tile Size", 1, 12, conf.getInt("tileSize"));
		
		
		tileTunerTab.add(centerCheck);
		tileTunerTab.add(scanWholeTile);
		tileTunerTab.add(minBlobSize);
		tileTunerTab.add(tileSize);
		
		centerCheck.addActionListener(refresher);
		scanWholeTile.addActionListener(refresher);
		
		minBlobSize.slider.addChangeListener(refresher);
		tileSize.slider.addChangeListener(refresher);
	}
	
	/**
	 * Color sliders
	 * 
	 * @see Config
	 */
	LinkedSlider rSlider, gSlider, bSlider;
	
	
	/**
	 * Slider to tune the variable with the same name in {@link Config}
	 */
	LinkedSlider.IntLinkedSlider rTarget, gTarget, bTarget, sensSlider, minBlobSize, tileSize;
	
	/**
	 * Check boxes to toggle variables with the same name in {@link Config}
	 */
	JCheckBox centerCheck, scanWholeTile, communicate, saveImgs, showDebugCam, printDebug;
	
	//private Config lastConf;
	/**
	 * A refresher to refresh the Config whenever changes are triggered
	 */
	private Refresher refresher = new Refresher();
	
	/**
	 * A class to refresh the Config every time a change is made
	 *
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	private class Refresher implements ActionListener, ChangeListener {
		
		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		public void stateChanged(ChangeEvent e) {
			refresh();
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			refresh();
		}
		
		/**
		 * This is called every time a value changes on the Configger. It places the values back
		 * into the Config.
		 */
		private void refresh() {
			Config conf = VisionProcessing.defaultProcessing.conf;
			conf.setValue("color_targetR", rTarget.getIntValue());
			conf.setValue("color_targetG", gTarget.getIntValue());
			conf.setValue("color_targetB", bTarget.getIntValue());

			conf.setValue("color_mulR", rSlider.getValue());
			conf.setValue("color_mulG", gSlider.getValue());
			conf.setValue("color_mulB", bSlider.getValue());

			conf.setValue("sensitivity", sensSlider.getIntValue());
			conf.setValue("minBlobSize", minBlobSize.getIntValue());
			conf.setValue("tileSize", tileSize.getIntValue());
			

			conf.setValue("checkCenter", centerCheck.isSelected());
			conf.setValue("scanWholeTile", scanWholeTile.isSelected());
			conf.setValue("communicateToRobot", communicate.isSelected());
			conf.setValue("debug_SaveImagesToFiles", saveImgs.isSelected());
			conf.setValue("debug_SaveImagesToFiles", saveImgs.isSelected());
			conf.setValue("debug_ShowDisplay", showDebugCam.isSelected());
			conf.setValue("debug_Print", printDebug.isSelected());
			
			//tabbedPane.repaint();
		}
	}
	
	///private LinkedSlider[] sliders = {rTarget, gTarget, bTarget, rSlider, gSlider, bSlider, sensSlider, minBlobSize, tileSize};
	
	/**
	 * Revert to the previously saved Config file
	 */
	private void revertToConf() {
		Config conf = Config.readDefaultConfig();

		rTarget.setValue(conf.getInt("color_targetR"));
		gTarget.setValue(conf.getInt("color_targetG"));
		bTarget.setValue(conf.getInt("color_targetB"));

		rSlider.setValue(conf.getDouble("color_mulR"));
		gSlider.setValue(conf.getDouble("color_mulG"));
		bSlider.setValue(conf.getDouble("color_mulB"));

		sensSlider.setValue(conf.getInt("sensitivity"));
		minBlobSize.setValue(conf.getInt("minBlobSize"));
		tileSize.setValue(conf.getInt("tileSize"));
		
		/*
		for(int i = 0; i < sliders.length; i++) {
			sliders[i].updateValLabel();
		}
		*/

		centerCheck.setSelected(conf.getBoolean("checkCenter"));
		scanWholeTile.setSelected(conf.getBoolean("scanWholeTile"));
		communicate.setSelected(conf.getBoolean("communicateToRobot"));
		saveImgs.setSelected(conf.getBoolean("debug_SaveImagesToFiles"));
		showDebugCam.setSelected(conf.getBoolean("debug_ShowDisplay"));
		printDebug.setSelected(conf.getBoolean("debug_Print"));

		VisionProcessing.defaultProcessing.conf = conf;
		
		System.out.println(conf);

		tabbedPane.repaint();
	}
	
	/**
	 * Sets up the Color Tuner tab
	 * 
	 * @param conf	The Config
	 */
	private void setupColorTuner(Config conf) {
		rSlider = new LinkedSlider.ExponentialLinkedSlider("Red Multiplier",   conf.getDouble("color_mulR"));
		gSlider = new LinkedSlider.ExponentialLinkedSlider("Green Multiplier", conf.getDouble("color_mulG"));
		bSlider = new LinkedSlider.ExponentialLinkedSlider("Blue Multiplier",  conf.getDouble("color_mulB"));
		
		rTarget = new LinkedSlider.IntLinkedSlider("Red Target",   0, 255, conf.getInt("color_targetR"));
		gTarget = new LinkedSlider.IntLinkedSlider("Green Target", 0, 255, conf.getInt("color_targetG"));
		bTarget = new LinkedSlider.IntLinkedSlider("Blue Target",  0, 255, conf.getInt("color_targetB"));
		
		sensSlider = new LinkedSlider.IntLinkedSlider("Sensitivity", -128, 127, conf.getInt("sensitivity"));
		
		
		colorTunerTab.add(rSlider);
		colorTunerTab.add(gSlider);
		colorTunerTab.add(bSlider);
		colorTunerTab.add(new JSeparator());
		colorTunerTab.add(rTarget);
		colorTunerTab.add(gTarget);
		colorTunerTab.add(bTarget);
		colorTunerTab.add(new JSeparator());
		colorTunerTab.add(sensSlider);
		
		
		rSlider.slider.addChangeListener(refresher);
		gSlider.slider.addChangeListener(refresher);
		bSlider.slider.addChangeListener(refresher);
		rTarget.slider.addChangeListener(refresher);
		gTarget.slider.addChangeListener(refresher);
		bTarget.slider.addChangeListener(refresher);
		sensSlider.slider.addChangeListener(refresher);
	}
	
	/**
	 * A simple utility method that creates a javax.swing.Box that holds a label indicating the name of the variable
	 * to change and a text field for the user to type input into.
	 * 
	 * @param textField	The JTextField the user can type into
	 * @param name	The name of the value to change (shown in a JLabel)
	 * @return a Box containing the JLabel and JTextField
	 */
	public static Box boxForTextField(JTextField textField, String name) {
		Box b = new Box(BoxLayout.X_AXIS);
		b.add(new JLabel(name));
		b.add(textField);
		return b;
	}
	
}
