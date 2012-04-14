package com._604robotics.robot2012.learning;

import java.util.Vector;

/**
 *
 * @author Michael Smith <mdsmtp@gmail.com>
 */
public class AveragingTutor implements Tutor {
    private static final double NaN = Double.NaN;
    private final Vector bounds = new Vector();
    
    private double distance = 0D;
    
    private double minTest = 0;
    private double maxTest = 1;
    
    private double minGood = NaN;
    private double maxGood = NaN;
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        Tuner t = new Tuner();

        do {

            double val = t.getNextTest();

            System.out.println("Trying " + val);


            String str = s.nextLine();

            if (str.equals("good")) {
                t.goodRobot(val);
            } else if (str.equals("low")) {
                t.badRobotLow(val);
            } else if (str.equals("high")) {
                t.badRobotHigh(val);
            } else {
                System.out.println("???");
            }
        } while (!(t.getTolerance() < .01));

        System.out.println("Done!!!");

        System.out.println("Bounds = [" + t.minGood + " -> " + t.maxGood + "]");
    }

    public void configure(double distance) {
        this.distance = distance;
    }

    public double shoot() {
        double avg = .5 * (minTest + maxTest);

        /*
         * If a good shot has been found...
         */
        if (minGood == minGood) {

            double dLow = minGood - minTest;
            double dHigh = maxTest - maxGood;

            if (dLow > dHigh) {
                System.out.println(" -----> New Tolerance: " + dLow);
                return .5 * (minTest + minGood); // avg between bottom test and bottom good
            } else {
                System.out.println(" -----> New Tolerance: " + dHigh);
                return .5 * (maxTest + maxGood); // avg between top test and top good
            }
        }

        System.out.println(" -----> New Tolerance: " + (maxTest - minTest));

        return avg;
    }

    public void feedback(int term) {
    }

    public Bounds[] getData() {
        return this.bounds;
    }

    public static class Tuner {

        /**
         * @return the previous tolerance
         */
        public double getTolerance() {
            return prevTolerance;
        }

        public double getNextTest() {
             // average of values
        }

        public void goodRobot(double value) {
            minGood = min(value, minGood);
            maxGood = max(value, maxGood);
        }

        public void badRobotLow(double value) {
            minTest = value;
        }

        public void badRobotHigh(double value) {
            maxTest = value;
        }

        private double min(double a, double b) {
            if (a != a) {
                return b;
            }
            if (b != b) {
                return a;
            }

            return Math.min(a, b);
        }

        private double max(double a, double b) {
            if (a != a) {
                return b;
            }
            if (b != b) {
                return a;
            }

            return Math.max(a, b);
        }
    }
}
