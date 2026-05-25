<?php
// read.php

include 'knn.php';

$train_file = "/home/kelly/prog-conc/anac_train.csv";
$test_file = "/home/kelly/prog-conc/anac_test.csv";

function getAnacData($file_path) {
    if (!file_exists($file_path)) {
        die("Erro: {$file_path} nao encontrado" . PHP_EOL);
    }

    $handle = fopen($file_path, 'r');
    fgetcsv($handle, 0, ','); // Pula o cabeçalho
    
    while (($line = fgetcsv($handle, 0, ',')) !== false) {
        yield $line; 
    }
    fclose($handle);
}

function runSequential($train_file, $test_file, $k) {
    $knn = new Knn();
    $train = [];
    $i = 0;
    
    echo "--- Carregando dados de treino ---" . PHP_EOL;
    foreach (getAnacData($train_file) as $row) {
        // Mapeia as 9 features do seu dataset da ANAC e a hora alvo (índice 9)
        $features = [
            (float)$row[0], (float)$row[1], (float)$row[2],
            (float)$row[3], (float)$row[4], (float)$row[5],
            (float)$row[6], (float)$row[7], (float)$row[8]
        ]; 
        $train[] = new Point($features, (float)$row[9]);
        
        $i++;
        if ($i % 100000 === 0) {
            echo "Registros no buffer: $i | RAM: " . round(memory_get_usage() / 1024 / 1024) . " MB\n";
        }
    }
        
    // Coleta apenas 1 ponto do arquivo de teste para o cálculo de ponto único
    $test_point = null;
    foreach (getAnacData($test_file) as $row) {
        $features = [
            (float)$row[0], (float)$row[1], (float)$row[2],
            (float)$row[3], (float)$row[4], (float)$row[5],
            (float)$row[6], (float)$row[7], (float)$row[8]
        ]; 
        $test_point = new Point($features, (float)$row[9]);
        break; // Interrompe o laço para pegar estritamente o primeiro ponto
    }

    echo "--- Executando o KNN (Ponto Único) ---" . PHP_EOL;
    
    // Cronometra exclusivamente a execução matemática do KNN
    $start = microtime(true);
    $result = $knn->regressor($train, $test_point, $k);
    $end = microtime(true);

    echo "Resultado da predição: " . $result . PHP_EOL;
    
    return $end - $start; // Retorna o tempo decorrido do cálculo
}

// Executa o script passando os caminhos corretos
$time = runSequential($train_file, $test_file, 3);
echo "Tempo de execução: " . round($time, 4) . " segundos" . PHP_EOL;