package com.dorae132.easyutil.easyexcel.read;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dorae132.easyutil.easyexcel.ExcelProperties;

/**
 * the enums of the excel versions
 * 
 * @author Dorae
 *
 */
public enum ExcelVersionEnums {

	V2003("xls"),
	V2007("xlsx");

	private String suffix;

	private ExcelVersionEnums(String suffix) {
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}

	public static Workbook createWorkBook(ExcelProperties properties) throws Exception {
		StringBuilder fileNameSB = new StringBuilder(properties.getFileName());
		String fileNameSufix = fileNameSB.substring(fileNameSB.lastIndexOf(".") + 1, fileNameSB.length());
		String absolutePath = new StringBuilder(properties.getFilePath()).append(properties.getFileName()).toString();
		File file = new File(absolutePath);
		FileInputStream inputStream = new FileInputStream(file);
		if (V2003.getSuffix().equals(fileNameSufix)) {
			return new HSSFWorkbook(inputStream);
		} else if (V2007.getSuffix().equals(fileNameSufix)) {
			return new XSSFWorkbook(inputStream);
		}
		return null;
	};
}
