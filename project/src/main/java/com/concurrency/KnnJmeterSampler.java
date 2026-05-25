package com.concurrency;

import java.util.List;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class KnnJmeterSampler extends AbstractJavaSamplerClient {
    private static List<Point> train;
    private static Point testPoint;
    
    // Objeto de trava para sincronismo entre as threads do JMeter
    private static final Object lock = new Object();

    @Override
    public void setupTest(JavaSamplerContext context) {
        // Bloqueia o acesso concorrente durante a leitura dos arquivos CSV
        synchronized (lock) {
            if (train == null) {
                train = DataLoader.carregarDados("/home/prog-conc/project/anac_train.csv", 500000);
                List<Point> testSet = DataLoader.carregarDados("/home/prog-conc/project/anac_test.csv", 1);
                
                if (testSet != null && !testSet.isEmpty()) {
                    testPoint = testSet.get(0);
                }
            }
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();
        
        // Proteção extra contra falha de leitura ou caminhos incorretos do arquivo
        if (train == null || testPoint == null) {
            result.setSuccessful(false);
            result.setResponseMessage("Erro Crítico: Os dados não foram carregados no setupTest.");
            result.setResponseCode("500");
            return result;
        }

        result.sampleStart(); // Inicia o cronômetro do JMeter

        try {
            // Chamada corrigida consumindo sua implementação de threads manuais (sem executor)
            double hour = Knn.regressor(train, testPoint.getFeatures(), 5);
            
            result.sampleEnd(); // Para o cronômetro
            result.setSuccessful(true);
            result.setResponseMessage("Classificado como: " + hour);
            result.setResponseCodeOK();
        } catch (Exception e) {
            result.sampleEnd();
            result.setSuccessful(false);
            // Captura o nome da exceção real para facilitar o seu diagnóstico se estourar threads do SO
            result.setResponseMessage("Erro: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            result.setResponseCode("500");
        }
        return result;
    }   
}