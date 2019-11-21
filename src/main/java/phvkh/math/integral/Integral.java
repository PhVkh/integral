import java.lang.Math;
import java.util.Scanner;

public class Integral {
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
		System.out.println("I do maths! " + integrateByAccuracy(start, finish, accuracy));
	}
}