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
 * TODO - comments, javadoc
 * 
 * TODO - make this whole mess less kludgy
 * 
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Configger {
	
	JFrame frame = new JFrame("604 FRCVision Configger");
	
	/**
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
	 */
	JTabbedPane tabbedPane = new JTabbedPane();
	
	Box colorTunerTab = new Box(BoxLayout.Y_AXIS), tileTunerTab = new Box(BoxLayout.Y_AXIS), debugTab = new Box(BoxLayout.Y_AXIS);
	
	JLabel colorLabel = new JLabel("r=000\tg=000\tb=000   ");
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Configger().start();
	}
	
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
	
	private void setupDebugTab(Config conf) {
		communicate = new JCheckBox("Communicate to Robot", conf.communicateToRobot);
		saveImgs = new JCheckBox("Save Images", conf.debug_SaveImagesToFiles);
		showDebugCam = new JCheckBox("Show Debug Cam", conf.debug_ShowDisplay);
		printDebug = new JCheckBox("Print Debug", conf.debug_Print);
		debugTab.add(communicate);
		debugTab.add(saveImgs);
		debugTab.add(showDebugCam);
		debugTab.add(printDebug);
		
		communicate.addActionListener(refresher);
		saveImgs.addActionListener(refresher);
		showDebugCam.addActionListener(refresher);
		printDebug.addActionListener(refresher);
	}
	
	private void setupTileTuner(Config conf) {
		centerCheck = new JCheckBox("Check Center", conf.checkCenter);
		scanWholeTile = new JCheckBox("Scan Whole Tile", conf.scanWholeTile);
		
		minBlobSize = new LinkedSlider.IntLinkedSlider("Min. Blob Size", 1, 100, conf.minBlobSize);
		tileSize = new LinkedSlider.IntLinkedSlider("Tile Size", 1, 12, conf.tileSize);
		
		
		tileTunerTab.add(centerCheck);
		tileTunerTab.add(scanWholeTile);
		tileTunerTab.add(minBlobSize);
		tileTunerTab.add(tileSize);
		
		centerCheck.addActionListener(refresher);
		scanWholeTile.addActionListener(refresher);
		
		minBlobSize.slider.addChangeListener(refresher);
		tileSize.slider.addChangeListener(refresher);
	}
	
	LinkedSlider rSlider, gSlider, bSlider;
	LinkedSlider.IntLinkedSlider rTarget, gTarget, bTarget, sensSlider, minBlobSize, tileSize;
	JCheckBox centerCheck, scanWholeTile, communicate, saveImgs, showDebugCam, printDebug;
	
	//private Config lastConf;
	private Refresher refresher = new Refresher();
	
	private class Refresher implements ActionListener, ChangeListener {
		
		public void stateChanged(ChangeEvent e) {
			refresh();
		}
		
		public void actionPerformed(ActionEvent e) {
			refresh();
		}
		
		private void refresh() {
			Config conf = VisionProcessing.defaultProcessing.conf;
			conf.color_targetR = rTarget.getIntValue();
			conf.color_targetG = gTarget.getIntValue();
			conf.color_targetB = bTarget.getIntValue();
			
			conf.color_mulR = rSlider.getValue();
			conf.color_mulG = gSlider.getValue();
			conf.color_mulB = bSlider.getValue();
			
			conf.sensitivity = (byte)sensSlider.getIntValue();
			conf.minBlobSize = minBlobSize.getIntValue();
			conf.tileSize = tileSize.getIntValue();
			
			
			conf.checkCenter = centerCheck.isSelected();
			conf.scanWholeTile = scanWholeTile.isSelected();
			conf.communicateToRobot = communicate.isSelected();
			conf.debug_SaveImagesToFiles = saveImgs.isSelected();
			conf.debug_ShowDisplay = showDebugCam.isSelected();
			conf.debug_Print = printDebug.isSelected();
			
			//tabbedPane.repaint();
		}
	}
	
	///private LinkedSlider[] sliders = {rTarget, gTarget, bTarget, rSlider, gSlider, bSlider, sensSlider, minBlobSize, tileSize};
	
	/**
	 * Revert to the previously saved Config file
	 */
	private void revertToConf() {
		Config conf = Config.readDefaultConfig();

		rTarget.setValue(conf.color_targetR);
		gTarget.setValue(conf.color_targetG);
		bTarget.setValue(conf.color_targetB);

		rSlider.setValue(0);
		gSlider.setValue(0);
		bSlider.setValue(0);
		tabbedPane.repaint();
		rSlider.setValue(conf.color_mulR);
		gSlider.setValue(conf.color_mulG);
		bSlider.setValue(conf.color_mulB);

		sensSlider.setValue(conf.sensitivity);
		minBlobSize.setValue(conf.minBlobSize);
		tileSize.setValue(conf.tileSize);
		
		/*
		for(int i = 0; i < sliders.length; i++) {
			sliders[i].updateValLabel();
		}
		*/

		centerCheck.setSelected(conf.checkCenter);
		scanWholeTile.setSelected(conf.scanWholeTile);
		communicate.setSelected(conf.communicateToRobot);
		saveImgs.setSelected(conf.debug_SaveImagesToFiles);
		showDebugCam.setSelected(conf.debug_ShowDisplay);
		printDebug.setSelected(conf.debug_Print);

		VisionProcessing.defaultProcessing.conf = conf;
		
		System.out.println(conf);

		tabbedPane.repaint();
	}
	
	private void setupColorTuner(Config conf) {
		rSlider = new LinkedSlider.ExponentialLinkedSlider("Red Multiplier",   conf.color_mulR);
		gSlider = new LinkedSlider.ExponentialLinkedSlider("Green Multiplier", conf.color_mulG);
		bSlider = new LinkedSlider.ExponentialLinkedSlider("Blue Multiplier",  conf.color_mulB);
		
		rTarget = new LinkedSlider.IntLinkedSlider("Red Target",   0, 255, conf.color_targetR);
		gTarget = new LinkedSlider.IntLinkedSlider("Green Target", 0, 255, conf.color_targetG);
		bTarget = new LinkedSlider.IntLinkedSlider("Blue Target",  0, 255, conf.color_targetB);
		
		sensSlider = new LinkedSlider.IntLinkedSlider("Sensitivity", -128, 127, conf.sensitivity);
		
		
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
	
	public static Box boxForTextField(JTextField f, String str) {
		Box b = new Box(BoxLayout.X_AXIS);
		b.add(new JLabel(str));
		b.add(f);
		return b;
	}
	
	public void start() {
		
	}
	
	
}
