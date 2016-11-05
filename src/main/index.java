/**
 * 2016 2016年4月27日 上午9:28:18
 * 瑞东
 * index
 * TODO
 */
package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import org.omg.CORBA.PRIVATE_MEMBER;

import Discretization.FeatureExtraction;
import jxlExcel.JxlExcel;
import myApriori.InterTemporalApriori;
import myApriori.IntraTemporalApriori;
import myApriori.myApriori;
import ruleEvaluation.RuleEvaluation;

/**
 * @author 瑞东 2016 2016年4月27日 上午9:28:18 瑞东 index TODO
 */
public class index {

	/**
	 * 2016年4月28日 下午9:46:26 瑞东 runInterApriori
	 * 
	 * @param nomimalInstances
	 * @param attributeName
	 * @param discretizationInstancesLength
	 * @param originalColumnsNumber
	 *            void TODO Operate the inter association rule
	 */
	public static void runInterApriori(char[][] nomimalInstances, String[] attributeName, int discretizationInstancesLength, int originalColumnsNumber, double intraMinSupport) {

		System.out.println("Start association, Get frequency : ");
		String[] stringNominalInstances = new String[originalColumnsNumber];
		// TreeSet[] maxFrequency = new TreeSet[originalColumnsNumber];
		ArrayList<String>[] arrayList = new ArrayList[originalColumnsNumber];
		discretizationInstancesLength++;
		IntraTemporalApriori intraTemporalApriori;
		// Get the most frequency
		for (int i = 0; i < originalColumnsNumber; i++) {
			// for (char temp[]: nomimalInstances) {
			System.out.print("The " + i + " column : ");
			stringNominalInstances[i] = String.valueOf(nomimalInstances[i]).substring(1, discretizationInstancesLength);
			intraTemporalApriori = new IntraTemporalApriori(stringNominalInstances[i], intraMinSupport, 10);
			// maxFrequency[i] = intraTemporalApriori.getMaxFrequency();// No need
			arrayList[i] = new ArrayList<String>();
			arrayList[i].addAll(intraTemporalApriori.getMaxFrequency());
			System.out.println(attributeName[i] + " : " + arrayList[i].toString() + "\n");
			// System.out.println(maxFrequency[i].toString() + "\n");
			// String intraTimeSeries = stringNominalInstances[i].substring(1,
			// numericToNominalConversion.getNewInstancesLength() + 1);
			// IntraTemporalApriori intraTemporalApriori = new
			// IntraTemporalApriori(intraTimeSeries, 0.1, 0.5);
			// intraTemporalApriori.run();
		}
		intraTemporalApriori = null;
		System.out.println("Start inter association : ");
		InterTemporalApriori interTemporalApriori = new InterTemporalApriori(stringNominalInstances, originalColumnsNumber, 15, 0.1, 0.9, intraMinSupport);
		stringNominalInstances = null;
		interTemporalApriori.setAttributeName(attributeName);
		attributeName = null;
		interTemporalApriori.setMaxFrequency(arrayList);
		arrayList = null;
		interTemporalApriori.run();
	}

	/**
	 * 2016年4月28日 下午11:15:04 瑞东 runIntraApriori
	 * 
	 * @param nomimalInstances
	 * @param discretizationInstancesLength
	 * @param originalColumnsNumber
	 *            void TODO Generate intra association rule
	 */
	public static void runIntraApriori(char[][] nomimalInstances, int discretizationInstancesLength, int originalColumnsNumber) {

		String[] stringNominalInstances = new String[originalColumnsNumber];
		for (int i = 0; i < originalColumnsNumber; i++) {

			System.out.println("The " + i + " column : ");
			stringNominalInstances[i] = new String(nomimalInstances[i]);
			String intraTimeSeries = stringNominalInstances[i].substring(1, discretizationInstancesLength + 1);
			IntraTemporalApriori intraTemporalApriori = new IntraTemporalApriori(intraTimeSeries, 0.05, 0.9);
			intraTemporalApriori.run();
		}
	}

	/**
	 * 2016年4月27日 上午9:28:19 瑞东 main
	 * 
	 * @param args
	 *            void TODO
	 */
	/// *********************************************************************************************//////
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		double Instances[][];
		char[][] nomimalInstances;
		String[] attributeName;
		final int originalColumnsNumber;
		final double originalInstancesLength;
		final int discretizationInstancesLength;
		final String inputExcelName = new String("160512KL20CMinMaxInput23Cyclic");
		// 160427KL20CMinMaxNromalizationInput
		// 160502Test
		// 160509KL20CMinMazNormalization19ColumnsInput
		// 160509KL20CMinMazNormalization19ColumnsInputContinue
		// 160512KL20CMinMaxInput23Cyclic
		// 160528Evaluation

		// JxlExcel jxlExcel = new
		// JxlExcel("160427KL20CMinMaxNromalizationInput", 0);
		JxlExcel jxlExcel = new JxlExcel(inputExcelName, 0);
		Instances = jxlExcel.getExcel();
		originalColumnsNumber = jxlExcel.getColumnsNum();
		originalInstancesLength = jxlExcel.getAttributeLength();
		attributeName = jxlExcel.getAttributeName();

		// *****************************Discretization ********************************//

		FeatureExtraction featureExtraction = new FeatureExtraction(Instances, originalInstancesLength, originalColumnsNumber, 1, 0.1);
		Instances = null;
		nomimalInstances = featureExtraction.getNormalizedInstances();
		discretizationInstancesLength = featureExtraction.getNewInstancesLength();

		// *****************************outPut to Excel file ********************************//

		// jxlExcel.setExcel("160428outputNewNumericInstances", 0, featureExtraction.getNewNumericInstances(), featureExtraction.getNewInstancesLength());
		// jxlExcel.setExcel("160509outputNormalizedInstances19", 0, featureExtraction.getNormalizedInstances(), featureExtraction.getNewInstancesLength());

		// * ***************************intra time series ***************************

		// runIntraApriori(nomimalInstances, discretizationInstancesLength, originalColumnsNumber);

		// * **************************Intra Rule Evaluation *****************************

		// RuleEvaluation ruleEvaluation = new RuleEvaluation(nomimalInstances[17], "tdddpdd", "d");
		// ruleEvaluation.NewRuleLocation();

		RuleEvaluation ruleEvaluation = new RuleEvaluation(nomimalInstances[15], nomimalInstances[21], "uuup", "upd");
		ruleEvaluation.NewTwoRuleLocation();

		// * **************************inter time series*****************************
		jxlExcel = null;
		featureExtraction = null;
		// runInterApriori(nomimalInstances, attributeName, discretizationInstancesLength, originalColumnsNumber, 0.05);
		attributeName = null;
		nomimalInstances = null;
		// runIntraApriori(nomimalInstances, discretizationInstancesLength,originalColumnsNumber);

	}
}
