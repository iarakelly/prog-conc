package com.concurrency;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;



public class Knn {
    
    public static double calculateDistance(double[] p1, double[] p2){
        double sum = 0;
        int length = Math.min(p1.length, p2.length);

        for (int i = 0; i < length; i++){
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

    public static String classifier(List<Point> train, double[] pointC, int k, ExecutorService executor) throws InterruptedException, ExecutionException{

        // Treinar é armazenar os dados, no knn
        // K é o número de vizinhos

        // Em vez de uma lista de 500k objetos a PriorityQueue 
        // guarda apenas os K vizinhos mais próximos.
        // Invertemos a ordem (Max-Heap) para remover sempre o mais distante dos K.

        // Define o número de threads (baseado nos núcleos da CPU)
        int numThreads = Runtime.getRuntime().availableProcessors();
        //ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = (int) Math.ceil((double) train.size() / numThreads);
        List<Callable<List<DistanceLabel>>> tasks = new ArrayList<>();
        //List<DistanceLabel> neighbors = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, train.size());
            
            if (start >= train.size()) break;

            List<Point> subList = train.subList(start, end);
            tasks.add(() -> {
                // Cada thread mantém sua própria PriorityQueue local para os K vizinhos
                PriorityQueue<DistanceLabel> localPq = new PriorityQueue<>(
                    k, (a, b) -> Double.compare(b.distance, a.distance));

                for (Point p : subList) {
                    double distance = calculateDistance(p.features, pointC);
                    if (localPq.size() < k) {
                        localPq.add(new DistanceLabel(distance, p.label));
                    } else if (distance < localPq.peek().distance) {
                        localPq.poll();
                        localPq.add(new DistanceLabel(distance, p.label));
                    }
                }
                return new ArrayList<>(localPq);
            });
        }

        List<Future<List<DistanceLabel>>> futures = executor.invokeAll(tasks);

        PriorityQueue<DistanceLabel> globalPq = new PriorityQueue<>(
                k, (a, b) -> Double.compare(b.distance, a.distance));
        
        for (Future<List<DistanceLabel>> future : futures) {
            for (DistanceLabel dl : future.get()) {
                if (globalPq.size() < k) {
                    globalPq.add(dl);
                } else if (dl.distance < globalPq.peek().distance) {
                    globalPq.poll();
                    globalPq.add(dl);
                }
            }
        }

        HashMap<String, Integer> votes = new HashMap<>();
        while (!globalPq.isEmpty()) {
            String s = globalPq.poll().label;
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

