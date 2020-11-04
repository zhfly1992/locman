/*
 * File name: ExportExcelTools.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年7月19日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.filetool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.beanutils.BeanMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.run.common.util.StringUtil;
import com.run.locman.constants.PublicConstants;

/**
 * @Description: 导出excel工具类(.xls)
 * @author: zhaoweizhi
 * @version: 1.0, 2018年7月19日
 */

public class ExportExcelTools {
	/** 每个sheet的条数默认为1000条 */
	public static int		SHEET_SIZE		= 1000000;
	/** 行高、列宽 */
	public static int		COLUMN_WIDTH	= 30;
	public static short		FONT_SIZE		= 15;
	public static String	DATE_PATTERN	= "yyyy-MM-dd hh:mm:ss";
	private static String	DEFAULT_TITLE	= "未命名";
	private static Pattern	P				= Pattern.compile("^[+-]?\\d+(\\.\\d+)?$");



	/**
	 * 
	 * @Description:excel生产
	 * @param title
	 *            表格名
	 * @param columnHeading
	 *            列标题
	 * @param dataSet
	 *            数据集
	 * @return XSSFWorkbook
	 */
	public static <K> XSSFWorkbook excelBuilder(String title, Map<String, Object> columnHeading,
			List<Map<K, Object>> dataSet) throws Exception {
		// 声明工作薄
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFCellStyle titleStyle = getTitleStyle(workbook);
		XSSFCellStyle recordStyle = getRecordStyle(workbook);
		if (StringUtil.isEmpty(title)) {
			title = DEFAULT_TITLE;
		}
		if (null == columnHeading || columnHeading.isEmpty() || null == dataSet) {
			workbook.createSheet(title);
		} else {
			if (dataSet.isEmpty()) {
				XSSFSheet sheet = workbook.createSheet(title);

				// 设置单元格格式
				for (int i = 0; i < columnHeading.size(); i++) {
					sheet.setColumnWidth(i, 100 * 100);
					sheet.setDefaultColumnStyle(i, recordStyle);
				}

				// 生成列头
				columnHeadingGenerator(sheet, columnHeading, titleStyle);
			}
			dataSetPartitionerAndGenerator(workbook, title, columnHeading, dataSet, titleStyle, recordStyle);
		}
		return workbook;
	}



	/**
	 * 
	 * @Description:map数据集 转 bean数据集
	 * @param dataSet
	 *            bean数据集
	 * @return
	 */
	public static List<Map<Object, Object>> beanList2MapList(List<? extends Object> dataSet) {
		if (null == dataSet) {
			return null;
		}
		return dataSet.stream().map(BeanMap::new).collect(Collectors.toList());
	}



	/**
	 * 
	 * @Description:列标题生成器
	 * @param sheet
	 *            当前表格
	 * @param columnHeading
	 *            列标题map
	 * @param titleStyle
	 *            单元格样式
	 */
	private static void columnHeadingGenerator(XSSFSheet sheet, Map<String, Object> columnHeading,
			XSSFCellStyle titleStyle) {
		XSSFRow row = sheet.createRow(0);
		IntStream.range(0, columnHeading.size()).forEach((idx) -> {
			XSSFCell cell = row.createCell(idx);
			cell.setCellStyle(titleStyle);
			cell.setCellValue(new XSSFRichTextString(String.valueOf(columnHeading.values().toArray()[idx])));
		});
	}



	/**
	 * 
	 * @Description:所有行记录生成器
	 * @param sheet
	 *            当前表格
	 * @param columnHeading
	 *            当前行标题map
	 * @param subRowRecord
	 *            当前所有行记录集合
	 * @param recordStyle
	 *            单元格样式
	 */
	private static <K> void rowRecordGenerator(XSSFSheet sheet, Map<String, Object> columnHeading,
			List<Map<K, Object>> subRowRecord, XSSFCellStyle recordStyle, XSSFWorkbook workbook) {
		IntStream.range(0, subRowRecord.size()).forEach((idx) -> {
			recordGenerator(sheet.createRow(idx + 1), columnHeading, subRowRecord.get(idx), recordStyle, workbook);
		});
	}



	/**
	 * 
	 * @Description:当前行记录生成器
	 * @param xssfRow
	 *            当前行
	 * @param columnHeading
	 *            当前行标题map
	 * @param record
	 *            当前行记录map
	 * @param recordStyle
	 *            单元格样式
	 */
	private static <K> void recordGenerator(XSSFRow xssfRow, Map<String, Object> columnHeading, Map<?, Object> record,
			XSSFCellStyle recordStyle, XSSFWorkbook workbook) {
		IntStream.range(0, columnHeading.size()).forEach((idx) -> {
			XSSFCell cell = xssfRow.createCell(idx);
			cell.setCellStyle(recordStyle);
			setFormatCell(cell, record.getOrDefault(columnHeading.keySet().toArray()[idx], ""), workbook,
					record.get(PublicConstants.IDNEX));
		});
	}



	/**
	 * 
	 * @Description:格式化单元格值
	 * @param cell
	 *            当前单元格
	 * @param object
	 *            值
	 */
	private static void setFormatCell(XSSFCell cell, Object object, XSSFWorkbook workbook, Object index) {
		int columnIndex = cell.getColumnIndex();
		if (index != null && index.equals(columnIndex)) {
			XSSFCellStyle style = getRecordStyle(workbook);
			style.setFillForegroundColor(IndexedColors.RED.getIndex());
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			cell.setCellStyle(style);
		}

		if (null == object) {
			cell.setCellValue("");
		} else if (object instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
			cell.setCellValue(sdf.format(object));
		} else {
			// 其余全部当做字符处理
			String value = String.valueOf(object);
			// Pattern p = Pattern.compile("^[+-]?\\d+(\\.\\d+)?$");
			Matcher matcher = P.matcher(value);
			if (matcher.matches()) {
				// 如果值是数字，设置为String类型
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(value);
			} else {
				cell.setCellValue(value);
			}
		}

	}



	/**
	 * 
	 * @Description:数据集分割和生成
	 * @param workbook
	 *            当前工作薄
	 * @param title
	 *            表格标题
	 * @param columnHeading
	 *            行标题map
	 * @param dataSet
	 *            数据集合
	 * @param titleStyle
	 * @param recordStyle
	 */
	private static <K> void dataSetPartitionerAndGenerator(XSSFWorkbook workbook, String title,
			Map<String, Object> columnHeading, List<Map<K, Object>> dataSet, XSSFCellStyle titleStyle,
			XSSFCellStyle recordStyle) {
		// 判断需要几个sheet,默认1个
		int num = 1;
		int size = dataSet.size();
		if (size % SHEET_SIZE == 0) {
			num = size / SHEET_SIZE;
		} else {
			num = size / SHEET_SIZE + 1;
		}
		IntStream.range(0, num).forEach((idx) -> {
			int fromIndex = SHEET_SIZE * idx;
			int toIndex = size;
			if (SHEET_SIZE * (idx + 1) > size) {
				toIndex = size;
			} else {
				toIndex = SHEET_SIZE * (idx + 1);
			}
			List<Map<K, Object>> subRowRecord = dataSet.subList(fromIndex, toIndex);
			XSSFSheet sheet = workbook.createSheet(String.format("%s(%d)", title, idx + 1));
			sheet.setDefaultColumnWidth(COLUMN_WIDTH);
			// 生成列头
			columnHeadingGenerator(sheet, columnHeading, titleStyle);
			// 生成记录
			rowRecordGenerator(sheet, columnHeading, subRowRecord, recordStyle, workbook);
		});
	}



	/**
	 * 
	 * @Description:获取标题样式
	 * @param workbook
	 *            工作薄
	 * @return
	 */
	private static XSSFCellStyle getTitleStyle(XSSFWorkbook workbook) {
		// 设置标题样式
		XSSFCellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
		titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		XSSFDataFormat format = workbook.createDataFormat();
		titleStyle.setDataFormat(format.getFormat("@"));
		// 生成标题字体
		XSSFFont titleFont = workbook.createFont();
		titleFont.setColor(HSSFColor.BLACK.index);
		titleFont.setFontHeightInPoints(FONT_SIZE);
		titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		titleFont.setFontName("黑体");
		// 把标题字体应用到标题样式
		titleStyle.setFont(titleFont);
		return titleStyle;
	}



	/**
	 * 
	 * @Description:获取记录样式
	 * @param workbook
	 *            工作薄
	 * @return
	 */
	private static XSSFCellStyle getRecordStyle(XSSFWorkbook workbook) {
		// 生成单元格样式
		XSSFCellStyle recordStyle = workbook.createCellStyle();
		recordStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		recordStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		recordStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		recordStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		recordStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		recordStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		recordStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		recordStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		XSSFDataFormat format = workbook.createDataFormat();
		recordStyle.setDataFormat(format.getFormat("@"));
		// 生成单元格字体
		XSSFFont recordFont = workbook.createFont();
		recordFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		recordFont.setColor(HSSFColor.BLACK.index);
		recordFont.setFontName("黑体");
		// 把单元格字体应用到单元格样式
		recordStyle.setFont(recordFont);
		return recordStyle;
	}
}
