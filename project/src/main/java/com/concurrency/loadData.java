package com.concurrency;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class loadData {

    public static List<Point> load(String path, int limite) {
        List<Point> pontos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linha;
            int count = 0;
            br.readLine(); // Pula o cabeçalho

            while ((linha = br.readLine()) != null && count < limite) {
                String[] partes = linha.split(",");
                if (partes.length < 2) continue;

                try {
                    double[] features = new double[partes.length - 1];
                    for (int i = 0; i < partes.length - 1; i++) {
                    features[i] = Double.parseDouble(partes[i]);
                    }
                    double hour = Double.parseDouble(partes[partes.length - 1]);
                    pontos.add(new Point(features, hour));
                    count++;
                } catch (Exception e) {
                    continue; // Pula a linha se houver erro de estrutura
                }
            }
        } 
        
        catch (Exception e) {
        System.err.println("Erro ao carregar dados: " + e.getMessage());
        }
        
    return pontos;
    } 
}
