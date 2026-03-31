import java.util.ArrayList;
import java.util.List;

public class main {

    public static void main (String[] args) {

        List<Point> train = new ArrayList<>();

        train.add(new Point(new double[]{1.0, 2.0}, "A"));
        train.add(new Point(new double[]{1.5, 1.8}, "A"));
        train.add(new Point(new double[]{5.0, 8.0}, "B"));
        train.add(new Point(new double[]{6.0, 9.0}, "B"));

        double[] teste = {2.0, 2.0};

        String resultado = Knn.classifier(train, teste, 3);

        System.out.println("Classe prevista: " + resultado);
    }
    
}