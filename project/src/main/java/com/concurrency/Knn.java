package com.concurrency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Knn {
    
    public static double calculateDistance(double[] p1, double[] p2){
        double sum = 0;
        int length = Math.min(p1.length, p2.length);
        
        for (int i = 0; i < length; i++){
            //double diff = p1[i] - p2[i];
            //sum += diff * diff;
            sum += Math.pow((p1[i]-p2[i]), 2);
            
        }
        
        return Math.sqrt(sum);
    }

    // Classe para juntar distance e target

    static class DistanceTarget{
        double distance;
        double hour;
    
        DistanceTarget(double d, double h){
            this.distance = d;
            this.hour = h;
        }
    }

    public static double regressor(List<Point> train, double[] pointC, int k){

        List<DistanceTarget> neighbors = new ArrayList<>();

        for (Point p : train) {
        double distance = calculateDistance(p.features, pointC);
           neighbors.add(new DistanceTarget(distance, p.hour));
            
        }

        Collections.sort(neighbors, Comparator.comparingDouble(dl -> dl.distance));


        double sumDistancias = 0;
        for (int i = 0; i < k; i++) {
            sumDistancias += neighbors.get(i).hour;
        }

        return sumDistancias/k;

    }
}

