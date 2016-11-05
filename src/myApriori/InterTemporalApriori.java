/**
 * 2016 2016年4月7日 下午11:18:30 瑞东 InterTemporalApriori TODO
 */
package myApriori;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.omg.CORBA.SystemException;

/** @author 瑞东 2016 2016年4月7日 下午11:18:30 瑞东 InterTemporalApriori TODO */
public class InterTemporalApriori {

	private final double	minSupport;								// 最小支持度
	private final double	minIntraSupport;						// In the intra stage, the minmum of the
	private final double	minSupportNumber;
	private final double	minConfidience;							// 最小置信度
	private final int		windowSize;
	private final int		columnsNumber;
	private final String[]	interTimeSeries;
	private final double	linesNumber;
	private ArrayList[]		maxFrequency;							// 最大频繁集
	private String[]		attributeName;
	private StringBuffer	printRuleResult	= new StringBuffer();
	private final double	minActiveSupportNumber;
	private final int		loopStrLength;

	/**
	 * @param interTimeSeries
	 * @param columnsNumber
	 * @param windwSize
	 * @param minsup
	 * @param minconf
	 * @param minIntraSupport
	 */
	public InterTemporalApriori(String[] interTimeSeries, int columnsNumber, int windwSize, double minsup, double minconf, double minIntraSupport) {
		this.interTimeSeries = interTimeSeries;
		this.columnsNumber = columnsNumber;
		this.windowSize = windwSize;
		this.minSupport = minsup;
		this.minConfidience = minconf;
		this.minIntraSupport = minIntraSupport;
		this.linesNumber = interTimeSeries[0].length();
		this.minActiveSupportNumber = minSupport * (linesNumber - (windowSize << 1));
		this.loopStrLength = (int) linesNumber - (windowSize << 1);

		minSupportNumber = minsup * linesNumber;
		System.out.println("\nWindow Size : " + windowSize + "; minIntraSupport : " + minIntraSupport + "; Minium Support : " + minSupport + "; supportNumber : " + minSupportNumber + "; Minium Confidence : " + minConfidience);
	}

	/**
	 * 2016年4月21日 下午1:22:51 瑞东 setMaxFrequency
	 *
	 * @param maxFrequency
	 *            void TODO Get the frequent item of every column
	 */
	public void setMaxFrequency(ArrayList<String>[] maxFrequency) {
		this.maxFrequency = maxFrequency;
	}

	/**
	 * 2016年5月8日 下午3:02:52 瑞东 setAttributeName
	 * 
	 * @param attributeName
	 *            void TODO Get the name of every sensor
	 */
	public void setAttributeName(String[] attributeName) {

		this.attributeName = attributeName;
	}

	/**
	 * 2016年5月3日 上午10:04:31 瑞东 findImportantPoint
	 *
	 * @param input
	 * @return boolean TODO Whether exits the peak trough and one value points
	 */
	public boolean findImportantPoint(String input) {
		// "p+|t+|o+"
		final String REGEX = new String("p+|t+|o+");
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	// public double countSupportOfLeftItem(int leftIndex, String leftItem) {// k=x.length
	// int leftItemLength = leftItem.length();
	// double supportNumber = 0;
	// String subStr;
	// double loopStrLength = linesNumber - leftItemLength;
	// int jumpLength = leftItemLength - 1;
	// for (int i = 0; i <= loopStrLength; i++) {
	// subStr = interTimeSeries[leftIndex].substring(i, i + leftItemLength);
	// if (leftItem.equals(subStr)) {
	// supportNumber++;
	// i += jumpLength;
	// }
	// }
	// return supportNumber;
	// }

	/**
	 * 2016年5月8日 下午9:55:48 瑞东 countSupportOfLeftItem0
	 * 
	 * @param leftIndex
	 * @param leftItem
	 * @return double TODO Count the support of the item in the string.
	 */
	public double countSupportOfLeftItem(int leftIndex, String leftItem) {// k=x.length
		String tempInterTimeSeries = interTimeSeries[leftIndex];
		int leftItemLength = leftItem.length();
		double supportNumber = 0;
		int index = -1;
		while ((index = tempInterTimeSeries.indexOf(leftItem)) != -1) {
			// System.arraycopy(arg0, arg1, arg2, arg3, arg4);
			tempInterTimeSeries = tempInterTimeSeries.substring(index + leftItemLength);
			supportNumber++;
		}

		return supportNumber;
	}

	/**
	 * 2016年5月1日 下午8:19:46 瑞东 countRuleSupport
	 *
	 * @param leftIndex
	 * @param rightIndex
	 * @param leftItem
	 * @param rightItem
	 * @return int TODO count the support of the right side for the 2 columns association
	 */
	public int countRuleSupport(int leftIndex, int rightIndex, String leftItem, String rightItem) {
		// System.out.println(" start count rule support.");
		String tempInterTimeSeries = interTimeSeries[leftIndex];
		int ruleSupport = 0, index = -1;
		double loopStrLength = linesNumber - windowSize;
		while (((index = tempInterTimeSeries.indexOf(leftItem)) != -1) && (index <= loopStrLength)) {
			if (interTimeSeries[rightIndex].substring(index, index + windowSize).indexOf(rightItem) != -1) {
				ruleSupport++;
			}
			tempInterTimeSeries = tempInterTimeSeries.replaceFirst(leftItem, leftItem.toUpperCase());
			// if (leftItem.equals("p")) {
			// System.out.println(leftItem + " ：" + index);
			// System.out.println(tempInterTimeSeries);
			// }
		}
		return ruleSupport;
	}

	/**
	 * 2016年5月2日 上午9:38:59 瑞东 countRuleSupport3
	 *
	 * @param leftIndex1
	 * @param leftIndex2
	 * @param rightIndex
	 * @param leftItem1
	 * @param leftItem2
	 * @param rightItem
	 * @return int TODO Count the support for 3 columns
	 */
	public double[] countRuleSupport3(int leftIndex1, int leftIndex2, int rightIndex, String leftItem1, String leftItem2, String rightItem) {

		// System.out.println(" start count rule support.");
		String tempInterTimeSeries = interTimeSeries[leftIndex1];
		// String tempLeftItem2 = interTimeSeries[leftIndex2];
		int index1 = -1, index2 = -1;
		double[] ruleSupport = new double[2];

		while ((index1 = tempInterTimeSeries.indexOf(leftItem1)) != -1 && index1 <= loopStrLength) {
			// System.out.print(index1 + ", ");
			tempInterTimeSeries = tempInterTimeSeries.replaceFirst(leftItem1, leftItem1.toUpperCase());
			// System.out.println(tempInterTimeSeries);
			index2 = interTimeSeries[leftIndex2].substring(index1, index1 + windowSize).indexOf(leftItem2);

			if (index2 != -1) {// if exit the item2 in windowsize
				index2 += index1;
				// System.out.print(index2 + ", ");
				ruleSupport[0]++;
				int index3 = interTimeSeries[rightIndex].substring(index2, index2 + windowSize).indexOf(rightItem);
				if (index3 != -1) {
					// index3 += index2;
					// System.out.print(index3 + "; ");
					ruleSupport[1]++;
				}
			}

		}
		// System.out.print("\n");
		return ruleSupport;
	}

	/**
	 * 2016年4月21日 下午3:02:04 瑞东 twoRuleGeneration void TODO Select two columns of the whole timeseries to generate the rule
	 */
	public void twoRuleGeneration() {
		for (int i = 0; i < columnsNumber; i++) {
			for (int j = i; j < columnsNumber; j++) {
				System.out.println(i + " -> " + j);
				// printRuleResult.append(attributeName[i] + " : " + maxFrequency[i].toString() + "\t\n");
				// printRuleResult.append(attributeName[j] + " : " + maxFrequency[j].toString() + "\t\n");
				printRuleResult.append(attributeName[i]);
				printRuleResult.append(" : ");
				printRuleResult.append(maxFrequency[i].toString());
				printRuleResult.append("\n");
				printRuleResult.append(attributeName[j]);
				printRuleResult.append(" : ");
				printRuleResult.append(maxFrequency[j].toString());
				printRuleResult.append("\n");

				twoColumnsAssociate(i, j);
				if (j != i) {
					twoColumnsAssociate(j, i);
				}
			}
			ruleResultPrint(printRuleResult);
			printRuleResult = new StringBuffer();

		}

	}

	/**
	 * 2016年4月21日 下午3:02:04 瑞东 twoRuleGeneration void TODO Select two columns of the whole timeseries to generate the rule
	 */
	public void ruleGeneration3() {
		// int count = 0;
		for (int i = 0; i < columnsNumber; i++) {
			for (int j = i + 1; j < columnsNumber; j++) {
				for (int k = j + 1; k < columnsNumber; k++) {
					// int k = j;
					// k++;
					System.out.println(i + " , " + j + " -> " + k);
					// printRuleResult.append(attributeName[i] + " : " + maxFrequency[i].toString() + "\n");
					// printRuleResult.append(attributeName[j] + " : " + maxFrequency[j].toString() + "\n");
					// printRuleResult.append(attributeName[k] + " : " + maxFrequency[k].toString() + "\n");
					printRuleResult.append(i + " ， " + j + " -> " + k);
					printRuleResult.append("\n");
					printRuleResult.append(attributeName[i]);
					printRuleResult.append(" : ");
					printRuleResult.append(maxFrequency[i].toString());
					printRuleResult.append("\n");
					printRuleResult.append(attributeName[j]);
					printRuleResult.append(" : ");
					printRuleResult.append(maxFrequency[j].toString());
					printRuleResult.append("\n");
					printRuleResult.append(attributeName[k]);
					printRuleResult.append(" : ");
					printRuleResult.append(maxFrequency[k].toString());
					printRuleResult.append("\n");
					columnsAssociate3(i, j, k);
					columnsAssociate3(i, k, j);
					columnsAssociate3(j, i, k);
					columnsAssociate3(j, k, i);
					columnsAssociate3(k, i, j);
					columnsAssociate3(k, j, i);
					ruleResultPrint(printRuleResult);
					printRuleResult = new StringBuffer();
				}

			}

		}
		// System.out.println(count);

	}

	/**
	 * 2016年4月21日 下午3:01:21 瑞东 twoColumnsAssociate
	 *
	 * @param leftIndex
	 * @param rightIndex
	 *            void TODO for two columns, generate the rule from left to right
	 */
	public void twoColumnsAssociate(int leftIndex, int rightIndex) {

		// System.out.println(attributeName[leftIndex] + " >>>>>> " + attributeName[rightIndex]);
		// ruleMap.put(attributeName[leftIndex], attributeName[rightIndex]);
		// printRuleResult.append(" "+attributeName[leftIndex] + "\t=>\t" + attributeName[rightIndex] + "\n");
		printRuleResult.append(" ");
		printRuleResult.append(attributeName[leftIndex]);
		printRuleResult.append("\t=>\t");
		printRuleResult.append(attributeName[rightIndex]);
		printRuleResult.append("\n");

		Iterator leftIterator = maxFrequency[leftIndex].iterator();
		String leftItem, rightItem, tempLeftItem;
		int ruleSupport = 0;
		double confidence = 0;
		final double minActiveSupportNumber = minSupport * (linesNumber - windowSize);

		// int count = 0;
		while (leftIterator.hasNext()) {
			tempLeftItem = leftItem = leftIterator.next().toString();
			// System.out.println("leftItem ：" + leftItem);
			// System.out.println(count);
			// count++;
			Iterator rightIterator = maxFrequency[rightIndex].iterator();
			while (rightIterator.hasNext()) {
				rightItem = rightIterator.next().toString();
				// System.out.println("rightItem ：" + rightItem);
				ruleSupport = countRuleSupport(leftIndex, rightIndex, leftItem, rightItem);
				confidence = (ruleSupport / countSupportOfLeftItem(leftIndex, leftItem));

				// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				// if (findImportantPoint(leftItem) || findImportantPoint(rightItem)) {
				// ruleSupport *= 10;
				// }

				// System.out.println(leftItem + " : " + rightItem + "ruleSupport ：" + ruleSupport + " confidence : " + confidence);
				if (ruleSupport <= minActiveSupportNumber && confidence >= minConfidience) {
					// System.out.println("ruleMap ：" + leftItem + "->" + rightItem);
					// System.out.println(leftItem + (leftItem.length() < 5 ? "\t" : "") + "-->" + rightItem + "\t" + confidence);
					// printRuleResult.append("\t" + leftItem + (leftItem.length() < 5 ? "\t" : "") + "-->" + rightItem + "\t" + confidence + "\n");
					printRuleResult.append("\t");
					printRuleResult.append(leftItem);
					printRuleResult.append("\t");
					printRuleResult.append("-->");
					printRuleResult.append(rightItem);
					printRuleResult.append("\t");
					printRuleResult.append(ruleSupport);
					printRuleResult.append("\t");
					printRuleResult.append(confidence);
					printRuleResult.append("\n");
				}

				// leftItem = new String(tempLeftItem);
				// rightItem = null;

			}
		}
	}

	/**
	 * 2016年5月1日 下午8:27:05 瑞东 ColumnsAssociate3
	 *
	 * @param leftIndex1
	 * @param rightIndex
	 *            void TODO Generate the rule that with 3 columns
	 */
	public void columnsAssociate3(int leftIndex1, int leftIndex2, int rightIndex) {

		// System.out.println(attributeName[leftIndex1] + " > " + attributeName[leftIndex2] + " >>>>>> " + attributeName[rightIndex]);
		// ruleMap.put(attributeName[leftIndex1], attributeName[rightIndex]);
		// printRuleResult.append("\n" + attributeName[leftIndex1] + "\t>\t" + attributeName[leftIndex2] + "\t==>>\t" + attributeName[rightIndex] + "\n");
		printRuleResult.append("\n");
		printRuleResult.append(attributeName[leftIndex1]);
		printRuleResult.append("\t>\t");
		printRuleResult.append(attributeName[leftIndex2]);
		printRuleResult.append("\t==>>\t");
		printRuleResult.append(attributeName[rightIndex]);
		printRuleResult.append("\n");

		String leftItem1, leftItem2, rightItem;
		// String tempLeftItem1 = "";
		double ruleSupport[] = { 0, 0 };
		double confidenceOf3Attributes = 0;
		// int count = 0;
		Iterator leftIterator1 = maxFrequency[leftIndex1].iterator(), leftIterator2 = null, rightIterator = null;
		while (leftIterator1.hasNext()) {
			// System.out.println(count);
			// count++;
			leftItem1 = leftIterator1.next().toString();
			leftIterator2 = maxFrequency[leftIndex2].iterator();
			while (leftIterator2.hasNext()) {
				leftItem2 = leftIterator2.next().toString();
				rightIterator = maxFrequency[rightIndex].iterator();
				while (rightIterator.hasNext()) {
					rightItem = rightIterator.next().toString();
					// System.out.println("leftItem1 ：" + leftItem1 + ";
					// leftItem2 ：" + leftItem2 + "; rightItem ：" + rightItem);
					ruleSupport = countRuleSupport3(leftIndex1, leftIndex2, rightIndex, leftItem1, leftItem2, rightItem);
					if (ruleSupport[1] != 0) {
						confidenceOf3Attributes = ruleSupport[1] / ruleSupport[0];
						// System.out.println("Confidence ：" + confidenceOf3Attributes + "Support1 : " + ruleSupport[1] + " Support0 : " + ruleSupport[0]);
						// System.out.println(leftItem1 + " " + findImportantPoint(leftItem1));

						// if (findImportantPoint(leftItem1) || findImportantPoint(leftItem2) || findImportantPoint(rightItem)) {
						// ruleSupport[1] *= 10;
						// }

						// System.out.println(leftItem + " : " + rightItem + "ruleSupport ：" + ruleSupport + " confidence : " + confidence);
						// if (ruleSupport[1] > 5 && ruleSupport[1] <= minActiveSupportNumber && confidenceOf3Attributes >= minConfidience) {
						if (ruleSupport[1] > 4 && ruleSupport[1] <= minActiveSupportNumber && confidenceOf3Attributes >= minConfidience) {
							// System.out.println(leftItem1 + " > " + leftItem2 + (leftItem2.length() < 5 ? "\t" : "") + "-->" + rightItem + "\t" + confidenceOf3Attributes);
							// printRuleResult.append(" " + leftItem1 + "\t>\t" + leftItem2 + "\t-->" + rightItem + "\t" + confidenceOf3Attributes + "\n");
							printRuleResult.append(leftItem1);
							printRuleResult.append("\t>\t");
							printRuleResult.append(leftItem2);
							printRuleResult.append("\t-->");
							printRuleResult.append(rightItem);
							printRuleResult.append("\t");
							printRuleResult.append(ruleSupport[1]);
							printRuleResult.append("\t");
							printRuleResult.append(confidenceOf3Attributes);
							printRuleResult.append("\n");
						}
					}

				}
			}

		}
	}

	public void rulePrint() {

		// System.out.println("Association Rule ：");
		double confidence = 0;
		// output to the txt file
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(outPutTXTPathAndName, false);
			bw = new BufferedWriter(fw);
			bw.write(" minIntraSupport = " + minIntraSupport + " minsup = " + minSupport + " supportNumber : " + minSupportNumber + " minconf = " + minConfidience + " window size = " + windowSize);
			bw.newLine();
			bw.write("Association Rule: ");
			bw.newLine();
			bw.write(printRuleResult.toString());
			bw.newLine();
			// bw.write(sb.toString());
			// bw.newLine();
			bw.flush();// press the memory to file
			if (bw != null) {
				bw.close();
			}
		} catch (Exception e) {
			System.out.println("Output to the txt file failed!!!!!!!");
			e.printStackTrace();
		}
	}

	public void titlePrint() {

		// System.out.println("Association Rule ：");
		// output to the txt file
		SimpleDateFormat currentSystemTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// Current System time
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(outPutTXTPathAndName, false);
			bw = new BufferedWriter(fw);
			bw.write("Frequency: supportOfItem >= 10 && supportOfItem <= minActiveSupportNumber && candidateItem.length() < 10  Rule: ruleSupport[1] <= minActiveSupportNumber && confidenceOf3Attributes >= minConfidience");
			bw.newLine();
			bw.write(currentSystemTime.format(new Date()) + " minIntraSupport = " + minIntraSupport + " minsup = " + minSupport + " supportNumber : " + minSupportNumber + " minconf = " + minConfidience + " window size = " + windowSize);
			bw.newLine();
			bw.write("Association Rule: ");
			bw.newLine();
			// bw.write(printRuleResult.toString());
			// bw.newLine();
			// bw.write(sb.toString());
			// bw.newLine();
			bw.flush();// press the memory to file
			if (bw != null) {
				bw.close();
			}
		} catch (Exception e) {
			System.out.println("Output to the txt file failed!!!!!!");
			e.printStackTrace();
		}
	}

	public void ruleResultPrint(StringBuffer printRuleResult) {

		// System.out.println("Association Rule ：");
		// output to the txt file
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(outPutTXTPathAndName, true);
			bw = new BufferedWriter(fw);
			bw.write(printRuleResult.toString());
			bw.newLine();
			bw.flush();// press the memory to file
			if (bw != null) {
				bw.close();
			}
		} catch (Exception e) {
			System.out.println("Output to the txt file failed!!!!!!");
			e.printStackTrace();
		}
	}

	public void operationTimePrint(long operationTime) {

		// System.out.println("Association Rule ：");
		// output to the txt file
		SimpleDateFormat currentSystemTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// Current System time
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(outPutTXTPathAndName, true);
			bw = new BufferedWriter(fw);
			bw.write(currentSystemTime.format(new Date()) + "  " + operationTime + " ms");
			bw.newLine();
			bw.flush();// press the memory to file
			if (bw != null) {
				bw.close();
			}
		} catch (Exception e) {
			System.out.println("Output to the txt file failed!!!!!!");
			e.printStackTrace();
		}
	}

	private final static String outPutTXTPathAndName = new String("160422Sources/160509Rule/160519C23RuleThreeMinFreMinSupCyclic.txt");

	public void run() {
		long startTime = System.currentTimeMillis();

		titlePrint();
		 twoRuleGeneration();
		ruleGeneration3();
		// rulePrint();

		long operationTime = System.currentTimeMillis() - startTime;
		System.out.println("System cost : " + operationTime + " ms");
		SimpleDateFormat currentSystemTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		System.out.println(currentSystemTime.format(new Date()));// new Date()为获取当前系统时间
		operationTimePrint(operationTime);

	}

}
