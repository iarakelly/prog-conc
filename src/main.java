import java.util.*;

public class Point{
    double[] features;
    String label;

    public Point(double[] features, String label){
        this.features = features;
        this.label = label;
    }

}

public class knn {

    public void main CalcularDistancia(Point p1, Point p2){
        double sum = 0.0;
        for (i=0; i<p1.leangth; i++){
            sum += Math.pow(p1.features[i]-p2.features[i], 2);

            return Math.sqrt(sum);
        }
    }
}

public class main {
    
}
