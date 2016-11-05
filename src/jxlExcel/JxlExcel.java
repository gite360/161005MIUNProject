package jxlExcel;

import java.io.File;
import jxl.*;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import java.util.*;

public class JxlExcel {

	// public static String FilePath = "D:/Program Files/160225eclipseCode/160421TmeporalAprioriMethod/160422Sources/160422dataSet" + "/";
	private final static String FilePath = "160422Sources/160422dataSet" + "/";
	// "160422Sources/160422dataSet"
	private static double AttributeLength; // the length of the row
	private static int columnsNumber;
	// int point;
	// double w; // 降维的数量
	private String[] attributeName = new String[30];
	private Sheet sheet;
	private Workbook book;

	/* ************************** initial method ************************ */

	public JxlExcel(String excelName, int sheetNumber) {
		// t.xls为要读取的excel文件名
		try {
			// Cell[] cell = new Cell[1000];
			// Arrays.fill(cell, null);
			book = Workbook.getWorkbook(new File(FilePath + excelName + ".xls"));
			sheet = book.getSheet(sheetNumber);
			columnsNumber = sheet.getColumns();
			this.AttributeLength = sheet.getRows() - 1;
			// book.close();
			System.out.println("Successful! Excel name:" + excelName + ".xls. Sheet order:" + sheetNumber + "." + " Columns:" + columnsNumber + " Length:" + AttributeLength);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Can not read the Excel!!!!");
		}
	}

	public double getAttributeLength() {

		return AttributeLength;
	}

	public int getColumnsNum() {

		return columnsNumber;
	}

	/* ********************read Excel file******************** */

	public double[][] getExcel() {

		double[][] excelContent = new double[30][10000];
		Cell[] cell = new Cell[2000];
		Arrays.fill(cell, null);
		int i = 0, j = 0;
		try {

			// get the title of every attribute
			for (i = 0; i < columnsNumber; i++) {
				cell[i] = sheet.getCell(i, 0);
				attributeName[i] = cell[i].getContents();
				// System.out.print(attributeName[i] + " ");
			}
			// System.out.print("\n");

			// 获取每一行的单元格
			i = 1;
			while (i <= AttributeLength) {
				// cell0 = sheet.getCell(0, i);// （列，行）
				for (j = 0; j < columnsNumber; j++) {
					cell[j] = sheet.getCell(j, i);
					/* if ("".equals(cell[0].getContents()) == true) { System.out.println( "Seccessiful get values from memory!!!!!!!!!."); book.close(); return column; } */
					// System.out.print(cell[j].getContents() + " , ");
					// System.out.print(cell[j].getType() + " , ");// Return the type of the data
					excelContent[j][i] = Double.parseDouble(cell[j].getContents());
					// System.out.print(excelContent[j][i] + " , ");
				}
				// System.out.print("\n");
				i++;
				// 如果读取的数据为空
				// if ("".equals(cell[0].getContents()) == true) {
				// System.out.print("Seccessiful get values from ");
				// break;
				// }
			}
			// System.out.println("Seccessiful store the value in column.");
			// System.out.println();
			System.out.println("Seccessiful get values from memory. Get " + --i + " rows value.");
			book.close();
		} catch (Exception e) {
			System.out.println("Fail to store the value in column !!!!!!!!!!");
		}
		return excelContent;
	}

	/**
	 * 2016年4月21日 下午1:54:16 瑞东
	 * getAttributeName
	 * 
	 * @return String[]
	 *         TODO Get the name of every column
	 */
	public String[] getAttributeName() {

		return attributeName;
	}

	/* ************* DOUBLE ATTRIBUTE write Excel ********************/

	public void setExcel(String excelName, int sheetNumber, double[][] vector, int newInstancesLength) {

		int i = 0, j = 0;
		try {
			// excelName.xls为要新建的文件名
			WritableWorkbook book1 = Workbook.createWorkbook(new File(FilePath + excelName + ".xls"));

			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet1 = book1.createSheet(Integer.toString(sheetNumber), sheetNumber);

			// write title
			for (i = 0; i < columnsNumber; i++) {
				sheet1.addCell(new Label(i, 0, attributeName[i]));
			}

			// 写入内容

			jxl.write.NumberFormat nf = new jxl.write.NumberFormat("0.########################");
			jxl.write.WritableCellFormat wcf = new jxl.write.WritableCellFormat(nf);
			for (j = 0; j < columnsNumber; j++)// column
			{
				i = 1;
				while (i <= newInstancesLength) {

					/* if (0.0 == vector[0][i]) break; */

					// jxl.write.NumberFormat nf = new jxl.write.NumberFormat("0.########################");
					// jxl.write.WritableCellFormat wcf = new jxl.write.WritableCellFormat(nf);
					jxl.write.Number n = new jxl.write.Number(j, i, vector[j][i], wcf);

					sheet1.addCell(n);

					// sheet1.addCell(new Label(j, i,
					// Double.toString(vector[j][i])));
					i++;
				}
				// for (i = 1; i <= rowsNumber; i++) {// row
				// sheet1.addCell(new Label(j, i,
				// Double.toString(vector[j][i])));
				// }
			}
			// System.out.println(i);
			// 写入数据
			book1.write();

			// 关闭文件
			book1.close();

			System.out.println("Seccessiful write to " + excelName + ".xls.");
		} catch (Exception e) {
			System.out.println("Failed write to " + excelName + ".xls!!!!!!!!");
		}
	}

	/* *************CHAR ATTRIBUTE write Excel ********************/

	public void setExcel(String excelName, int sheetNumber, char[][] timeSeriesCharacter, int newInstancesLength) {

		int i = 0, j = 0;
		try {
			// excelName.xls为要新建的文件名
			WritableWorkbook book1 = Workbook.createWorkbook(new File(FilePath + excelName + ".xls"));

			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet1 = book1.createSheet(Integer.toString(sheetNumber), sheetNumber);

			// write title
			for (i = 0; i < columnsNumber; i++) {
				sheet1.addCell(new Label(i, 0, attributeName[i]));
			}

			// 写入内容
			for (j = 0; j < columnsNumber; j++)// column
			{
				i = 1;
				while (i <= newInstancesLength) {
					sheet1.addCell(new Label(j, i, Character.toString(timeSeriesCharacter[j][i])));
					i++;
				}
				// for (i = 1; i <= rowsNumber; i++) {// row
				// sheet1.addCell(new Label(j, i,
				// Double.toString(vector[j][i])));
				// }
			}
			// System.out.println(i);
			// 写入数据
			book1.write();

			// 关闭文件
			book1.close();

			System.out.println("Seccessiful write to " + excelName + ".xls.");
		} catch (Exception e) {
			System.out.println("Failed write to " + excelName + ".xls!!!!!!!!");
		}
	}
}
