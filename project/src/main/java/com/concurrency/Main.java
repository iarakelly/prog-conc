package com.concurrency;

public class Main {

    public static void main(String[] args) {

        new java.io.File("output").mkdirs();

        String arquivoOriginal = "anac_reduzido.csv";
        String arquivoTreino = "anac_train.csv";
        String arquivoTeste = "anac_test.csv";

        DatasetSplitter.splitDataset(
            arquivoOriginal, 
            arquivoTreino, 
            arquivoTeste, 
            0.8
        );
    }
}