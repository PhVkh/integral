import edu.mines.jtk.util.AtomicFloat;
import java.lang.Math;
import java.util.Scanner;

public class Integral {
    static AtomicFloat answer = new AtomicFloat(); //в эту переменную будет записано значение интеграла
	
	//наша функция
	public static float func(float x) {
        return x*x;
    }
    
    //интегрируем func от start до finish, разбивая на piecies частей
    public static float integrateByPiecies(float start, float finish, int piecies) {
        float result = 0;
        float step = (finish - start) / piecies;   
        for (float i = start; i <= finish; i += step) {
            result += func(i) * step;
        }
        return result;
    }
    
    //интегрируем func от start до finish, разбивая на такое количество кусков, что accuracy больше разности integrateByPiecies для n и  n+1 кусков
    public static float integrateByAccuracy(float start, float finish, float accuracy) {
        float delta = 0;
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
        float start = in.nextFloat();
        System.out.println("to ");
        float finish = in.nextFloat();
        System.out.println("with accuracy ");
        float accuracy = in.nextFloat();
		System.out.println("number of threads ");
        int cores = in.nextInt();
		
		float size = (finish - start) / cores; //длина куска оси х, которую будет интегрировать каждый поток
		float startForThread = start;
		for (int i = 0; i < cores; ++i) {
			Thread partOfIntegral = new Thread(new shortIntegral(startForThread, startForThread + size, accuracy));
			startForThread += size;
			partOfIntegral.start();
			try {
				partOfIntegral.join();
			} catch (Exception e) {
			}
		}
        System.out.println("Result: " + answer);
    }
}

class shortIntegral implements Runnable {
    private float s;
	private float f;
	private float a;
    
    public shortIntegral(float s, float f, float a) {
        this.s = s;
        this.f = f;
		this.a = a;
	}

    public void run() {
		Integral.answer.addAndGet(Integral.integrateByAccuracy(s, f, a));
    }
}