package com._604robotics.tcpcommunicator;

import com._604robotics.robot2012.vision.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import logging.Logger;

/**
 * TURTLE protocol
 * 
 * 
 * Target
 * Upload to
 * Robot
 * Transfered from
 * Laptop
 * Exchange
 * protocol
 * 
 * 
 * 
 * Server class for the vision data transfer protocol.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 */
public class TcpCommunicator implements Runnable {
	private final String ip;
	private final int port;
	private final boolean debug;
	
	private Socket conn = null;
	private BufferedOutputStream bufferedOut = null;
	private OutputStream rawOut = null;
	
	private boolean enabled = false;
	private Thread thread;
	
	/**
	 * Initializes a new TcpCommunicator.
	 * 
	 * By default, the robot IP address is set to "10.6.4.2", the port is set
	 * to 3333, and the debug mode is set to TRUE.
	 */
	public TcpCommunicator () {
		this.ip = "10.6.4.2";
		this.port = 3333;
		this.debug = true;
	}
	
	/**
	 * Initializes a new TcpCommunicator with the specified robot IP address.
	 * 
	 * By default, the port is set to 3333 and the debug mode is set to TRUE.
	 * 
	 * @param   ip    The IP address of the robot.
	 */
	public TcpCommunicator (String ip) {
		this.ip = ip;
		this.port = 3333;
		this.debug = true;
	}
	
	/**
	 * Initializes a new TcpCommunicator with the specified robot IP address
	 * and port.
	 * 
	 * By default, the debug mode is set to TRUE.
	 * 
	 * @param   ip      The IP address of the robot.
	 * @param   port    The port to connect to.
	 */
	public TcpCommunicator (String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.debug = true;
	}
	
	/**
	 * Initializes a new TcpCommunicator with the specified robot IP address,
	 * port, and debug mode.
	 * 
	 * @param   ip      The IP address of the robot.
	 * @param   port    The port to connect to.
	 * @param   debug   Print debug info?
	 */
	public TcpCommunicator (String ip, int port, boolean debug) {
		this.ip = ip;
		this.port = port;
		this.debug = debug;
	}
	
	/**
	 * Checks whether or not the TcpCommunicator has been enabled.
	 * 
	 * @return  Whether or not the TcpCommunicator has been enabled.
	 */
	public boolean isEnabled () {
		return this.enabled;
	}
	
	/**
	 * Checks whether or not the TcpCommunicator thread is currently running.
	 * 
	 * @return  Whether or not the TcpCommunicator thread is currently running.
	 */
	public boolean isRunning () {
		return this.thread != null && this.thread.isAlive();
	}
	
	/**
	 * Enables the TcpCommunicator, launching the thread.
	 */
	public void up () {
		if (this.isRunning())
			return;
		
		this.enabled = true;
		
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	/**
	 * Disables the TcpCommunicator.
	 */
	public void down() {
		this.enabled = false;
	}
	
	/**
	 * Interrupts the TcpCommunicator thread, forcing it to quit.
	 * 
	 * Use only in emergencies!
	 */
	public void forceQuit() {
		this.enabled = false;
		
		if (this.thread != null)
			this.thread.interrupt();
	}
	
	/**
	 * Writes a double to the stream.
	 * 
	 * @param   val    The double to write.
	 * 
	 * @throws  IOException
	 * @throws  NullPointerException 
	 */
	private void writeDouble (double val) throws IOException, NullPointerException {
		long process = Double.doubleToLongBits(val);
		
		this.bufferedOut.write(new byte[] {
				(byte)((process >> 56) & 0xff),
				(byte)((process >> 48) & 0xff),
				(byte)((process >> 40) & 0xff),
				(byte)((process >> 32) & 0xff),
				(byte)((process >> 24) & 0xff),
				(byte)((process >> 16) & 0xff),
				(byte)((process >> 8 ) & 0xff),
				(byte)((process >> 0) & 0xff)
		});
	}
	
	/**
	 * Writes a float to the stream.
	 * 
	 * @param   val    The float to write.
	 * 
	 * @throws  IOException
	 * @throws  NullPointerException 
	 */
	private void writeFloat (float val) throws IOException, NullPointerException {
		int process = Float.floatToIntBits(val);
		
		this.bufferedOut.write(new byte[] {
				(byte)((process >> 24) & 0xff),
				(byte)((process >> 16) & 0xff),
				(byte)((process >> 8 ) & 0xff),
				(byte)((process >> 0) & 0xff)
		});
	}
	
	/**
	 * Writes the specified points to the stream.
	 * 
	 * If there is no robot currently connected, it fails silently and discards
	 * the points into the ether.
	 * 
	 * @param   points  An array of Targets to write.
	 */
	public void writePoints (Target[] points) {
		if (this.conn == null || this.bufferedOut == null)
			return;
		
		try {
			for (int i = 0; i < points.length; i++) {
				if (points[i] == null)
					continue;
				
				this.bufferedOut.write((byte) 0);
				
				this.writeDouble(points[i].x);
				this.writeDouble(points[i].y);
				this.writeDouble(points[i].z);
				this.writeDouble(points[i].angle);
				
				this.writeDouble(points[i].xUncertainty);
				this.writeDouble(points[i].yUncertainty);
				this.writeDouble(points[i].zUncertainty);
				this.writeDouble(points[i].angleUncertainty);
				
				final OutputStream flushable = bufferedOut;
				
				Thread flusherThread = new Thread(new Runnable() {
					public void run() {
						try {
							flushable.flush();
						} catch (IOException ex) {
							//Logger.ex(ex);
							try {
								flushable.close();
							} catch (IOException ex1) {
								//Logger.ex(ex1);
							}
						}
					}
				});
				
				flusherThread.start();
			}
			
			this.bufferedOut.write((byte) 1);
		} catch (Exception ex) {
			
		}
	}
	
	/**
	 * Don't use this to launch the server; use up() instead.
	 * 
	 * This implements the run() method of type Runnable, allowing this to be
	 * run as a thread. For internal use!
	 */    
	@Override
	public void run() {
		InputStream in;
		
		while (this.enabled) {
			if (this.debug)
				Logger.log("[TCP] Connecting...");
			
			/* Repeatedly attempts to establish a connection with the robot. */
			
			while (this.conn == null) {
				try {
					this.conn = new Socket(this.ip, this.port);
					
					try {
						this.rawOut = this.conn.getOutputStream();
						this.bufferedOut = new BufferedOutputStream(this.rawOut, 4096); // buffer size = 4 KB
					} catch (Exception ex) {
						try {
							this.conn.close();
						} catch (IOException ex2) {
							
						}
						
						this.conn = null;
						this.bufferedOut = null;
						
						try {
							
							Thread.sleep(250);
						} catch (InterruptedException ex2) {
							
						}
					}
				} catch (Exception ex) {
					this.conn = null;
					
					try {
						Thread.sleep(250);
					} catch (InterruptedException ex2) {
						
					}
				}
			}
			
			if (this.debug)
				Logger.log("[TCP] Connected.");
			
			/* Monitors the socket, checking for a disconnection. */
			
			try {
				in = conn.getInputStream();
				while (in.read() != -1);
				in.close();
			} catch (IOException ex) {
				
			}
			
			/* Client has disconnected; clean up and loop around. */
			
			in = null;
			
			try {
				this.bufferedOut.close();
			} catch (IOException ex) {
				
			}
			
			try {
				this.conn.close();
			} catch (IOException ex) {
				
			}
			
			this.conn = null;
			this.bufferedOut = null;
			
			if(this.debug)
				Logger.err("[TCP] Connection lost.");
		}
	}
	
	
	/**
	 * For testing purposes.
	 * 
	 * Run this as an application, and it will connect to 127.0.0.1 and stream
	 * arbitrary data for testing purposes.
	 * 
	 * @param   args    Command-line arguments. Not currently used.
	 */
	public static void main (String[] args) {
		TcpCommunicator instance = new TcpCommunicator("127.0.0.1");
		instance.up();
		
		while (true) {
			instance.writePoints(new Target[] { new Target(123.456, 234.568, 0.38, 0.0), new Target(0.123, 0.0, 489.1, 1.2), new Target(99.999, 239.2, 39.01, 333.4492), new Target(100.1, 215.33, 301.0, 9993.23193) });
			
			try {
				Thread.sleep(30);
			} catch (InterruptedException  ex) {
				
			}
		}
	}
}