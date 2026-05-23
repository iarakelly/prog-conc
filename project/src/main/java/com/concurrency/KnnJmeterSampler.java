package com.concurrency;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class KnnJmeterSampler extends AbstractJavaSamplerClient {
    private static List<Point> train;
    private static List<Point> test;
    private static Point testPoint;
    private static String erroSetup = null; // Guarda a falha real do carregamento

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("cenario", "ponto_unico"); 
        return defaultParameters;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        synchronized (KnnJmeterSampler.class) {
            if (train == null) {
                try {
                    train = loadData.load("/home/kelly/prog-conc/project/anac_train.csv", 500000);
                    test = loadData.load("/home/kelly/prog-conc/project/anac_test.csv", 100);
                    
                    if (train == null || train.isEmpty()) {
                        erroSetup = "O arquivo anac_train.csv retornou 0 linhas. Verifique se o caminho /home/kelly/prog-conc/ existe e se o Splitter rodou com sucesso.";
                    } else if (test == null || test.isEmpty()) {
                        erroSetup = "O arquivo anac_test.csv retornou 0 linhas. Verifique se o caminho /home/kelly/prog-conc/ existe e se o Splitter rodou com sucesso.";
                    } else {
                        testPoint = test.get(0);
                    }
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    erroSetup = "Exceção disparada no loadData: " + e.getMessage() + "\nStack: " + sw.toString();
                }
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

        result.sampleStart(); 

        // CRUCIAL: Se o setup falhou antes, expõe o erro real na árvore de resultados do JMeter
        if (erroSetup != null) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseCode("500");
            result.setResponseMessage(erroSetup);
            return result;
        }

        // Segunda barreira física de proteção antes de ler .features
        if (testPoint == null || train == null) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseCode("500");
            result.setResponseMessage("Erro Crítico: Os dados não foram carregados na memória (referência nula).");
            return result;
        }

        try {
            if ("lote".equalsIgnoreCase(cenario)) {
                for (Point p : test) {
                    Knn.regressor(train, p.features, 5); 
                }
                result.sampleEnd();
                result.setResponseMessage("Lote de 100 pontos processado com sucesso.");
            } else {
                double hour = Knn.regressor(train, testPoint.features, 5); 
                result.sampleEnd();
                
                int horas = (int) hour / 60;
                int minutos = (int) hour % 60;
                result.setResponseMessage(String.format("Predição do horário de partida: %02d:%02d", horas, minutos));
            }
            
            result.setSuccessful(true);
            result.setResponseCodeOK();
            
        } catch (Exception e) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseMessage("Erro durante o processamento do k-NN: " + e.getMessage());
            
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            result.setResponseData(stringWriter.toString(), "UTF-8");
        }

        return result;
    }   
}