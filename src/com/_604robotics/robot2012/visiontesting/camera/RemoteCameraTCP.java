package com._604robotics.robot2012.visiontesting.camera;

import com._604robotics.robot2012.Aiming.Aiming;
import com._604robotics.robot2012.Aiming.Point3d;
import edu.wpi.first.wpilibj.Timer;
import frc.vision.Target;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;

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

    public Point3d[] getTargets () {
        return this.server.points;
    }
    
    public int getUPS() {
        return this.server.ups;
    }
}

class RemoteCameraServer implements Runnable {
    private Aiming aiming = new Aiming();
    
    public boolean enabled = false;
    public Point3d[] points = new Point3d[] {  };
    public int ups = 0;
    
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
    
    public void run () {
        System.out.println("TCP task running.");
        
        Point3d p = new Point3d();
        
        ServerSocketConnection server = null;
        SocketConnection conn;
        InputStream in;
        
        int packetId = 0;
        
        Vector pts = new Vector();
        Point3d[] iter;
        
        Timer second = new Timer();
        int u = 0;
        
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
                conn = (SocketConnection) server.acceptAndOpen();
                
                System.out.println("Connection received.");
                
                in = conn.openInputStream();
                
                pts.removeAllElements();
                
                u = 0;
                
                second.reset();
                second.start();

                while (this.enabled && (packetId = in.read()) != -1) {
                    switch (packetId) {
                        case 0:
                            p = new Point3d();
                            
                            if ((p.x = readDouble(in)) == -1)
                                break;
                            if ((p.y = readDouble(in)) == -1)
                                break;
                            if ((p.z = readDouble(in)) == -1)
                                break;
                            
                            pts.addElement(p);
                            
                            break;
                        case 1:
                            iter = new Point3d[pts.size()];
                            
                            for (int i = 0; i < pts.size(); i++)
                                iter[i] = (Point3d) pts.elementAt(i);
                            
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
        
        System.out.println("TCP task stopped.");
    }
}