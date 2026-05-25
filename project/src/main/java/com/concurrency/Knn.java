package com.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Knn {

    public static double calculateDistance(double[] p1, double[] p2){
        double sum = 0;
        int length = Math.min(p1.length, p2.length);

        for (int i = 0; i < length; i++){
            double diff = p1[i] - p2[i];
            sum += diff * diff;
        }
        
        return Math.sqrt(sum);
    }

    static class DistanceTarget {
        double distance;
        double target; 
    
        DistanceTarget(double d, double t){
            this.distance = d;
            this.target = t;
        }
    }

    // Classe interna para encapsular o trabalho de cada thread manual
    private static class KnnTask implements Runnable {
        private final List<Point> subList;
        private final double[] pointC;
        private final int k;
        private final List<DistanceTarget> result = new ArrayList<>();

        KnnTask(List<Point> subList, double[] pointC, int k) {
            this.subList = subList;
            this.pointC = pointC;
            this.k = k;
        }

        @Override
        public void run() {
            List<DistanceTarget> localList = new ArrayList<>();

            for (Point p : subList) {
                double distance = calculateDistance(p.features, pointC);
                localList.add(new DistanceTarget(distance, p.hour));
            }
            
            // Ordena e isola os K menores locais da thread
            localList.sort((a, b) -> Double.compare(a.distance, b.distance));
            
            int limit = Math.min(k, localList.size());
            for (int i = 0; i < limit; i++) {
                result.add(localList.get(i));
            }
        }

        public List<DistanceTarget> getResult() {
            return result;
        }
    }

    public static double regressor(List<Point> train, double[] pointC, int k) throws InterruptedException, ExecutionException {
        
        int numThreads = Runtime.getRuntime().availableProcessors();
        int chunkSize = (int) Math.ceil((double) train.size() / numThreads);
        
        List<Thread> threads = new ArrayList<>();
        List<KnnTask> tasks = new ArrayList<>();

        // 1. Criação e inicialização manual das threads
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, train.size());

            if (start >= train.size()) break;

            List<Point> subList = train.subList(start, end);
            KnnTask task = new KnnTask(subList, pointC, k);
            tasks.add(task);

            // Instancia uma Platform Thread manualmente
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start(); 
        }

        // 2. Sincronização manual (Aguarda o término de todas as threads)
        for (Thread thread : threads) {
            thread.join();
        }

        // 3. Consolidação global dos resultados das threads
        List<DistanceTarget> globalList = new ArrayList<>();
        for (KnnTask task : tasks) {
            globalList.addAll(task.getResult());
        }

        // Ordenação final do funil para obter os K vizinhos mais próximos do dataset completo
        globalList.sort((a, b) -> Double.compare(a.distance, b.distance));
        
        int limit = Math.min(k, globalList.size());
        double sumLabels = 0;

        for (int i = 0; i < limit; i++) {
            sumLabels += globalList.get(i).target;
        }

        return limit > 0 ? (sumLabels / limit) : 0.0;
    }
}