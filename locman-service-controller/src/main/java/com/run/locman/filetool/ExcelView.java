/*
 * File name: ExcelView.java
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

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

/**
 * @Description: xls格式的excelView
 * @author: zhaoweizhi
 * @version: 1.0, 2018年7月19日
 */

public class ExcelView extends AbstractXlsView {
	private static String	EXCEL_SUFFIX		= ".xlsx";
	public static String	EXCEL_NAME			= "title";
	public static String	EXCEL_DATASET		= "dataSet";
	public static String	EXCEL_COLUMNHEADING	= "columnHeading";
	public static String	USER_AGENT			= "USER-AGENT";
	public static String	MSIE				= "msie";



	public ExcelView() {
		super.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}



	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String title = String.valueOf(model.getOrDefault(EXCEL_NAME, ""));
			if (isIE(request)) {
				title = URLEncoder.encode(title, "UTF8");
			} else {
				title = new String(title.getBytes("UTF-8"), "ISO-8859-1");
			}
			response.setHeader("content-disposition", "attachment;filename=" + title + EXCEL_SUFFIX);

		} catch (Exception e) {
			logger.error("buildExcelDocument()->exception", e);
		}
	}



	@Override
	@SuppressWarnings("unchecked")
	protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
		try {
			String title = String.valueOf(model.getOrDefault(EXCEL_NAME, ""));
			List<Map<Object, Object>> dataSet = (List<Map<Object, Object>>) model.getOrDefault(EXCEL_DATASET, null);
			Map<String, Object> columnHeading = (Map<String, Object>) model.getOrDefault(EXCEL_COLUMNHEADING, null);
			XSSFWorkbook excelBuilder = ExportExcelTools.excelBuilder(title, columnHeading, dataSet);
			return excelBuilder;
		} catch (Exception e) {
			return new XSSFWorkbook();
		}
	}



	protected static boolean isIE(HttpServletRequest request) {
		if (request.getHeader(USER_AGENT).toLowerCase().indexOf(MSIE) > 0
				|| request.getHeader(USER_AGENT).toLowerCase().indexOf("rv:11.0") > 0
				|| request.getHeader(USER_AGENT).toLowerCase().indexOf("edge") > 0) {
			return true;
		} else {
			return false;
		}
	}

}
