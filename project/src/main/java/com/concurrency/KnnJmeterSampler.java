package com.concurrency;

import java.util.List;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class KnnJmeterSampler extends AbstractJavaSamplerClient{
    private static List<Point> train;
    private static Point testPoint;

    @Override
    public void setupTest(JavaSamplerContext context) {
        // Carrega os dados apenas UMA vez para todas as threads
        if (train == null) {
            train = DataLoader.carregarDados("/home/prog-conc/project/anac_train.csv", 500000);
            List<Point> testSet = DataLoader.carregarDados("/home/prog-conc/project/anac_test.csv", 1);
            testPoint = testSet.get(0);
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        result.sampleStart(); // Inicia o cronômetro do JMeter

        try {
            String label = Knn.classifier(train, testPoint.getFeatures(), 5);
            result.sampleEnd(); // Para o cronômetro
            result.setSuccessful(true);
            result.setResponseMessage("Classificado como: " + label);
            result.setResponseCodeOK();
        } catch (Exception e) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseMessage("Erro: " + e.getMessage());
        }
        return result;
    }   
}
