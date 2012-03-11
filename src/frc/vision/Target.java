package frc.vision;

/**
 * An Object to hold target parameters.
 * 
 * @author  Kevin Parker <kevin.m.parker@gmail.com>
 * @author  Sebastian Merz <merzbasti95@gmail.com>
 * 
 */
public class Target {
    public int x1, y1, w, h;
    
    /**
     * Blank constructor.  Does nothing.
     */
    public Target() {
        
    }
    
    /**
     * 
     * @param x1    The left x value for the target.
     * @param y1    The bottom y value for the target.
     * @param w     The width of the target.
     * @param h     The height of the target.
     */
    public Target(int x1, int y1, int w, int h) {
        this.x1 = x1;
        this.y1 = y1;
        this.w = w;
        this.h = h;
    }
}