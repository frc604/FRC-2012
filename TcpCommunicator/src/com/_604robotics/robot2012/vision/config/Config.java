package com._604robotics.robot2012.vision.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.Icon;


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
	private static final File	defaultConfigFile;
	
	static {
		File normalDefaultFile = new File("vision.conf");
		//if FRC-2012/vision.conf exists, use that instead...
		
		File otherOne = new File("FRC-2012/vision.conf");
		
		if(otherOne.exists())
			defaultConfigFile = otherOne;
		else
			defaultConfigFile = normalDefaultFile;
	}
	
	
	private final HashMap<String, DataValue> dataMap = new HashMap<String, DataValue>();
	
	
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
				
				try {
					conf.setValue(key, value);
				} catch(Exception ex) {
					System.err.println("Had error with key '"+key+ "' for value: "+value);
					ex.printStackTrace();
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return conf;
	}

	void setValue(String key, Object value) throws NumberFormatException {
		setValue(key, value.toString()); // TODO - make better
	}

	void setValue(String key, String value) throws NumberFormatException {
		DataValue dv = getDataValue(key);
		
		Class<?> type = dv.getType();
		
		if(type.equals(Boolean.class)) {
			dv.value = Boolean.parseBoolean(value);
		} else if(type.equals(Double.class)) {
			dv.value = Double.parseDouble(value);
		} else if(type.equals(Integer.class)) {
			dv.value = Integer.parseInt(value);
		}
	}
	
	private DataValue getDataValue(String key) {
		return dataMap.get(key.toLowerCase());
	}

	/**
	 *
	 * A class for storing Config values
	 *
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	public static class DataValue implements Comparable<DataValue> {
		private String key;
		private Object value;
		private Object defValue;
		
		public DataValue(String k, Object v, Object def) {
			key = k;
			
			defValue = def;
			
			if(def == null) {
				throw new NullPointerException("The default value cannot be null.");
			}
			
			set(v);
		}
		
		public String getKey() {
			return key;
		}
		
		public Class<?> getType() {
			return defValue.getClass();
		}
		
		public void set(Object val) {
			if(val==null) {
				value = defValue;
				return;
			}
			
			if(!defValue.getClass().equals(val.getClass())) {
				throw new RuntimeException("The Value and default values were of different classes");
			}
			
			value = val;
		}
		
		public boolean getBoolean() {
			if(!(defValue instanceof Boolean))
				throw new RuntimeException("The value could not be parsed as a boolean.");
			
			return (Boolean) value;
		}
		public int getInt() {
			if(!(defValue instanceof Integer))
				throw new RuntimeException("The value could not be parsed as an int.");
			
			return (Integer) value;
		}
		public double getDouble() {
			if(defValue instanceof Integer) {
				return getInt();
			}
			
			if(!(defValue instanceof Double))
				throw new RuntimeException("The value could not be parsed as a double.");
			
			return (Double) value;
		}

		public int compareTo(DataValue dv) {
			return key.compareTo(dv.key);
		}
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
		
		ArrayList<DataValue> sorted = new ArrayList<DataValue>();
		sorted.addAll(dataMap.values());
		Collections.sort(sorted);

		for(DataValue dv: sorted) {
			fw.write(getOutString(dv.key, dv.value));
		}
		
		fw.flush();
		fw.close();
	}
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String ret = "";
		
		for(DataValue dv: dataMap.values()) {
			ret += "[" + dv.key + " = " + dv.value + "] ";
		}
		
		return ret;
		/*
		return "Config [checkCenter=" + checkCenter + ", communicateToRobot=" + communicateToRobot + ", debug_Print="
				+ debug_Print + ", debug_SaveImagesToFiles=" + debug_SaveImagesToFiles + ", debug_ShowDisplay="
				+ debug_ShowDisplay + ", minBlobSize=" + minBlobSize + ", scanWholeTile=" + scanWholeTile
				+ ", sensitivity=" + sensitivity + ", tileSize=" + tileSize + ", color_targetR=" + color_targetR
				+ ", color_targetG=" + color_targetG + ", color_targetB=" + color_targetB + ", color_mulR="
				+ color_mulR + ", color_mulG=" + color_mulG + ", color_mulB=" + color_mulB + "]";
				*/
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
	
	private final void addKeyValuePair(String key, Object defValue) {
		DataValue dv = new DataValue(key, defValue, defValue);
		
		dataMap.put(key.toLowerCase(), dv);
	}
	
	public Config() {
		/**
		 * Should the tiling algorithm check the center of the tile, as well as the corners to determine if it should be
		 * considered for being in the target?
		 */
		addKeyValuePair("checkCenter", true);
		
		/**
		 * Should this program attempt to communicate to the robot?
		 */
		addKeyValuePair("communicateToRobot", true);
		
		/**
		 * Should debug info be shown?
		 * 
		 * This includes time per frame, number of visible targets, and estimated position of visible targets.
		 */
		addKeyValuePair("debug_Print", false);
		
		/**
		 * Should camera images be stored onto disk, for debug purposes?
		 */
		addKeyValuePair("debug_SaveImagesToFiles", false);
		
		/**
		 * Should the fancy display be shown, with green and red tiles indicating matching and non-matching
		 * tiles, with blue lines and dots indicating target sides and corners?
		 */
		addKeyValuePair("debug_ShowDisplay", false);
		
		
		/**
		 * A calibration constant indicating the minimum size for a potential target to be considered. This number is given
		 * in square "tiles", with {@link #tileSize} pixels side lengths
		 */
		addKeyValuePair("minBlobSize", 25);
		
		/**
		 * Should all pixels be scanned in every tile be scanned, or just the corners (and possibly center)
		 */
		addKeyValuePair("scanWholeTile", false);
		
		/**
		 * A constant between -128 to +127 indicating how sensitive the color acceptance of the target should be. Lower
		 * numbers will allow more pixels, while higher numbers will eliminate more.
		 * 
		 * </br> This number needs to be chosen high enough to reduce or eliminate false positives, but it needs to be low
		 * enough to not generate false negatives.
		 */
		addKeyValuePair("sensitivity", -75);		// higher numbers means more rejected pixels
		
		/**
		 * The size of each tile in the vision processing. This is represented in pixels. It should be a number chosen large
		 * enough to have a good speed, but small enough to not miss a target in the image.
		 */
		addKeyValuePair("tileSize", 7);
		
		/**
		 * The color of the vision target when the light is shining on it
		 */
		//was (28, 168, 255) for blue light
		addKeyValuePair("color_targetR", 3);
		addKeyValuePair("color_targetG", 251);
		addKeyValuePair("color_targetB", 242);
		
		/**
		 * How much to multiply the square of the errors per color channel by
		 */
		addKeyValuePair("color_mulR", 0.0207);
		addKeyValuePair("color_mulG", 0.02);
		addKeyValuePair("color_mulB", 0.02);

		
		/**
		 * The width of the vision target, in inches
		 */
		addKeyValuePair("targetWidth", 24.0);

		/**
		 * The height of the vision target, in inches
		 */
		addKeyValuePair("targetHeight", 18.0);

		/**
		 * How far the camera is pointing down, in degrees
		 */
		addKeyValuePair("camAngle", -18.0);

		/**
		 * How far the camera is pointing left and right, in degrees
		 */
		addKeyValuePair("camAngleLR", -3.5);

		/**
		 * Horizontal offset of the camera, in inches
		 */
		addKeyValuePair("camOffsetLR", -3.5);

		
		/**
		 * How many horizontal pixels away from center a line is that goes out at the same rate it goes right
		 */
		addKeyValuePair("kx", 700.0);								// was 736, then 605; then 695-705
		
		/**
		 * How many vertical pixels away from center a line is that goes out at the same rate it goes up
		 */
		addKeyValuePair("ky", 530.0);								// was 736, then 605; then 695-705

		/**
		 * How big is "green" on the "aimed indicator" in inches?
		 */
		addKeyValuePair("aimedIndicator_greenSize", 3.0);
		
		/**
		 * How big is "orange" on the "aimed indicator" in inches?
		 * If you don't want orange displayed, set it to 0.
		 */
		addKeyValuePair("aimedIndicator_orangeSize", 5.0);

		/**
		 * How big is an inch on the "aimed indicator" in pixels?
		 */
		addKeyValuePair("aimedIndicator_inchWidth", 40);
		
		/**
		 * How much less should the "aimed indicator" bar move when it's outside the green range?
		 * 
		 * default (.5) means 
		 */
		addKeyValuePair("aimedIndicator_outsideScalingFactor", .5);
	
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return dataMap.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Config other = (Config) obj;
		if (dataMap == null) {
			if (other.dataMap != null)
				return false;
		} else if (!dataMap.equals(other.dataMap))
			return false;
		return true;
	}

	public Object get(String key) {
		return this.getDataValue(key).value;
	}
	public boolean getBoolean(String key) {
		return this.getDataValue(key).getBoolean();
	}
	public int getInt(String key) {
		return this.getDataValue(key).getInt();
	}
	public double getDouble(String key) {
		return this.getDataValue(key).getDouble();
	}
	
}
