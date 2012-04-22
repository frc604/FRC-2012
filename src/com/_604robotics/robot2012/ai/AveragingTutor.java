package com._604robotics.robot2012.ai;

import java.util.Vector;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class AveragingTutor implements Tutor {
    private static final double NaN = Double.NaN;
    private final Vector bounds = new Vector();
    
    private double distance = 0D;
    private double lastShot = 0D;
    
    private double minTest = 0;
    private double maxTest = 1;
    
    private double minGood = NaN;
    private double maxGood = NaN;

    private static double min (double a, double b) {
        if (a != a) {
            return b;
        }
        if (b != b) {
            return a;
        }

        return Math.min(a, b);
    }

    private static double max (double a, double b) {
        if (a != a) {
            return b;
        }
        if (b != b) {
            return a;
        }

        return Math.max(a, b);
    }
    
    public void configure (double distance) {
        this.distance = distance;
    }
    
    public void record () {
        this.bounds.addElement(new Bounds(distance, minGood, maxGood));
    }

    public double shoot () {
        double avg = .5 * (minTest + maxTest);

        /*
         * If a good shot has been found...
         */
        if (minGood == minGood) {
            double dLow = minGood - minTest;
            double dHigh = maxTest - maxGood;

            if (dLow > dHigh) {
                System.out.println(" -----> New Tolerance: " + dLow);
                
                lastShot = .5 * (minTest + minGood); // avg between bottom test and bottom good;
                return lastShot;
            } else {
                System.out.println(" -----> New Tolerance: " + dHigh);
                
                lastShot = .5 * (maxTest + maxGood); // avg between top test and top good
                return lastShot;
            }
        }

        System.out.println(" -----> New Tolerance: " + (maxTest - minTest));

        lastShot = avg;
        return lastShot;
    }

    public void feedback (int term) {
        switch (term) {
            case 0:
                minGood = min(lastShot, minGood);
                maxGood = max(lastShot, maxGood);
                break;
            case -1:
                minTest = lastShot;
                break;
            case 1:
                maxTest = lastShot;
                break;
        }
    }

    public Vector getData () {
        return this.bounds;
    }
}
