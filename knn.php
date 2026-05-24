<?php 

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

    function __construct( float $distance, float $hour){
        $this->distance = $distance;
        $this->hour = $hour;
    }
}

class Knn{

    function calculate_distance(Point $p1, Point $p2): float{
    $sum = 0;

        for($i = 0; $i < count($p1->features); $i++){
            $sum += ($p1->features[$i] - $p2->features[$i])**2; 
        }
    return $sum**(1/2);
    }

    function regressor(array $train, Point $pc, $k){
        $neighboors = array();

        foreach($train as $p){
            $dist = $this->calculate_distance($p, $pc);
            $neighboors[] = new DistanceTarget($dist, $p->hour);
        }

        usort($neighboors, function( $a, $b){
            // O operador <=> retorna -1 se $a < $b, 0 se igual, e 1 se $a > $b
            return $a->distance <=> $b->distance;
        });

        $sumDistances = 0;

        for ($i = 0; $i < $k; $i++){
            $sumDistances += $neighboors[$i]->hour;
        }
        return $sumDistances/$k;
    }    
}