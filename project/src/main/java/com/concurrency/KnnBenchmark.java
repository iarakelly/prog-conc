package com.concurrency;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1) // Diminuí para 1 fork para ser mais rápido agora
@State(Scope.Benchmark)



public class KnnBenchmark {

    private List<Point> train;
    private List<Point> testSet;
    private Point pontoUnico;
    private int k = 5;
    private ExecutorService executor;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        // Carrega o treino e o teste direto aqui no Setup
        this.train = DataLoader.carregarDados("anac_train.csv", 500000); 
        this.testSet = DataLoader.carregarDados("anac_test.csv", 100);
        this.pontoUnico = testSet.get(0);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        this.executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors(),
        r -> {
            Thread t = new Thread(r);
            t.setDaemon(true); 
            return t;
        }
        );
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (executor != null) {
        executor.shutdownNow(); // Interrompe tudo imediatamente
        }
    }

    @Benchmark
    public void benchmarkPontoUnico(Blackhole bh) throws InterruptedException, ExecutionException {
        String resultado = Knn.classifier(train, pontoUnico.getFeatures(), k, executor);
        bh.consume(resultado);
    }

    @Benchmark
    public void benchmarkKnnLoteCompleto(Blackhole bh) throws InterruptedException, ExecutionException {
        for (Point p : testSet) {
            // Chama o seu método original do classificador
            String resultado = Knn.classifier(train, p.getFeatures(), k, executor);
            bh.consume(resultado);
        }
    }

    
}