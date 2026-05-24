<?php

include 'knn.php';

$train_file = "/home/lucyedja/Downloads/prog-conc/anac_train.csv";
$test_file = "/home/lucyedja/Downloads/prog-conc/anac_test.csv";

function getAnacData($file_path) {
    if (!file_exists($file_path)){
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
    
    // 1. CARREGAMENTO (Fora do cronômetro)
    // CORREÇÃO 1: Mudado de $file_path para $train_file (variável correta)
    foreach (getAnacData($train_file) as $row) {
        $features = [
            (float)$row[0], (float)$row[1], (float)$row[2],
            (float)$row[3], (float)$row[4], (float)$row[5],
            (float)$row[6], (float)$row[7], (float)$row[8]
        ]; 
        // CORREÇÃO 2: Cast para (float) no target do regressor
        $train[] = new Point($features, (float)$row[9]);
        
        $i++;
        if ($i % 50000 === 0) {
            echo "Linhas carregadas: $i | Memória atual: " . round(memory_get_usage() / 1024 / 1024) . " MB\n";
        }
    }
    
    // Coleta apenas 1 ponto do arquivo de teste para o exemplo de ponto único
    $test_point = null;
    foreach (getAnacData($test_file) as $row) {
        $features = [(float)$row[0], (float)$row[1], (float)$row[2], (float)$row[3], (float)$row[4], (float)$row[5], (float)$row[6], (float)$row[7], (float)$row[8]]; 
        $test_point = new Point($features, (float)$row[9]);
        break; // Pega só o primeiro
    }

    // 2. MEDIÇÃO EXCLUSIVA DO KNN
    $start = microtime(true);
    
    // CORREÇÃO 3: $test_point agora existe dentro do escopo da função
    $result = $knn->regressor($train, $test_point, $k);
    
    $end = microtime(true);

    echo "Resultado da predição: " . $result . PHP_EOL;
    return $end - $start; // Retorna apenas o tempo de cálculo matemático
}

// Execução do teste manual (Macro-bench simples)
$time = runSequential($train_file, $test_file, 3);
echo "Tempo estrito do KNN: " . round($time, 4) . " segundos" . PHP_EOL;