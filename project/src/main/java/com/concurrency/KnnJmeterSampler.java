package com.concurrency;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class KnnJmeterSampler extends AbstractJavaSamplerClient{
    private static List<Point> train;
    private static List<Point> test;
    private static Point testPoint;

    // Define os parâmetros que vão aparecer na interface do JMeter
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        // O padrão será rodar o ponto único, mas você pode mudar para "lote" no JMeter
        defaultParameters.addArgument("cenario", "ponto_unico"); 
        return defaultParameters;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        // Carrega os dados apenas UMA vez para todas as threads
        synchronized (KnnJmeterSampler.class) {
                if (train == null) {
                    train = DataLoader.carregarDados("/home/kelly/prog-conc/anac_train.csv", 500000);
                    test = DataLoader.carregarDados("/home/kelly/prog-conc/anac_test.csv", 1);
                    testPoint = test.get(0);
                }    
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult result = new SampleResult();

        String cenario = context.getParameter("cenario");

        if ("lote".equalsIgnoreCase(cenario)) {
            result.setSampleLabel("k-NN Serial - Lote 100 Pontos");
        } else {
            result.setSampleLabel("k-NN Serial - Ponto Unico");
        }

        result.sampleStart(); // Inicia o cronômetro do JMeter

        try {
            if ("lote".equalsIgnoreCase(cenario)) {
                // Cenário 2: Processa o lote completo de 100 pontos (Igual ao seu JMH)
                for (Point p : test) {
                    Knn.classifier(train, p.getFeatures(), 5);
                }
                result.sampleEnd();
                result.setResponseMessage("Lote de 100 pontos classificado.");
            } else {
                // Cenário 1: Processa apenas o ponto único (Igual ao seu JMH)
                String label = Knn.classifier(train, testPoint.getFeatures(), 5);
                result.sampleEnd();
                result.setResponseMessage("Classificado como: " + label);
            }
            
            result.setSuccessful(true);
            result.setResponseCodeOK();
            
        } catch (Exception e) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseMessage("Erro: " + e.getMessage());
            
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            result.setResponseData(stringWriter.toString(), "UTF-8");
        }

        
        return result;
    }   
}
