public class App {
    static int count = 0;

    public static void main(String[] args) throws Exception { 
        Runnable compraIngresso = new Runnable()
        {
            public void run(){
                if (count < 30){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    count++;
                    System.out.println(Thread.currentThread().getName() + " Comprou Ingresso" + count);
                }
            } 
        };

        for(int i =1; i<20 ; i++){
            Thread pessoa = new Thread(compraIngresso);
            pessoa.start();

        }

    }

}
