package ruleEvaluation;

import org.omg.CORBA.SystemException;

public class RuleEvaluation {

	private char[] intraTimeSeries, leftTimeSeriesChar0, leftTimeSeriesChar1, rightTimeSeriesChar;
	private String lefttItem, leftItem, rightItem;

	/**
	 * @param intraTimeSeries
	 * @param leftItem
	 * @param rightItem
	 *            Rules for single time series
	 */
	public RuleEvaluation(char[] intraTimeSeries, String leftItem, String rightItem) {
		this.intraTimeSeries = intraTimeSeries;
		this.lefttItem = leftItem + rightItem;
	}

	/**
	 * @param leftTimeSeries
	 * @param rightTimeSeries
	 * @param leftItem
	 * @param rightItem
	 *            For the rules between two sensors
	 */
	public RuleEvaluation(char[] leftTimeSeries, char[] rightTimeSeries, String leftItem, String rightItem) {
		this.leftTimeSeriesChar0 = leftTimeSeries;
		this.rightTimeSeriesChar = rightTimeSeries;
		this.leftItem = leftItem;
		this.rightItem = rightItem;

	}

	/**
	 * 160309sax
	 * 2016 2016年6月20日 下午8:33:09 Max
	 * void
	 * TODO For intra time series, find the location of the rule.
	 */
	public void SAXRuleLocation() {

		String stringItraTimeSeries = String.valueOf(intraTimeSeries);
		// System.out.println(" start count rule support.");
		int index = -1;
		double startIndex = 0, endIndex = 0;
		int loopStrLength = stringItraTimeSeries.length() - lefttItem.length();
		while ((index = stringItraTimeSeries.indexOf(lefttItem)) != -1 && index <= loopStrLength) {
			startIndex = (index - 1) * 8 + 1;
			endIndex = startIndex + 80;
			System.out.println(index + " : " + startIndex + " - " + endIndex);
			stringItraTimeSeries = stringItraTimeSeries.replaceFirst(lefttItem, lefttItem.toUpperCase());
			// System.out.println(tempInterTimeSeries);
		}
	}

	/**
	 * 160309sax
	 * 2016 2016年6月23日 下午4:15:19 Max
	 * void
	 * TODO Find the location of the rule between two sensors
	 */
	public void SAXRuleTwoLocation() {

		String lefTimeSeries = String.valueOf(leftTimeSeriesChar0).substring(0, 1001);
		String rightTimeSeries = String.valueOf(rightTimeSeriesChar);
		// System.out.println(" start count rule support.");

		int leftIndex = -1, rightIndex = -1;

		while ((leftIndex = lefTimeSeries.indexOf(leftItem)) != -1) {
			System.out.print((leftIndex + 1) + "  ");
			if ((rightIndex = rightTimeSeries.substring(leftIndex, leftIndex + 15).indexOf(rightItem)) != -1) {
				System.out.println(leftIndex + rightIndex + 1);
			}
			lefTimeSeries = lefTimeSeries.replaceFirst(leftItem, leftItem.toUpperCase());
			// System.out.println(tempInterTimeSeries);
		}
	}

	/**
	 * 160421tmeporalapriorimethod
	 * 2016 2016年6月22日 下午7:32:48 Max
	 * void
	 * TODO Rule from the new discretization method
	 */
	public void NewRuleLocation() {

		String stringItraTimeSeries = String.valueOf(intraTimeSeries);
		// System.out.println(" start count rule support.");
		int index = -1;
		double startIndex = 0, endIndex = 0;
		int loopStrLength = stringItraTimeSeries.length() - lefttItem.length();
		while ((index = stringItraTimeSeries.indexOf(lefttItem)) != -1 && index <= loopStrLength) {
			startIndex = index * 2;
			endIndex = startIndex + 20;
			System.out.println(index + " : " + startIndex + " - " + endIndex);
			stringItraTimeSeries = stringItraTimeSeries.replaceFirst(lefttItem, lefttItem.toUpperCase());
			// System.out.println(tempInterTimeSeries);
		}
	}

	/**
	 * 160421tmeporalapriorimethod
	 * 2016 2016年6月23日 下午8:30:23 Max
	 * void
	 * TODO rule between two sensors from new discretization method
	 */
	public void NewTwoRuleLocation() {

		String lefTimeSeries = String.valueOf(leftTimeSeriesChar0).substring(0, 4319);
		String rightTimeSeries = String.valueOf(rightTimeSeriesChar);
		// System.out.println(" start count rule support.");
		int leftIndex = -1, rightIndex = -1;
		double startIndex = 0, endIndex = 0;
		// int loopStrLength = lefTimeSeries.length() - lefttItem.length();
		while ((leftIndex = lefTimeSeries.indexOf(leftItem)) != -1) {
			startIndex = leftIndex * 2;
			endIndex = startIndex + 8;
			System.out.print("L : "+startIndex + " - " + endIndex + "    ");
			if ((rightIndex = rightTimeSeries.substring(leftIndex, leftIndex + 15).indexOf(rightItem)) != -1) {
				int absoluteRightIndex = leftIndex + rightIndex;
				
				startIndex = absoluteRightIndex * 2;
				endIndex = startIndex + 6;
				
				System.out.println("R : "+startIndex + " - " + endIndex);
			}
			lefTimeSeries = lefTimeSeries.replaceFirst(leftItem, leftItem.toUpperCase());
			// System.out.println(tempInterTimeSeries);
		}
	}
}
