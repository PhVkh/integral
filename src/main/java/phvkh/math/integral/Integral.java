import java.lang.Math;
import java.util.Scanner;

public class Integral {
    static double answer = 0; //в эту переменную будет записано значение интеграла
	
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
    public static double integrateByAccuracy(double start, double finish, double accuracy) {
        double delta = 0;
        int piecies = 1;
        do {
            delta = Math.abs(integrateByPiecies(start, finish, piecies) - integrateByPiecies(start, finish, piecies + 1));
            ++piecies;
        } while (delta > accuracy);
        return integrateByPiecies(start, finish, piecies);
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
        int cores = in.nextInt();
		
		double size = (finish - start) / cores; //длина куска оси х, которую будет интегрировать каждый поток
		double startForThread = start;
		shortIntegral[] parts = new shortIntegral[cores];
		for (int i = 0; i < cores; ++i) {
			parts[i] = new shortIntegral(startForThread, startForThread + size, accuracy);
			Thread partOfIntegral = new Thread(parts[i], "i.toString()");
			startForThread += size;
			partOfIntegral.start();
		}
		//усыпим наш главный поток, пока остальные работают
		try {
	    	while (Thread.activeCount() > 1) {
		    	Thread.sleep(250);
		    }
		} catch (InterruptedException e) {}
		for (int i = 0; i < cores; ++i) {
			answer += parts[i].getResult();
		} 
        System.out.println("Result: " + answer);
    }
}

class shortIntegral implements Runnable {
    private double s;
	private double f;
	private double a;
	private double result;
    
    public shortIntegral(double s, double f, double a) {
        this.s = s;
        this.f = f;
		this.a = a;
	}

    public void run() {
		result = Integral.integrateByAccuracy(s, f, a);
    }
	
	public double getResult() {
		return result;
	}
}