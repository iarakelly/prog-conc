<?php
// KnnBench.php

require_once 'knn.php';

/**
 * @BeforeClassMethods({"carregarDados"})
 */
class KnnBench {
    private static $trainFile = "/home/lucyedja/Downloads/prog-conc/anac_train.csv";
    private static $testFile = "/home/lucyedja/Downloads/prog-conc/anac_test.csv";
    private static $knn;
    
    // Arrays que ficarão estáticos na memória para os testes usarem diretamente
    private static $trainData = [];
    private static $testLote = [];
    private static $testPontoUnico;

    // Gerador que lê o arquivo sob demanda linha por linha
    private static function getStreamData(string $filePath, ?int $limit = null): generator {
        if (($handle = fopen($filePath, 'r')) !== false) {
            fgetcsv($handle, 0, ','); // Pula cabeçalho
            $count = 0;
            while (($row = fgetcsv($handle, 0, ',')) !== false) {
                if (isset($row[8])) { 
                    $features = [
                        (float)$row[0], (float)$row[1], (float)$row[2],
                        (float)$row[3], (float)$row[4], (float)$row[5],
                        (float)$row[6], (float)$row[7], (float)$row[8]
                    ];
                    yield new Point($features, (float)$row[9]);
                    
                    $count++;
                    if ($limit !== null && $count >= $limit) {
                        break;
                    }
                }
            }
            fclose($handle);
        }
    }

    // Executa apenas UMA vez antes de todos os testes começarem
    public static function carregarDados(): void {
        // Desativa o limite de memória para este processo do PHP CLI
        ini_set('memory_limit', '-1');
        
        self::$knn = new Knn();

        // Carrega o treino completo para a memória de forma isolada (FORA DO CRONÔMETRO)
        self::$trainData = iterator_to_array(self::getStreamData(self::$trainFile));

        // Carrega o lote de 100 pontos de teste para a memória
        self::$testLote = iterator_to_array(self::getStreamData(self::$testFile, 100));

        // Define o ponto único baseado no primeiro elemento do lote
        if (!empty(self::$testLote)) {
            self::$testPontoUnico = self::$testLote[0];
        }
    }

    /**
     * TESTE 1: Regressão de um ponto único (Contra o treino COMPLETO)
     * @Iterations(3)
     * @Revs(1)
     */
    public function benchRegressorPontoUnico(): void {
        self::$knn->regressor(self::$trainData, self::$testPontoUnico, 3);
    }

    /**
     * TESTE 2: Regressão em lote com 100 pontos (Contra o treino COMPLETO)
     * @Iterations(3)
     * @Revs(1)
     */
    public function benchRegressorLote100(): void {
        foreach (self::$testLote as $testPoint) {
            self::$knn->regressor(self::$trainData, $testPoint, 3);
        }
    }
}