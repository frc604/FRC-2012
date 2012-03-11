package com._604robotics.robot2012.vision.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Config implements Cloneable {
	
	public Config clone() {
		try {
			return (Config) super.clone();
		} catch (CloneNotSupportedException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static final File	defaultConfigFile	= new File("vision.conf");
	
	public static Config conf;
	
	/* CheckCenter, CommunicateToRobot, Debug_SaveImagesToFiles, Debug_ShowDisplay,
		MegaScan, MinBlobSize, Sensitivity, Step, ShowDebugInfo,  */
	
	// public static final Config config;
	// public static final File configFile = new File("604vision.conf");
	
	/*
	static {
		try {
			config = readConfig(configFile);
		} catch (FileNotFoundException ex) {
			config = null;
			ex.printStackTrace();
		}
	} */
	
	static boolean parseBoolean(String str, boolean def) {
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception ex) {
			return def;
		}
	}
	
	static byte parseByte(String str, byte def) {
		try {
			return Byte.parseByte(str);
		} catch (Exception ex) {
			return def;
		}
	}
	
	static double parseDouble(String str, double def) {
		try {
			return Double.parseDouble(str);
		} catch (Exception ex) {
			return def;
		}
	}
	
	static int parseInt(String str, int def) {
		try {
			return Integer.parseInt(str);
		} catch (Exception ex) {
			return def;
		}
	}

	public static Config readDefaultConfig() {
		return readConfig(defaultConfigFile);
	}
	public void saveDefaultConfig() throws IOException {
		save(defaultConfigFile);
	}
	
	public static Config readConfig(File file) /*throws FileNotFoundException*/ {
		Config conf = new Config();
		
		try {//TODO - this will ignore exceptions
			Scanner scanner = new Scanner(file);
			
			
			while (scanner.hasNext()) {
				String str = scanner.nextLine();
				
				String[] strs = str.split(" = ");
				
				if (strs.length != 2) {
					System.err.println("Couldn't parse:\t" + str);
					continue;
				}
				
				String key = strs[0].trim();
				
				String value = strs[1].trim();
				
				if (key.equals("checkCenter")) {
					conf.checkCenter = parseBoolean(value, conf.checkCenter);
				} else if (key.equals("communicateToRobot")) {
					conf.communicateToRobot = parseBoolean(value, conf.communicateToRobot);
				} else if (key.equals("debug_SaveImagesToFiles")) {
					conf.debug_SaveImagesToFiles = parseBoolean(value, conf.debug_SaveImagesToFiles);
				} else if (key.equals("debug_Print")) {
					conf.debug_Print = parseBoolean(value, conf.debug_Print);
				} else if (key.equals("debug_ShowDisplay")) {
					conf.debug_ShowDisplay = parseBoolean(value, conf.debug_ShowDisplay);
				} else if (key.equals("scanWholeTile")) {
					conf.scanWholeTile = parseBoolean(value, conf.scanWholeTile);
				} else if (key.equals("minBlobSize")) {
					conf.minBlobSize = parseInt(value, conf.minBlobSize);
				} else if (key.equals("sensitivity")) {
					conf.sensitivity = parseByte(value, (byte) conf.sensitivity);
				} else if (key.equals("tileSize")) {
					conf.tileSize = parseInt(value, conf.tileSize);

				} else if (key.equals("color_targetR")) {
					conf.color_targetR = parseInt(value, conf.color_targetR);
				} else if (key.equals("color_targetG")) {
					conf.color_targetG = parseInt(value, conf.color_targetG);
				} else if (key.equals("color_targetB")) {
					conf.color_targetB = parseInt(value, conf.color_targetB);

				} else if (key.equals("color_mulR")) {
					conf.color_mulR = parseDouble(value, conf.color_mulR);
				} else if (key.equals("color_mulG")) {
					conf.color_mulG = parseDouble(value, conf.color_mulG);
				} else if (key.equals("color_mulB")) {
					conf.color_mulB = parseDouble(value, conf.color_mulB);
				} else {
					System.out.println("Didn't find key '"+key+ "' for value: "+value);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return conf;
	}
	
	public void save(File file) throws IOException {
		if(!file.exists())
			file.createNewFile();
		
		FileWriter fw = new FileWriter(file);
		
		fw.write(getOutString("checkCenter", checkCenter));
		fw.write(getOutString("communicateToRobot", communicateToRobot));
		fw.write(getOutString("debug_SaveImagesToFiles", debug_SaveImagesToFiles));
		fw.write(getOutString("debug_Print", debug_Print));
		fw.write(getOutString("debug_ShowDisplay", debug_ShowDisplay));
		fw.write(getOutString("scanWholeTile", scanWholeTile));
		fw.write(getOutString("minBlobSize", minBlobSize));
		fw.write(getOutString("sensitivity", sensitivity));
		fw.write(getOutString("tileSize", tileSize));

		fw.write(getOutString("color_targetR", color_targetR));
		fw.write(getOutString("color_targetG", color_targetG));
		fw.write(getOutString("color_targetB", color_targetB));

		fw.write(getOutString("color_mulR", color_mulR));
		fw.write(getOutString("color_mulG", color_mulG));
		fw.write(getOutString("color_mulB", color_mulB));
		
		fw.flush();
		fw.close();
	}
	
	
	
	public String toString() {
		return "Config [checkCenter=" + checkCenter + ", communicateToRobot=" + communicateToRobot + ", debug_Print="
				+ debug_Print + ", debug_SaveImagesToFiles=" + debug_SaveImagesToFiles + ", debug_ShowDisplay="
				+ debug_ShowDisplay + ", minBlobSize=" + minBlobSize + ", scanWholeTile=" + scanWholeTile
				+ ", sensitivity=" + sensitivity + ", tileSize=" + tileSize + ", color_targetR=" + color_targetR
				+ ", color_targetG=" + color_targetG + ", color_targetB=" + color_targetB + ", color_mulR="
				+ color_mulR + ", color_mulG=" + color_mulG + ", color_mulB=" + color_mulB + "]";
	}

	private String getOutString(String key, Object value) {
		return key + " = " + value + "\n";
	}
	
	/**
	 * Should the tiling algorithm check the center of the tile, as well as the corners to determine if it should be
	 * considered for being in the target?
	 */
	public boolean	checkCenter				= true;
	
	/**
	 * Should this program attempt to communicate to the robot?
	 */
	public boolean	communicateToRobot		= true;
	
	/**
	 * Should debug info be shown?
	 * 
	 * This includes time per frame, number of visible targets, and estimated position of visible targets.
	 */
	public boolean	debug_Print				= false;
	
	public boolean	debug_SaveImagesToFiles	= false;
	
	public boolean	debug_ShowDisplay		= true;
	
	
	/**
	 * A calibration constant indicating the minimum size for a potential target to be considered. This number is given
	 * in square "tiles", with {@link #tileSize} pixels side lengths
	 */
	public int		minBlobSize				= 25;
	
	/**
	 * Should all pixels be scanned in every tile be scanned, or just the corners (and possibly center)
	 */
	public boolean	scanWholeTile			= false;
	
	/**
	 * A constant between -128 to +127 indicating how sensitive the color acceptance of the target should be. Lower
	 * numbers will allow more pixels, while higher numbers will eliminate more.
	 * 
	 * </br> This number needs to be chosen high enough to reduce or eliminate false positives, but it needs to be low
	 * enough to not generate false negatives.
	 */
	public byte		sensitivity				= -127;		// higher numbers means more rejected pixels
	
	/**
	 * The size of each tile in the vision processing. This is represented in pixels. It should be a number chosen large
	 * enough to have a good speed, but small enough to not miss a target in the image.
	 */
	public int		tileSize				= 5;
	
	public int color_targetR = 5;
	public int color_targetG = 140;
	public int color_targetB = 255;
	
	public double color_mulR = .2;
	public double color_mulG = .0005;
	public double color_mulB = .025;
	
}
