package com.dorae132.easyutil.easyexcel.read;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * the interface to get sheet
 * @author Dorae
 *
 */
public interface ISheetSupplier {
	default Sheet getSheet(Workbook workbook) {
		// Temporarily do not consider supporting multiple sheets
		return workbook.getSheetAt(0);
	}
}
