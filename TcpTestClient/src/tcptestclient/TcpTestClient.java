package tcptestclient;

import com._604robotics.robot2012.Aiming.PointAndAngle3d;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpTestClient {
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
    
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(3333);
            Socket conn;
            InputStream in;
            
            int packetId;
            PointAndAngle3d p;
            
            while (true) {
                System.out.println("Listening...");
                
                conn = server.accept();
                in = conn.getInputStream();
                
                while ((packetId = in.read()) != -1) {
                    switch (packetId) {
                        case 0:
                            p = new PointAndAngle3d();
                            
                            if ((p.x = readDouble(in)) == -1)
                                break;
                            if ((p.y = readDouble(in)) == -1)
                                break;
                            if ((p.z = readDouble(in)) == -1)
                                break;
                            if ((p.angle = readDouble(in)) == -1)
                                break;
                            
                            System.out.println("x: " + p.x + ", y: " + p.y + ", z: " + p.z + ", angle: " + p.angle);
                            
                            break;
                        case 1:
                            System.out.println("---------------------");
                            
                            break;
                    }
                }
                
                try {
                    in.close();
                    conn.close();
                } catch(Exception ex) {
                    
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}