package com.concurrency;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String caminhoArquivo = "../anac_reduzido.csv"; // Certifique-se que o arquivo está na raiz do projeto
        List<Point> train = new ArrayList<>();
        
        // 1. Buffer de Leitura para o CSV
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            
            // Pular o cabeçalho se o seu CSV tiver um
            String cabecalho = br.readLine(); 
            
            while ((linha = br.readLine()) != null) {
                // Supondo que o CSV seja separado por vírgula ou ponto-e-vírgula
                String[] colunas = linha.split(","); 
                
                // Exemplo: Supondo que as 2 primeiras colunas são números (features)
                // e a última coluna é a classe (label)
                double[] feat = new double[2];
                feat[0] = Double.parseDouble(colunas[0]);
                feat[1] = Double.parseDouble(colunas[1]);
                
                String label = colunas[colunas.length - 1];
                
                train.add(new Point(feat, label));
            }
            
            System.out.println("Leitura concluída! Total de pontos carregados: " + train.size());

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.err.println("Erro na conversão de números: " + e.getMessage());
            return;
        }

        // 2. Definindo o ponto de teste e o K
        // (Ajuste esses valores de acordo com a escala do seu dataset da ANAC)
        double[] teste = {20.5, 15.2}; 
        int k = 5;

        // 3. Executando o seu Classificador
        if (!train.isEmpty()) {
            System.out.println("Calculando KNN...");
            long inicio = System.currentTimeMillis();
            
            String resultado = Knn.classifier(train, teste, k);
            
            long fim = System.currentTimeMillis();
            
            System.out.println("------------------------------------");
            System.out.println("Classe prevista: " + resultado);
            System.out.println("Tempo de execução: " + (fim - inicio) + "ms");
            System.out.println("------------------------------------");
        }
    }
}