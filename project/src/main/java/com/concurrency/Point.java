package com.concurrency;

public class Point {
    double[] features;
    double hour;

    public Point(double[] features, double hour){
        this.features = features;
        this.hour = hour;
    }

    public double[] getFeatures(){return features;}
}
