package com.concurrency;

import java.util.List;
import java.util.concurrent.ExecutionException;
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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1)
@State(Scope.Benchmark)
public class KnnBenchmark {

    private List<Point> train;
    private List<Point> testSet;
    private Point pontoUnico;
    private int k = 5;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        // Carrega o treino e o teste direto no Setup da simulação
        this.train = DataLoader.carregarDados("anac_train.csv", 500000); 
        this.testSet = DataLoader.carregarDados("anac_test.csv", 100);
        this.pontoUnico = testSet.get(0);
    }

    @Benchmark
    public void benchmarkPontoUnico(Blackhole bh) throws InterruptedException, ExecutionException {
        // Ajustado para receber o retorno double e sem passar executor
        double resultado = Knn.regressor(train, pontoUnico.getFeatures(), k);
        bh.consume(resultado);
    }
}