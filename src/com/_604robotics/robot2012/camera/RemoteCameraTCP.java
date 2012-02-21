package com._604robotics.robot2012.camera;

import com._604robotics.robot2012.Aiming.Aiming;
import com._604robotics.robot2012.Aiming.PointAndAngle3d;
import edu.wpi.first.wpilibj.Timer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;

/**
 * Implements a CameraInterface that draws data from a TCP connection.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
public class RemoteCameraTCP implements CameraInterface {
    private double started = new Date().getTime();
    private double last = 0;
    
    private RemoteCameraServer server = new RemoteCameraServer();
    private Thread thread;
    
    public void begin() {
        this.server.enabled = true;
        
        this.thread = new Thread(server);
        this.thread.start();
    }

    public void end() {
        this.server.enabled = false;
    }

    public PointAndAngle3d[] getTargets () {
        return this.server.points;
    }
    
    /**
     * Returns the number of updates received per second.
     * 
     * For testing and debugging purposes.
     * 
     * @return  The number of updates per second.
     */
    public int getUPS() {
        return this.server.ups;
    }
}

/** 
 * Internal server class that does the actual heavy lifting.
 * 
 * @author  Michael Smith <mdsmtp@gmail.com>
 */
class RemoteCameraServer implements Runnable {
    private Aiming aiming = new Aiming();
    
    public boolean enabled = false;
    public PointAndAngle3d[] points = new PointAndAngle3d[] {  };
    public int ups = 0;
    
    /**
     * Reads a double from the stream.
     * 
     * @param   in  The stream to read from.
     * 
     * @return  The double read.
     */
    private static double readDouble (InputStream in) {
        long process = 0;
        int curr = 0;
        
        try {
            for (int i = 0; i < 8; i++) {
                curr = in.read();
                if (curr == -1) return -1D;
                
                process = (process << 8) + (((byte) curr) & 0xff);
            }
            
            return Double.longBitsToDouble(process);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Reads a float from the stream.
     * 
     * @param   in  The stream to read from.
     * 
     * @return The float read.
     */
    private static double readFloat (InputStream in) {
        int process = 0;
        int curr = 0;
        
        try {
            for (int i = 0; i < 4; i++) {
                curr = in.read();
                if (curr == -1) return -1D;
                
                process = (process << 8) + (((byte) curr) & 0xff);
            }
            
            return Float.intBitsToFloat(process);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Runs the main thread.
     * 
     * Implements the run() method in type Runnable.
     */
    public void run () {
        System.out.println("TCP task running.");
        
        PointAndAngle3d p;
        
        ServerSocketConnection server = null;
        SocketConnection conn;
        InputStream in;
        
        int packetId = 0;
        
        Vector pts = new Vector();
        PointAndAngle3d[] iter;
        
        Timer second = new Timer();
        int u = 0;
        
        /* Repeatedly attempts to launch the server until success. */
        
        while (this.enabled && server == null) {
            try {
                System.out.println("Launching server...");
                server = (ServerSocketConnection) Connector.open("socket://:" + 3333);
                System.out.println("Launched server.");
            } catch (IOException ex) {
                server = null;
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex1) {
                    ex1.printStackTrace();
                }
            }
        }
        
        while (this.enabled) {
            try {
                /* Accepts a client and prepares to receive data. */
                
                conn = (SocketConnection) server.acceptAndOpen();
                
                System.out.println("Connection received.");
                
                in = conn.openInputStream();
                
                pts.removeAllElements();
                
                u = 0;
                
                second.reset();
                second.start();

                /* Processes incoming packets. */
                
                while (this.enabled && (packetId = in.read()) != -1) {
                    switch (packetId) {
                        case 0:
                            /*
                            * Processes a target packet. Reads in four doubles,
                            * and assigns them to the x, y, z, and angle of a
                            * new PointAndAngle3d, respectively, which it then
                            * adds to the point queue.
                            */
                            
                            p = new PointAndAngle3d();
                            
                            if ((p.x = readDouble(in)) == -1)
                                break;
                            if ((p.y = readDouble(in)) == -1)
                                break;
                            if ((p.z = readDouble(in)) == -1)
                                break;
                            if ((p.angle = readDouble(in)) == -1)
                                break;
                            
                            pts.addElement(p);
                            
                            break;
                        case 1:
                            /*
                             * Processes an "end of targets" packet, pushing the
                             * point queue onto the main accessible variable,
                             * cleaning things up, and preparing to receive
                             * another batch of points.
                             */
                            
                            iter = new PointAndAngle3d[pts.size()];
                            
                            for (int i = 0; i < pts.size(); i++)
                                iter[i] = (PointAndAngle3d) pts.elementAt(i);
                            
                            this.points = iter;
                            pts.removeAllElements();
                            
                            if (second.get() >= 1) {
                                this.ups = u;
                                u = 0;
                                second.reset();
                            }
                            
                            u++;
                            
                            break;
                    }
                }
                
                /* The client has disconnected. Clean things up and reset. */
                
                try {
                    in.close();
                    conn.close();
                } catch(Exception ex) {
                    
                }
                
                second.stop();
                
                System.out.println("Connection closed.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        /* The task has been disabled. Stop. */
        
        System.out.println("TCP task stopped.");
    }
}