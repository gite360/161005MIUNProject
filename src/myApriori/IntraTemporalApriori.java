package myApriori;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import org.omg.CORBA.SystemException;

/**
 * @author 瑞东
 * @see associate to the single time series.
 */
public class IntraTemporalApriori {

	private static final String	REGEX			= "p+|t+|o+";
	private static final String	REGEX0			= "z{4,}|u{4,}|d{4,}|l{4,}";// at least 3 same point
	private double				minsup;										// 最小支持度
	private double				minconf;									// 最小置信度
	private double				minAbsoluteSupportNumber;
	private double				instancesLength;
	// 注意使用IdentityHashMap，否则由于关联规则产生存在键值相同的会出现覆盖
	private IdentityHashMap		ruleMap			= new IdentityHashMap();
	private String[]			transSet;									// 事务集合，可以根据需要从构造函数里传入
	// private int itemCounts;// 候选1项目集大小,即字母的个数
	private TreeSet[]			frequencySet	= new TreeSet[40];			// 频繁项集数组，[0]:代表1频繁集...
	private TreeSet[]			candidateSet	= new TreeSet[40];			// 候选集数组
	private TreeSet				maxFrequency	= new TreeSet();			// 最大频繁集
	private TreeSet				candidate		= new TreeSet();			// 1候选集
	private int					frequencySum;								// Total number of the frequency[];
	private String				intraTimeSeries;
	private Map[]				hashMap			= null;						// store the frequency of Every item.
	// private TreeSet filtedFrequency = new TreeSet();// filt the same symbol string

	public IntraTemporalApriori(String intraTimeSeries, double minSupport, double minConfidence) {
		this.intraTimeSeries = intraTimeSeries;
		instancesLength = intraTimeSeries.length();
		this.minsup = minSupport;
		minAbsoluteSupportNumber = instancesLength * minsup;
		this.minconf = minConfidence;
		System.out.println("minSupport = " + minSupport + ". minSupportNumber = " + minAbsoluteSupportNumber + ". minConfidence = " + minConfidence);
		// maxFrequency = new TreeSet();
		// System.out.println("The length of the time series = " + intraTimeSeries.length());
		int itemCounts = countCandidate1();// 初始化1候选集的大小
		// System.out.println("The length of the Candidate 1_itemset = " + itemCounts);
		// 初始化其他两个

		for (int i = 0; i < 40; i++) {// !!!!!!!itemCounts!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			frequencySet[i] = new TreeSet();
			candidateSet[i] = new TreeSet();
		}
		candidateSet[0] = candidate;
		frequencySet[0] = candidateSet[0];
	}

	/**
	 * 2016 2016年4月4日 下午9:06:38 瑞东
	 * countCandidate1
	 * TODO Generate the 1-Candidate
	 * 
	 * @return
	 * 		int the length of the 1-candidate
	 */
	public int countCandidate1() {

		char temp;
		// 遍历所有事务集String 加入集合，set自动去重了
		for (int i = 0; i < instancesLength; i++) {
			temp = intraTimeSeries.charAt(i);
			// System.out.println(temp);
			candidate.add(String.valueOf(temp));
		}
		return candidate.size();
	}

	// public double countSupportOfItem(String x) {// k=x.length
	// int windowSize = x.length();
	// double supportNumber = 0;
	// String subStr = null;
	// for (int i = 0; i < instancesLength - windowSize + 1; i++) {
	// subStr = intraTimeSeries.substring(i, i + windowSize);
	// if (x.equals(subStr)) {
	// supportNumber++;
	// i += (windowSize - 1);
	// }
	// }
	// return supportNumber;
	// }

	/**
	 * 2016年5月8日 下午9:55:48 瑞东 countSupportOfLeftItem0
	 * 
	 * @param leftIndex
	 * @param candidateItem
	 * @return double TODO Count the support of the item in the string.
	 */
	public double countSupportOfItem(String candidateItem) {// k=x.length
		String tempAttribute = new String(intraTimeSeries);

		int leftItemLength = candidateItem.length();
		double supportNumber = 0;
		int index = -1;
		while ((index = tempAttribute.indexOf(candidateItem)) != -1) {
			tempAttribute = new String(tempAttribute.substring(index + leftItemLength));
			supportNumber++;
		}

		return supportNumber;
	}

	public void canditate_gen(int k) {

		String y = "", z = "", m = "";
		// char c1 = 'a', c2 = 'a';
		char c1, c2;
		Iterator temp1 = frequencySet[k - 2].iterator();
		Iterator temp2 = frequencySet[0].iterator();
		TreeSet h = new TreeSet();
		while (temp1.hasNext()) {
			y = (String) temp1.next();
			c1 = y.charAt(y.length() - 1);
			// System.out.println(y + " , " + c1);
			while (temp2.hasNext()) {
				z = (String) temp2.next();
				// System.out.println("canditate_gen(int k) z = "+z);
				c2 = z.charAt(0);
				if (c1 >= c2)
					continue;
				else {
					m = y + z;
					// System.out.println("canditate_gen(int k) : y = " + y + " z = " + z + " m = " + m);
					h.add(m);
				}
			}
			temp2 = frequencySet[0].iterator();
		}
		candidateSet[k - 1] = h;
	}

	/**
	 * 2016 2016年4月4日 下午8:43:14 瑞东
	 * canditate2_gen
	 * TODO Generate candidate 2-itemset: Traditional Join. Inverted Join. Self Join
	 * void
	 */
	public void canditate2_gen() {

		String y = "", z = "", m = "";
		// char c1 = 'a', c2 = 'a';
		char c1, c2;
		Iterator temp1 = candidateSet[0].iterator();
		Iterator temp2 = candidateSet[0].iterator();
		TreeSet h = new TreeSet();
		while (temp1.hasNext()) {
			y = (String) temp1.next();
			c1 = y.charAt(y.length() - 1);
			// System.out.println(y + " , " + c1);
			// if (y.equals("o") || y.equals("t") || y.equals("p")) {
			// System.out.println("*********************" + y);
			// h.add(y);
			// }
			while (temp2.hasNext()) {
				z = (String) temp2.next();
				c2 = z.charAt(0);
				m = y + z;
				// System.out.println("canditate2_gen(int k) : y = " + y + " z = " + z + " m = " + m);
				h.add(m);
				// }
			}
			temp2 = candidateSet[0].iterator();
		}
		candidateSet[1] = h;
		// System.out.println("1-canditate: " + candidateSet[0]);
		// System.out.println("2-canditate: " + candidateSet[1]);
	}

	/**
	 * 2016年4月7日 上午9:15:49 瑞东
	 * canditateGeneration @param k
	 * void
	 * TODO k-candidate generation
	 */
	public void kCanditateGeneration(int k) {

		// System.out.println(k + "-candidate:");
		String y = "", z = "", m = "";
		String rightSubString0 = "", leftSubString0 = "";
		String rightSubString1 = "", leftSubString1 = "";
		String newLeftSelfJoin, newRightSelfJoin;
		String newLeftMostItem0 = "", newLeftMostItem1 = "", newRightMostItem0 = "", newRightMostItem1 = "";
		char c1, c2;
		Iterator temp1 = frequencySet[k - 2].iterator();
		Iterator temp2 = frequencySet[k - 2].iterator();
		TreeSet h = new TreeSet();

		while (temp1.hasNext()) {
			y = (String) temp1.next();

			// Left and Right Self Join
			// System.out.println(y);
			newLeftSelfJoin = y.charAt(0) + y;
			newRightSelfJoin = y + y.charAt(k - 2);
			h.add(newLeftSelfJoin);
			h.add(newRightSelfJoin);
			// System.out.print(newLeftSelfJoin + " " + newRightSelfJoin + " ");

			// Left-most Join and Right-most Join
			rightSubString0 = y.substring(1);
			leftSubString0 = y.substring(0, k - 2);

			// System.out.println(y + " , " + c1);
			while (temp2.hasNext()) {
				z = (String) temp2.next();
				rightSubString1 = z.substring(1);
				leftSubString1 = z.substring(0, k - 2);
				if (rightSubString0.equals(rightSubString1)) {
					newRightMostItem0 = z.charAt(0) + y;
					newRightMostItem1 = y.charAt(0) + z;
					h.add(newRightMostItem0);
					h.add(newRightMostItem1);
					// System.out.print(newRightMostItem0 + " " + newRightMostItem1 + " ");
				}
				if (leftSubString0.equals(leftSubString1)) {
					newLeftMostItem0 = y + z.charAt(k - 2);
					newLeftMostItem1 = z + y.charAt(k - 2);
					h.add(newLeftMostItem0);
					h.add(newLeftMostItem1);
					// System.out.print(newLeftMostItem0 + " " + newLeftMostItem1 + " ");
				}

			}
			temp2 = frequencySet[k - 2].iterator();

		}
		candidateSet[k - 1] = h;

		// **********************print the k-candidate
		// Iterator printResult = candidateSet[k - 1].iterator();
		// while (printResult.hasNext()) {
		// System.out.print(printResult.next() + " ");
		// }
		// System.out.println(".");
	}

	// // k候选集=>k频繁集
	// /**
	// * @param k
	// */
	// public void frequent_gen(int k) {
	//
	// String s1 = "";
	// Iterator ix = candidateSet[k - 1].iterator();
	// while (ix.hasNext()) {
	// s1 = (String) ix.next();
	// if (count_sup(s1) >= (minsup * transSet.length)) {
	// frequencySet[k - 1].add(s1);
	// }
	// }
	// }

	/**
	 * 2016年5月3日 上午10:04:31 瑞东
	 * findImportantPoint
	 * 
	 * @param input
	 * @return boolean
	 *         TODO Whether exits the peak trough and one value points
	 */
	public boolean findImportantPoint(String input) {

		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	/**
	 * 2016年5月3日 下午2:02:45 瑞东
	 * matchPepeatedCommonPoint
	 * 
	 * @param input
	 * @return boolean
	 *         TODO deter whether is llllll dddddd zzzzzz value
	 */
	public boolean matchPepeatedCommonPoint(String input) {

		Pattern pattern = Pattern.compile(REGEX0);
		Matcher matcher = pattern.matcher(input);
		return matcher.find();
	}

	/**
	 * 2016年4月7日 上午9:16:34 瑞东
	 * frequent2_gen @param k
	 * void
	 * TODO k-frequency generation
	 */
	public void kFrequencyGeneration(int k) {

		// System.out.println(k + "-Frequency: " + "k-minimum support = " + (minsup * (intraTimeSeries.length() / k)));

		String candidateItem;
		double supportOfItem = 0;
		double minActiveSupportNumber = 0;
		Iterator candidateItemSet = candidateSet[k - 1].iterator();
		// hashMap[2] = new HashMap();
		while (candidateItemSet.hasNext()) {
			candidateItem = candidateItemSet.next().toString();
			// System.out.println(temp1);
			supportOfItem = countSupportOfItem(candidateItem);

			// System.out.print(candidateItem + " : " + m);
			// if (m >= (int) (minsup * (intraTimeSeries.length() / k))) {// Hint!!!!! With the window_size increase the support can be change
			// if (m / (strLength / k) >= minsup) {//same result with above
			//
			// if (temp1.indexOf('o') != -1) {
			// frequencySet[k - 1].add("o");
			// }
			// if (temp1.indexOf('p') != -1) {
			// frequencySet[k - 1].add("p");
			// }
			// if (temp1.indexOf('t') != -1) {
			// frequencySet[k - 1].add("t");
			// }

			// if (!matchPepeatedCommonPoint(candidateItem)) {
			// m *= findImportantPoint(candidateItem) ? 10 : 1;
			// // System.out.println(candidateItem);
			// // System.out.println(" " + m);
			// }
			// System.out.println(candidateItem+" "+m + " " + findImportantPoint(candidateItem));

			minActiveSupportNumber = minsup * (instancesLength - candidateItem.length());
//			 if (supportOfItem >= minActiveSupportNumber) {
			if (supportOfItem >= 5 && supportOfItem <= minActiveSupportNumber && candidateItem.length() < 10) {
				// System.out.println(temp1 + "=" + m + " ");
				// System.out.println(candidateItem);
				frequencySet[k - 1].add(candidateItem);
			}

		}

	}

	/**
	 * 2016年4月7日 上午9:36:15 瑞东
	 * is_frequent_empty
	 * 
	 * @param k
	 * @return boolean
	 *         TODO Judge whether the frequency is empty
	 */
	public boolean is_frequent_empty(int k) {

		if (frequencySet[k - 1].isEmpty())
			return true;
		else
			return false;
	}

	/**
	 * 2016年4月7日 上午9:36:06 瑞东
	 * included
	 * 
	 * @param s1
	 * @param s2
	 * @return boolean
	 *         TODO
	 */
	public boolean included(String s1, String s2) {

		for (int i = 0; i < s1.length(); i++) {
			if (s2.indexOf(s1.charAt(i)) == -1)
				return false;
			else if (i == s1.length() - 1)
				return true;
		}
		return true;
	}

	/**
	 * 2016年4月7日 上午11:16:13 瑞东
	 * maxfrequent_gen void
	 * TODO Get the all the frequency items
	 */
	public void maxfrequent_gen() {

		// if (intraTimeSeries.indexOf('o') != -1) {
		// maxFrequency.add("o");
		// }
		// if (intraTimeSeries.indexOf('p') != -1) {
		// maxFrequency.add("p");
		// }
		// if (intraTimeSeries.indexOf('t') != -1) {
		// maxFrequency.add("t");
		// }

		for (int i = 1; i <= frequencySum; i++) {
			maxFrequency.addAll(frequencySet[i]);// TreeSet store the item by order
			// System.out.println(frequencySet[i].toString());
		}

	}

	public void print_maxfrequent() {

		Iterator iterator = maxFrequency.iterator();
		System.out.print("产生规则频繁项集：");
		while (iterator.hasNext()) {
			// System.out.print(toDigit((String) iterator.next()) + "\t");
			System.out.print(toDigit((String) iterator.next()) + ", ");
		}
		System.out.println();
	}

	public void rulePrint() {

		String x, y;
		double supportNumber, confidence = 0;
		Set hs = ruleMap.keySet();
		Iterator iterator = hs.iterator();
		StringBuffer sb = new StringBuffer();

		System.out.println(maxFrequency.toString());
		System.out.println("Association Rule ：");
		while (iterator.hasNext()) {
			x = (String) iterator.next();
			y = (String) ruleMap.get(x);
			supportNumber = countSupportOfItem(x + y);
			confidence = (supportNumber / countSupportOfItem(x));

			// x = toDigit(x);
			// y = toDigit(y);
			System.out.println(x + (x.length() < 5 ? "\t" : "") + "-->" + y + "\t" + supportNumber + "\t" + confidence);
			sb.append("  " + x + (x.length() < 5 ? "\t" : "") + "-->" + y + "\t" + supportNumber + "\t" + confidence + "\t\n");
		}

		// output to the txt file
		SimpleDateFormat currentSystemTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// Current System time
		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(outPutTXTPathAndName, true);
			bw = new BufferedWriter(fw);
			bw.write("Frequency: supportOfItem >= 5 && supportOfItem <= minActiveSupportNumber && candidateItem.length() < 10  Rule: confidenceOf3Attributes >= minConfidience");
			bw.newLine();
			bw.write(currentSystemTime.format(new Date()) + " minIntraSupport = " + minsup + " minsupNumber = " + minAbsoluteSupportNumber + " minconf = " + minconf);
			bw.newLine();
			bw.write(maxFrequency.toString());
			bw.newLine();
			bw.write("Association Rule: ");
			bw.newLine();
			bw.write(sb.toString());
			bw.newLine();
			bw.flush();
			if (bw != null)
				bw.close();
		} catch (Exception e) {
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

	// public void subGen(String s) {
	//
	// String x = "", y = "";
	// for (int i = 1; i < (1 << s.length()) - 1; i++) {
	// for (int j = 0; j < s.length(); j++) {
	// if (((1 << j) & i) != 0) {
	// x += s.charAt(j);
	// }
	// }
	// for (int j = 0; j < s.length(); j++) {
	// if (((1 << j) & (~i)) != 0) {
	// y += s.charAt(j);
	// }
	// }
	// // System.out.println("subGen() : " + "x = " + x + ", y = " + y + ", x + y = " + x + y + countSupportOfItem(x + y) + " " + countSupportOfItem(x));
	// if (countSupportOfItem(x + y) / countSupportOfItem(x) >= minconf) {
	// ruleMap.put(x, y);
	// }
	// x = "";
	// y = "";
	// }
	// }

	/**
	 * 2016年4月14日 下午1:09:00 瑞东
	 * itemRuleGeneration
	 * 
	 * @param frequencyItem
	 *            void
	 *            TODO Generate the rule from one string which from most frequency string.
	 */
	public void itemRuleGeneration(String frequencyItem) {

		String leftItem = "", rightItem = "";
		for (int i = 1; i < frequencyItem.length(); i++) {

			leftItem = frequencyItem.substring(0, i).trim();
			rightItem = frequencyItem.substring(i).trim();

			// System.out.println("subGen() : " + "leftItem = " + leftItem + ", rightItem = " + rightItem + ", leftItem + rightItem = " + leftItem + rightItem + countSupportOfItem(leftItem + rightItem)
			// + " " + countSupportOfItem(leftItem));
			if (countSupportOfItem(frequencyItem) / countSupportOfItem(leftItem) >= minconf) {
				ruleMap.put(leftItem, rightItem);

			}
			leftItem = "";
			rightItem = "";
		}
	}

	public void ruleGen() {

		String s;
		Iterator iterator = maxFrequency.iterator();
		// Iterator iterator = filtedFrequency.iterator();

		while (iterator.hasNext()) {
			s = (String) iterator.next();
			// System.out.println("***************************************");
			// System.out.println("maxFrequency[] : " + s);
			// subGen(s);
			itemRuleGeneration(s);
		}
	}

	// public void print_canditate() {
	//
	// for (int i = 0; i < frequencySet[0].size(); i++) {
	// Iterator ix = candidateSet[i].iterator();
	// Iterator iy = frequencySet[i].iterator();
	// System.out.print("候选集" + (i + 1) + ":");
	// while (ix.hasNext()) {
	// System.out.print((String) ix.next() + "\t");
	// // System.out.print(toDigit((String) ix.next()) + "\t");
	// }
	// System.out.print("\n" + "频繁集" + (i + 1) + ":");
	// while (iy.hasNext()) {
	// System.out.print((String) iy.next() + "\t");
	// // System.out.print(toDigit((String) iy.next()) + "\t");
	// }
	// System.out.println();
	// }
	// }

	/**
	 * 2016年4月7日 上午9:54:52 瑞东
	 * printCanditateAndFrequencyResult void
	 * TODO Print out the result of the canidate and frequency
	 */
	public void printCanditateAndFrequencyResult() {

		for (int i = 0; i < frequencySum; i++) {
			Iterator ix = candidateSet[i].iterator();
			Iterator iy = frequencySet[i].iterator();
			System.out.print("候选集" + (i + 1) + ":");
			while (ix.hasNext()) {
				System.out.print((String) ix.next() + "\t");
				// System.out.print(toDigit((String) ix.next()) + "\t");
			}
			System.out.print("\n" + "频繁集" + (i + 1) + ":");
			while (iy.hasNext()) {
				System.out.print((String) iy.next() + "\t");
				// System.out.print(toDigit((String) iy.next()) + "\t");
			}
			System.out.println();
		}
	}

	private String toDigit(String str) {

		if (str != null) {
			StringBuffer temp = new StringBuffer();
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				temp.append(((int) c - 96) + " ");
				// temp.append(((int) c - 65) + " ");
			}
			return temp.toString();
		} else {
			return null;
		}
	}

	/**
	 * 判断是否是由同一字符构成的，true代表由同一字符构成的 false反之
	 */
	public static boolean isSameSymbol(String s) {

		boolean flag = false;
		// 当s为空字符串或者null,认为不是由同一字符构成的
		if (s == null) {
			return flag;
		}
		// 当只有一个字符的时候，认为由同一字符构成
		if (1 == s.length()) {
			flag = true;
			return flag;
		}
		char chacter = s.charAt(1);
		for (int i = 1; i <= s.length() - 2; i++) {
			if (chacter != s.charAt(i)) {
				flag = false;
				return flag;
			}
		}
		flag = true;
		return flag;
	}

	public void printK_MinimumSupport() {

		double k = 2;
		for (k = 2; k <= 10; k++) {
			System.out.print(k + "=" + minsup * (intraTimeSeries.length() / k) + " ");
		}
		System.out.print("\n");
	}

	/**
	 * 2016年4月7日 下午8:29:33 瑞东
	 * filteSameSymble void
	 * TODO filt the same symbol string.
	 */
	// public void filteSameSymble() {
	//
	// String item;
	// Iterator iterator = maxFrequency.iterator();
	// while (iterator.hasNext()) {
	// item = iterator.next().toString();
	// if (!isSameSymbol(item)) {
	// filtedFrequency.add(item);
	// }
	// }
	// }

	public double getIntraMinSupport() {

		return minsup;
	}

	public String[] getTrans_set() {

		return transSet;
	}

	public void setTrans_set(String[] transSet) {

		transSet = transSet;
	}

	public void setMinsup(double minsup) {

		this.minsup = minsup;
	}

	/**
	 * @return
	 */
	public double getMinconf() {

		return minconf;
	}

	/**
	 * 
	 * @param minconf
	 */
	public void setMinconf(double minconf) {

		this.minconf = minconf;
	}

	private final static String outPutTXTPathAndName = new String("160422Sources/160509Rule/160528C12IntraTwoMinFreEvaluationP.txt");

	/* *************************************************** */
	/**
	 * 
	 */
	public void run() {
		long startTime = System.currentTimeMillis();
		// printK_MinimumSupport();
		int k = 2;
		// item1_gen();// produce the Candidate 1_itemset
		canditate2_gen();
		kFrequencyGeneration(2);
		do {
			k++;
			if (k > 40)
				break;
			kCanditateGeneration(k); // k>2
			// canditate_gen(k);
			kFrequencyGeneration(k);
			// if (frequencySet[k - 1].size() == 1 && isSameSymbol(frequencySet[k - 1].toString())) {
			// break;
			// }

		} while (!is_frequent_empty(k));

		frequencySum = k - 1;
		// printCanditateAndFrequencyResult();
		maxfrequent_gen();
		// print_maxfrequent();
		// filteSameSymble();
		ruleGen();
		rulePrint();

		long operationTime = System.currentTimeMillis() - startTime;
		System.out.println("System cost : " + operationTime + " ms");
		SimpleDateFormat currentSystemTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		System.out.println(currentSystemTime.format(new Date()));// new Date()为获取当前系统时间
		operationTimePrint(operationTime);
	}

	/**
	 * 2016年5月3日 下午2:04:01 瑞东
	 * getMaxFrequency
	 * 
	 * @return TreeSet
	 *         TODO
	 */
	public TreeSet getMaxFrequency() {

		// printK_MinimumSupport();
		int k = 2;
		// item1_gen();// produce the Candidate 1_itemset
		canditate2_gen();
		kFrequencyGeneration(2);
		do {
			k++;
			if (k > 40)
				break;
			kCanditateGeneration(k); // k=3
			// canditate_gen(k);
			kFrequencyGeneration(k);
			// if (frequencySet[k - 1].size() == 1 && isSameSymbol(frequencySet[k - 1].toString())) {
			// break;
			// }

		} while (!is_frequent_empty(k));

		frequencySum = k - 1;
		// printCanditateAndFrequencyResult();
		maxfrequent_gen();
		return maxFrequency;
	}

}
