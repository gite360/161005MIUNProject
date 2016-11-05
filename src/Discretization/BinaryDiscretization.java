/**
 * 2016 2016年4月28日 下午3:39:51
 * 瑞东
 * BinaryDiscretization
 * TODO
 */
package Discretization;

/**
 * @author 瑞东
 *         2016 2016年4月28日 下午3:39:51
 *         瑞东
 *         BinaryDiscretization
 *         TODO Deal with 0_1 attributes
 */
public class BinaryDiscretization {
	double[][] binaryInstances;
	double[][] newNumericInstances = new double[30][10000];
	char[][] binaryNormalizedInstances = new char[30][10000];
	double originalInstancesLength;
	int N;
	final static char[] Alphabet = { 'z', 'o' };// peak trough up down level


	public BinaryDiscretization(double[][] originalInstances, double originalInstancesLength, int N) {
		binaryInstances = originalInstances;
		this.originalInstancesLength = originalInstancesLength;
		this.N = N;
	}


	public char[][] getBinaryNormalizedInstances() {

//		int count = 0;
		for (int j = 0; j < 3; j++) {
			int t = 1;
//			count = 0;
			for (int i = N + 1; i <= originalInstancesLength - N - 1; i++) {
				newNumericInstances[j][t] = binaryInstances[j][i];
				if (binaryInstances[j][i] == 0) {
					binaryNormalizedInstances[j][t] = Alphabet[0];
				} else {
					binaryNormalizedInstances[j][t] = Alphabet[1];
//					count++;
				}
				i++;
				t++;
			}
//			System.out.println("count = "+ count);
		}
		return binaryNormalizedInstances;
	}
}
