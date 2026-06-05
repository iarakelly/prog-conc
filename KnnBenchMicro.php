<?php

require_once 'knn.php';

use PhpBench\Attributes as Bench;

class KnnBenchMicro {
    private Point $p1;
    private Point $p2;
    private Knn $knn;

    public function setUp(): void {


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

        if (!empty($this->dadosLoteTeste) && !empty($this->dadosTreino)) {
            $this->p1 = $this->dadosTreino[0];
            $this->p2 = $this->dadosLoteTeste[0];
        }

        $this->knn = new Knn();
    }

    #[Bench\BeforeMethods('setUp')]
    #[Bench\Revs(1000)]       // Roda o conteúdo 1000 vezes por iteração
    #[Bench\Iterations(5)]    // Faz 5 baterias de testes isoladas
    #[Bench\Warmup(2)]        // Roda 2 vezes antes de ligar o cronômetro
    public function benchCalculateDistance(): void
    {
        $this->knn->calculate_distance($this->p1, $this->p2);
    }
}

