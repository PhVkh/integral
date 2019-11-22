import java.lang.Math;
import java.util.Scanner;

public class Integral {
	public static double answer;
	static int cores; //количество ядер
	
	//наша функция
	public static double func(double x) {
        return Math.pow(x, 2);
    }
    
    //интегрируем func от start до finish, разбивая на piecies частей
    public static double integrateByPiecies(double start, double finish, int piecies) {
        double result = 0;
        double step = (finish - start) / piecies;   
        for (double i = start; i <= finish; i += step) {
            result += func(i) * step;
        }
        return result;
    }
    
    //интегрируем func от start до finish, разбивая на такое количество кусков, что accuracy больше разности integrateByPiecies для n и  n+1 кусков
    public static void integrateByAccuracy(double start, double finish, double accuracy, int cores) {
        if (cores == 1) {
			double delta = 0;
            int piecies = 1;
            do {
                delta = Math.abs(integrateByPiecies(start, finish, piecies) - integrateByPiecies(start, finish, piecies + 1));
                ++piecies;
            } while (delta > accuracy);
			answer += integrateByPiecies(start, finish, piecies);
		} else { //если ядер несколько - разделим работу
            double f = start + (finish - start) / cores;
			Thread rightPart = new Thread(new shortIntegral(f, finish, accuracy, cores));
			rightPart.start();
	    	double delta = 0;
            int piecies = 1;
            do {
                delta = Math.abs(integrateByPiecies(start, finish, piecies) - integrateByPiecies(start, finish, piecies + 1));
                ++piecies;
            } while (delta > accuracy);
    		double leftPart = integrateByPiecies(start, f, piecies);
			try {
				rightPart.join();
			} catch (Exception e) {
				
			}
			answer += leftPart;			
		}
    }
	
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        System.out.println("Integrate from ");
        double start = in.nextDouble();
        System.out.println("to ");
        double finish = in.nextDouble();
        System.out.println("with accuracy ");
        double accuracy = in.nextDouble();
		System.out.println("number of threads ");
        cores = in.nextInt();
		
		double size = (finish - start) / cores; //длина куска оси х, которую будет интегрировать каждый поток
		double startForThread = start;
		
		integrateByAccuracy(start, finish, accuracy, cores);
        System.out.println("Result: " + answer);
    }
}

class shortIntegral implements Runnable {
    private double start;
	private double finish;
	private double accuracy;
	private int cores;
	
    public shortIntegral(double s, double f, double a, int c) {
        this.start = s;
        this.finish = f;
		this.accuracy = a;
		this.cores = c;
	}

    public void run() {
		Integral.integrateByAccuracy(start, finish, accuracy, cores - 1);
    }
}