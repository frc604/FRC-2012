package logging;
import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import com._604robotics.robot2012.vision.VisionProcessing;



public class Logger {
	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Vision");
	static {
		new File("logs").mkdir();
		logger.setLevel(Level.ALL);
		try {
			if(VisionProcessing.defaultProcessing.conf.getBoolean("debug_Print"))
				logger.addHandler(new FileHandler("logs/vision_log_"+System.currentTimeMillis()+".log"));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("The log couldn't log to a file.");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex1) {}
			System.err.println("This is very bad.");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex1) {}
			System.err.println("http://simple.wikipedia.org/wiki/Exception_handling");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException ex1) {}
			
			
		}
	}
	
	public static void log(Object o) {
		if (VisionProcessing.defaultProcessing.conf.getBoolean("debug_Print")) {
			logger.info(o.toString());
		}
	}
	public static void err(Object o) {
		logger.severe(o.toString());
	}
	public static void warn(Object o) {
		logger.warning(o.toString());
	}
	public static void ex(Exception ex) {
		logger.throwing("?", "?", ex);
	}
	
	public static void main(String[] args) {
		Logger.log("Hai");
	}
}
