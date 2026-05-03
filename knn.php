<?php 

class Point {
    public array $features;
    public string $label;

    public function __construct(array $features, string $label){
        $this->features = $features;
        $this->label = $label;

    }
}

class DistanceLabel {
    public float $distance;
    public string $label;

    function __construct( float $distance, string $label){
        $this->distance = $distance;
        $this->label = $label;
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

    function classifier(array $train, Point $pc, $k){
        $neighboors = array();

        foreach($train as $p){
            $dist = $this->calculate_distance($p, $teste);
            $neighboors[] = new DistanceLabel($dist, $p->label);
        }

        usort($neigbhoors, function( $a, $b){
            // O operador <=> retorna -1 se $a < $b, 0 se igual, e 1 se $a > $b
            return $a->distance <=> $b->distance;
        });

        $votes = array();

        for($i = 0; $i < k; $i++){
            $s = $neigboors[$i]->label;

            if(isset($votes[$label])){
                $votes[$label]++;    
            }
            else{
                $votes[$label] =1; 
            }
        }
        return array_key_first($votes);
    }    
}