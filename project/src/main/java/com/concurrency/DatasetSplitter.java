package com.concurrency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class DatasetSplitter {

    public static void splitDataset(String inputPath, String trainPath, String testPath, double trainRatio) {
        Random random = new Random();
        int countTrain = 0;
        int countTest = 0;

        InputStream input = DatasetSplitter.class
        .getClassLoader()
        .getResourceAsStream(inputPath);

        if (input == null) {
        throw new RuntimeException("Arquivo não encontrado: " + inputPath);
        }

        try (BufferedReader br = new BufferedReader(
                new java.io.InputStreamReader(input));
        
            BufferedWriter trainWriter = new BufferedWriter(new FileWriter(trainPath));
            BufferedWriter testWriter = new BufferedWriter(new FileWriter(testPath))) {

            String line;
            String header = br.readLine();
            if (header != null) {
                trainWriter.write(header);
                trainWriter.newLine();
                testWriter.write(header);
                testWriter.newLine();
            }

            while ((line = br.readLine()) != null) {
                // Pular linhas vazias se houver
                if (line.trim().isEmpty()) continue;

                if (random.nextDouble() < trainRatio) {
                    trainWriter.write(line);
                    trainWriter.newLine();
                    countTrain++;
                } else {
                    testWriter.write(line);
                    testWriter.newLine();
                    countTest++;
                }
                
                // Feedback visual a cada 100k linhas para você não achar que travou
                if ((countTrain + countTest) % 100000 == 0) {
                    System.out.println("Processadas " + (countTrain + countTest) + " linhas...");
                }
            }
            
            // Força a escrita do que sobrou no buffer
            trainWriter.flush();
            testWriter.flush();

            System.out.println("=== Sucesso! ===");
            System.out.println("Treino: " + countTrain + " linhas em " + trainPath);
            System.out.println("Teste: " + countTest + " linhas em " + testPath);

        } catch (IOException e) {
            System.err.println("Erro crítico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        splitDataset("anac_reduzido.csv", "anac_train.csv", "anac_test.csv", 0.8);
    }
}