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
        //File original = new File(arquivoOriginal);
       // if (!original.exists()) {
        //    System.err.println("ERRO: O arquivo " + arquivoOriginal + " não foi encontrado na raiz do projeto!");
        //    System.err.println("Certifique-se de que ele está na pasta: " + System.getProperty("user.dir"));
        //    return;
       // }

        // 3. Só divide o dataset se os arquivos de treino/teste ainda não existirem
      //  File fTrain = new File(arquivoTreino);
        //File fTest = new File(arquivoTeste);

       // if (!fTrain.exists() || !fTest.exists()) {
        //    System.out.println("Arquivos de treino/teste não encontrados. Dividindo agora...");
        //    DatasetSplitter.splitDataset(arquivoOriginal, arquivoTreino, arquivoTeste, 0.8);
        //    System.out.println("Divisão concluída!");
       // } else {
       //     System.out.println("Arquivos de treino e teste já estão prontos.");
       // }
    }
}