<?php
// KnnBench.php

require_once 'knn.php';

use PhpBench\Attributes\BeforeMethods;
use PhpBench\Attributes\Iterations;
use PhpBench\Attributes\Revolutions;

class KnnBench {
    // Variáveis normais de objeto, garantindo que cada processo tenha sua própria cópia limpa na memória
    private array $dadosTreino = [];
    private array $dadosLoteTeste = [];
    private ?Point $pontoUnicoTeste = null;
    private ?Knn $instanciaKnn = null;

    // Essa função vai rodar ANTES de cada teste, garantindo o isolamento total dos processos
    public function inicializarAmbiente(): void {
        ini_set('memory_limit', '-1');
        
        $this->instanciaKnn = new Knn();
        
        $train_file = "/home/kelly/prog-conc/anac_train.csv";
        $test_file = "/home/kelly/prog-conc/anac_test.csv";

        // 1. Carrega o treino para este processo atual
        if (($handle = fopen($train_file, 'r')) !== false) {
            fgetcsv($handle, 0, ','); // Pula cabeçalho
            while (($row = fgetcsv($handle, 0, ',')) !== false) {
                $features = [
                    (float)$row[0], (float)$row[1], (float)$row[2],
                    (float)$row[3], (float)$row[4], (float)$row[5],
                    (float)$row[6], (float)$row[7], (float)$row[8]
                ];
                $this->dadosTreino[] = new Point($features, (float)$row[9]);
            }
            fclose($handle);
        }

        // 2. Carrega o lote de teste para este processo atual
        if (($handle = fopen($test_file, 'r')) !== false) {
            fgetcsv($handle, 0, ','); // Pula cabeçalho
            $count = 0;
            while (($row = fgetcsv($handle, 0, ',')) !== false && $count < 100) {
                $features = [
                    (float)$row[0], (float)$row[1], (float)$row[2],
                    (float)$row[3], (float)$row[4], (float)$row[5],
                    (float)$row[6], (float)$row[7], (float)$row[8]
                ];
                $this->dadosLoteTeste[] = new Point($features, (float)$row[9]);
                $count++;
            }
            fclose($handle);
        }

        if (!empty($this->dadosLoteTeste)) {
            $this->pontoUnicoTeste = $this->dadosLoteTeste[0];
        }
    }

    // TESTE 1: Regressão de um ponto único
    #[BeforeMethods('inicializarAmbiente')]
    #[Iterations(5)]
    #[Revolutions(1)]
    public function benchRegressorPontoUnico(): void {
        $this->instanciaKnn->regressor($this->dadosTreino, $this->pontoUnicoTeste, 3);
    }

    // TESTE 2: Regressão em lote com 100 pontos
    #[BeforeMethods('inicializarAmbiente')]
    #[Iterations(5)]
    #[Revolutions(1)]
    public function benchRegressorLote100(): void {
        foreach ($this->dadosLoteTeste as $testPoint) {
            $this->instanciaKnn->regressor($this->dadosTreino, $testPoint, 3);
        }
    }
}