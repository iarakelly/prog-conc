<?php


if (!extension_loaded('parallel')) {
    die("A extensão 'parallel' precisa estar instalada e ativa no seu PHP (ZTS).\n");
}

class Point {
    public array $features;
    public float $hour;

    public function __construct(array $features, float $hour){
        $this->features = $features;
        $this->hour = $hour;
    }
}

class DistanceTarget {
    public float $distance;
    public float $hour;

    function __construct(float $distance, float $hour){
        $this->distance = $distance;
        $this->hour = $hour;
    }
}

class Knn {


public static function calculate_distance(array $p1_features, array $pc_features): float {
        $sum = 0;
        $count = count($p1_features);
        for ($i = 0; $i < $count; $i++) {
            $sum += ($p1_features[$i] - $pc_features[$i]) ** 2; 
        }
        return sqrt($sum);
    }

    public function regressor(array $train, Point $pc, int $k): float {
      
    
        $flatTrain = [];
        foreach ($train as $p) {
            $flatTrain[] = [
                'features' => $p->features,
                'hour' => $p->hour
            ];
        }

        
        
        $numThreads = (int) (shell_exec('nproc') ?? 4); 
        $totalSize = count($flatTrain);
        $chunkSize = (int) ceil($totalSize / $numThreads);


        $chunks = array_chunk($flatTrain, $chunkSize);
        
        $runtimes = [];
        $futures = [];
        $pcFeatures = $pc->features;


        foreach ($chunks as $index => $chunk) {
            $runtimes[$index] = new \parallel\Runtime();
            
            $futures[$index] = $runtimes[$index]->run(function(array $subChunk, array $pcFeatures, int $k) {
                $localList = [];
                
                foreach ($subChunk as $p) {
                    $sum = 0;
                    // Agora acessamos como chave de array puro ($p['features']), sem objetos!
                    $count = count($p['features']);
                    for ($i = 0; $i < $count; $i++) {
                        $sum += ($p['features'][$i] - $pcFeatures[$i]) ** 2; 
                    }
                    $dist = sqrt($sum);
                    
                    $localList[] = ['distance' => $dist, 'hour' => $p['hour']];
                }

                usort($localList, function($a, $b) {
                    return $a['distance'] <=> $b['distance'];
                });

                return array_slice($localList, 0, $k);

            }, [$chunk, $pcFeatures, $k]);
        }


        $globalList = [];
        foreach ($futures as $future) {
            $partialResult = $future->value(); 
            foreach ($partialResult as $item) {
                $globalList[] = new DistanceTarget($item['distance'], $item['hour']);
            }
        }


        usort($globalList, function($a, $b) {
            return $a->distance <=> $b->distance;
        });

        $sumHours = 0;
        $limit = min($k, count($globalList));
        
        for ($i = 0; $i < $limit; $i++) {
            $sumHours += $globalList[$i]->hour;
        }

        return $limit > 0 ? ($sumHours / $limit) : 0.0;
    }
}