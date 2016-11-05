/**
 * 2016 2016年4月27日 下午9:08:42
 * 瑞东
 * FeatureExtraction
 * TODO
 */
package Discretization;

import java.util.Arrays;
import org.omg.CORBA.SystemException;

/**
 * @author 瑞东
 *         2016 2016年4月27日 下午9:08:42
 *         瑞东
 *         FeatureExtraction
 *         TODO Discrete the time series as the up down level peak and trough symbol.
 *         advice the windowsize as 3
 */
public class FeatureExtraction {

	int i = 0, k = 0, j = 0, N;
	private double Threshold;
	int newInstancesLength;
	final double originalInstancesLength;
	final int columnsNumber;

	double[][] Instances;
	double[][] newNumericInstances = new double[25][10000];
	char[][] normalizedInstances = new char[25][10000];
	final static char[] Alphabet = { 'p', 't', 'u', 'd', 'l' }; // peak trough up down level

	/**
	 * @param Instances
	 * @param length
	 * @param columnsNumber
	 * @param N
	 * @param Threshold
	 */
	public FeatureExtraction(double[][] Instances, double length, int columnsNumber, int N, double Threshold) {
		this.Instances = Instances;
		this.columnsNumber = columnsNumber;// ！！！！！difference
		originalInstancesLength = length;// ！！！！！difference
		this.N = N;
		this.Threshold = Threshold;

		getFeature();
		// printNewNumericInstances();
		// printNormalizedInstances();

	}

	/**
	 * 2016年4月27日 下午12:50:46 瑞东
	 * getControlPoint3
	 * 
	 * @param Instances
	 * @param length
	 * @param columnsNumber
	 * @param N=3
	 * @return double[][]
	 *         TODO advice the windowsize is 3
	 *         The idea of this method is when a point is bigger or smaller than his neighbors. it is a curve
	 */
	public void getFeature() {

		int t = 0;
		double growthRate = 0;
		Threshold /= 100;
		if (columnsNumber <= 15) {
			BinaryDiscretization binaryDiscretization = new BinaryDiscretization(Instances, originalInstancesLength, N);
			normalizedInstances = binaryDiscretization.getBinaryNormalizedInstances();
			t = 2;
		}

		else {
			BinaryDiscretization binaryDiscretization = new BinaryDiscretization(Instances, originalInstancesLength, N);
			normalizedInstances = binaryDiscretization.getBinaryNormalizedInstances();
			t = 3;
			// t = 2;
		}

		for (k = t; k < columnsNumber; k++) {
			// for (k = 0; k < columnsNumber; k++) {
			t = 1;
			for (i = N + 1; i <= originalInstancesLength - N - 1; i++) {

				growthRate = 0;
				newNumericInstances[k][t] = Instances[k][i];

				if (Instances[k][i] > Instances[k][i + 1] && Instances[k][i] > Instances[k][i - 1]) {

					normalizedInstances[k][t] = Alphabet[0];

				} else if (Instances[k][i] < Instances[k][i + 1] && Instances[k][i] < Instances[k][i - 1]) {

					normalizedInstances[k][t] = Alphabet[1];

				} else {

					growthRate = (Instances[k][i + 1] - Instances[k][i - 1]) / Math.abs(Instances[k][i - 1]);
					// System.out.println(i + " " + newNumericInstances[k][t] + " " + growthRate);
					if (growthRate >= Threshold) {
						normalizedInstances[k][t] = Alphabet[2];
					} else if (growthRate <= -Threshold) {
						normalizedInstances[k][t] = Alphabet[3];
					} else {
						normalizedInstances[k][t] = Alphabet[4];
					}
				}
				i++;
				// i += 2;
				t++;

			}
			newInstancesLength = --t;

		}
		System.out.println("After Discretization!! The new length is : " + newInstancesLength);

	}

	public void printNewNumericInstances() {

		for (i = 0; i < newInstancesLength; i++) {
			for (j = 0; j < columnsNumber; j++) {
				System.out.print(newNumericInstances[j][i] + ", ");
			}
			System.out.println();
		}
	}

	public void printNormalizedInstances() {

		for (i = 0; i < newInstancesLength; i++) {
			for (j = 0; j < columnsNumber; j++) {
				System.out.print(normalizedInstances[j][i] + ", ");
			}
			System.out.println();
		}
	}

	/* ************************** Get Set Method ************************************ */

	public char[][] getNormalizedInstances() {

		return normalizedInstances;
	}

	public double[][] getNewNumericInstances() {

		return newNumericInstances;
	}

	public int getNewInstancesLength() {

		return newInstancesLength;
	}

}
