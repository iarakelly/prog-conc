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
        for (int i = 0; i < p1.length; i++){
            double diff = p1[i] - p2[i];
            sum += diff * diff;
            //sum += Math.pow((p1[i]-p2[i]), 2);
            
        }
        
        return Math.sqrt(sum);
    }

    //CLasse para juntar distance com label

    static class DistanceLabel{
        double distance;
        String label;
    
        DistanceLabel(double d, String r){
            this.distance = d;
            this.label = r;
        }
    }

    public static String classifier(List<Point> train, double[] pointC, int k){

        // Treinar é armazenar os dados, no knn
        // K é o número de vizinhos

        // Em vez de uma lista de 500k objetos a PriorityQueue 
        // guarda apenas os K vizinhos mais próximos.
        // Invertemos a ordem (Max-Heap) para remover sempre o mais distante dos K.

        java.util.PriorityQueue<DistanceLabel> pq = new java.util.PriorityQueue<>(
        k, (a, b) -> Double.compare(b.distance, a.distance));

        for (Point p : train) {
        double distance = calculateDistance(p.features, pointC);
        
            if (pq.size() < k) {
                pq.add(new DistanceLabel(distance, p.label));
            } else if (distance < pq.peek().distance) {
                pq.poll(); // Remove o mais longe dos K atuais
                pq.add(new DistanceLabel(distance, p.label));
            }
        }

        List<DistanceLabel> neighbors = new ArrayList<>();

        for (Point p : train){
            double distance = calculateDistance(p.features, pointC);
            neighbors.add(new DistanceLabel(distance, p.label)); // criando objeto novo

        }

        Collections.sort(neighbors, Comparator.comparingDouble(dl -> dl.distance)); //Pegue um objeto da lista

        HashMap<String, Integer> votes = new HashMap<>();
        while (!pq.isEmpty()) {
            String s = pq.poll().label;
            votes.put(s, votes.getOrDefault(s, 0) + 1);
        }
    /*
    for(int i = 0; i < k; i++){
        String s = neighbors.get(i).label;

        votes.put(s, votes.getOrDefault(s, 0) + 1); // pega o contador ou inicia default como 0

    } 
    */    

        return Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();

    }
}

