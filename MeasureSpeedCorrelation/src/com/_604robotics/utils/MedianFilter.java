package com._604robotics.utils;

public class MedianFilter {
    private static final int MAX = 2000000;
    private static final int NUM = 4000;
    
    private double[] t = new double[MAX];
    private double[] a = new double[MAX];
    private double[] bb = new double[MAX];
    
    private double ave, aa;
    
    private double middle;
    private double[] c = new double[NUM];
    
    private double temp;
    
    private long jk, k;
    
    private long m, mm;
    private long np;
    private long last;
    private long i, j, w;
    
    private long iv = 0;
    
    public MedianFilter () {
        
    }
}
