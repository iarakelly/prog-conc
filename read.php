<?php

include 'knn.php';

$file_path = "anac_reduzido.csv";

function getAnacData($file_path) {
    $handle = fopen($file_path, 'r');
    $header = fgetcsv($handle, 0, ','); // Pula o cabeçalho
    
    while (($line = fgetcsv($handle, 0, ',')) !== false) {
        // Retornamos a linha bruta para o teste de parsing
        yield $line; 
    }
    fclose($handle);
}

function runSequential($file_path, $test_point, $k) {
    $knn = new Knn();
    $train = [];
    $i = 0;
    
    $start = microtime(true);
    foreach (getAnacData($file_path) as $row) {
        // Mapeamos as features conforme sua necessidade de análise
        if (isset($row[84], $row[85], $row[4])) {
            $features = [(float)$row[84], (float)$row[85]]; 
            $train[] = new Point($features, $row[4]);
        }
        $i++;
        if ($i % 50000 === 0) {
            echo "Linhas carregadas: $i | Memória atual: " . round(memory_get_usage() / 1024 / 1024) . " MB\n";
        }
    }
    
    $result = $knn->classifier($train, $test_point, $k);
    $end = microtime(true);
    
    return $end - $start;
}

$test_point = new Point([1000.0, 500.0], ""); 
$time = runSequential($file_path, $test_point, 3);
echo "Tempo de execução: " . $time . " segundos" . PHP_EOL;