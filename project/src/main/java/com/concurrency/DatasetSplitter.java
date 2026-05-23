package com.concurrency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DatasetSplitter {

    public static void splitDataset(String inputPath, String trainPath, String testPath, double trainRatio) {
        Random random = new Random();
        int countTrain = 0;
        int countTest = 0;

       try (BufferedReader br = new BufferedReader(new FileReader(inputPath));
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
                
                if ((countTrain + countTest) % 100000 == 0) {
                    System.out.println("Processadas " + (countTrain + countTest) + " linhas...");
                }
            }
            
            trainWriter.flush();
            testWriter.flush();

            System.out.println("=== SPLIT CONCLUÍDO COM SUCESSO ===");
            System.out.println("Treino: " + countTrain + " linhas gravadas.");
            System.out.println("Teste: " + countTest + " linhas gravadas.");

        } catch (IOException e) {
            System.err.println("Erro crítico ao processar arquivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        splitDataset("/home/kelly/prog-conc/dataset_knn_concorrente_1GB.csv", 
        "/home/kelly/prog-conc/project/anac_train.csv",
        "/home/kelly/prog-conc/project/anac_test.csv",
        0.8);
    }
}