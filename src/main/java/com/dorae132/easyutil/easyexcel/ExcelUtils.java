package com.dorae132.easyutil.easyexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Excel utils
 * 
 * @author Dorae
 *
 */
public class ExcelUtils {

	/**
	 * support the append strategy, but not rocommend, please focus on excelExportApend() 
	 * @param properties
	 * @param iFillSheet
	 * @return
	 * @throws Exception
	 */
	public static Object excelExport(ExcelProperties properties, IFillSheet iFillSheet) throws Exception {
		// 1.创建目录
		validateFileDir(properties.getFilePath());
		File file = new File(new StringBuilder(properties.getFilePath()).append(properties.getFileName()).toString());
		if (!file.exists()) {
			try (FileOutputStream tmpOutPutStream = new FileOutputStream(file)) {
				XSSFWorkbook tmpWorkBook = new XSSFWorkbook();
				tmpWorkBook.write(tmpOutPutStream);
			} catch (Exception e) {
				throw e;
			}
		}
		// 2.写入文件
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
			SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, properties.getRowAccessWindowsize());
			Sheet sheet = sxssfWorkbook.getSheet(properties.getSheetName());
			if (sheet == null) {
				sheet = sxssfWorkbook.createSheet(properties.getSheetName());
			}
			iFillSheet.fill(properties, sheet);
			outputStream = new FileOutputStream(file);
			sxssfWorkbook.write(outputStream);
		} catch (Exception e) {
			throw e;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		// 3.处理结果
		if (properties.getProcessor() != null) {
			return properties.getProcessor().process(file);
		} else {
			return file;
		}
	}

	/**
	 * 校验目录是否存在
	 * 
	 * @param filePath
	 */
	private static void validateFileDir(String filePath) {
		File tempDir = new File(filePath);
		if (!tempDir.exists() && !tempDir.isDirectory()) {
			tempDir.mkdir();
		}
	}
}
