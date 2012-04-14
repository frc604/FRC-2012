package com._604robotics.utils;

import com.sun.squawk.util.MathUtils;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class SmarterDashboard {

	public static double getDouble(String key, double def) {
		try {
			return SmartDashboard.getDouble(key, def);
		} catch (Exception ex) {
			return def;
		}
	}
	
	public static String repeatString(String what, int times) {
		String ret = "";
		for (int i = 0; i < times; i++)
			ret += what;
		return ret;
	}
	
	public static String renderDebug(double x) {
		if (Math.abs(x) > 15)
			return repeatString("-", 31);
		String ret = repeatString(" ", 15) + "^" + repeatString(" ", 15) + "\n";
		int position = (int) MathUtils.round(x / 30 * 15);
		for (int i = -15; i <= 15; i++)
			ret += (i == position)
			? '|'
					: ' ';
		ret += "\n" + repeatString(" ", 15) + "^" + repeatString(" ", 15);
		return ret;
	}
}
