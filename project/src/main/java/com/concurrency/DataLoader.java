package com.concurrency;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    public static List<Point> carregarDados(String caminho, int limite) {
        List<Point> pontos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            int cont = 0;
            br.readLine(); // Pula o cabeçalho

            while ((linha = br.readLine()) != null && cont < limite) {
                String[] partes = linha.split(",");
                if (partes.length < 2) continue;

                try {
                    // Extrai as 9 primeiras colunas como features
                    double[] features = new double[partes.length - 1];
                    for (int i = 0; i < partes.length - 1; i++) {
                        try {
                            features[i] = Double.parseDouble(partes[i]);
                        } catch (NumberFormatException e) {
                            features[i] = 0.0; 
                        }
                    }
                    
                    // CORREÇÃO: Faz o parse da última coluna (índice 9) para double (Target)
                    double target;
                    try {
                        target = Double.parseDouble(partes[partes.length - 1]);
                    } catch (NumberFormatException e) {
                        target = 0.0;
                    }
                    
                    // Adiciona o ponto passando o target numérico corrigido
                    pontos.add(new Point(features, target));
                    cont++;
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
        }
        return pontos;
    } 
}