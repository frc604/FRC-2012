package com._604robotics.robot2012.vision.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


/**
 * The configuration of the Team 604 FRCVision
 * 
 *
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class Config {
	
	/**
	 * The default Config file
	 */
	private static final File	defaultConfigFile	= new File("vision.conf");
	
	
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
	
	/**
	 * Parses a boolean
	 * 
	 * @param str	The string to parse
	 * @param def	The default value
	 * @return	a boolean
	 */
	static boolean parseBoolean(String str, boolean def) {
		try {
			return Boolean.parseBoolean(str);
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Parses a byte
	 * 
	 * @param str	The string to parse
	 * @param def	The default value
	 * @return	a byte
	 */
	static byte parseByte(String str, byte def) {
		try {
			return Byte.parseByte(str);
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Parses a double
	 * 
	 * @param str	The string to parse
	 * @param def	The default value
	 * @return	a double
	 */
	static double parseDouble(String str, double def) {
		try {
			return Double.parseDouble(str);
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Parses an int
	 * 
	 * @param str	The string to parse
	 * @param def	The default value
	 * @return	an int
	 */
	static int parseInt(String str, int def) {
		try {
			return Integer.parseInt(str);
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Reads the default Config file
	 * 
	 * @return the Config, as read from vision.conf
	 */
	public static Config readDefaultConfig() {
		return readConfig(defaultConfigFile);
	}
	
	/**
	 * Saves this Config to the default file
	 * 
	 * @throws IOException	If an error occurs
	 */
	public void saveDefaultConfig() throws IOException {
		save(defaultConfigFile);
	}
	
	/**
	 * Read a Config from a file
	 * 
	 * @param file	the file to read it from
	 * @return	the Config read from the file
	 */
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
	
	/**
	 * Saves this Config to a given file
	 * 
	 * @param file	The file to save to
	 * @throws IOException	If an error occurs
	 */
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
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Config [checkCenter=" + checkCenter + ", communicateToRobot=" + communicateToRobot + ", debug_Print="
				+ debug_Print + ", debug_SaveImagesToFiles=" + debug_SaveImagesToFiles + ", debug_ShowDisplay="
				+ debug_ShowDisplay + ", minBlobSize=" + minBlobSize + ", scanWholeTile=" + scanWholeTile
				+ ", sensitivity=" + sensitivity + ", tileSize=" + tileSize + ", color_targetR=" + color_targetR
				+ ", color_targetG=" + color_targetG + ", color_targetB=" + color_targetB + ", color_mulR="
				+ color_mulR + ", color_mulG=" + color_mulG + ", color_mulB=" + color_mulB + "]";
	}

	/**
	 * Turn a key-value pair into a string
	 * 
	 * @param key	The key
	 * @param value	The value
	 * @return	A string
	 */
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
	
	/**
	 * Should camera images be stored onto disk, for debug purposes?
	 */
	public boolean	debug_SaveImagesToFiles	= false;
	
	/**
	 * Should the fancy display be shown, with green and red tiles indicating matching and non-matching
	 * tiles, with blue lines and dots indicating target sides and corners?
	 */
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
	
	/**
	 * The color of the vision target when the light is shining on it
	 */
	public int color_targetR = 5, color_targetG = 140, color_targetB = 255;
	
	/**
	 * How much to multiply the square of the errors per color channel by
	 */
	public double color_mulR = .2, color_mulG = .0005, color_mulB = .025;
	
}
