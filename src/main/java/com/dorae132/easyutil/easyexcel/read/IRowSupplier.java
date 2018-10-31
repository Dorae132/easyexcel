package com.dorae132.easyutil.easyexcel.read;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.dorae132.easyutil.easyexcel.Pair;

/**
 * the interface to get rows from the given sheet
 * @author Dorae
 *
 */
public interface IRowSupplier {

	/**
	 * @param sheet The target sheet
	 * @param lastRowIndex The last index row that has been readed, default -1
	 * @return
	 */
	default Pair<Boolean, Row> getRow(Sheet sheet, int lastRowIndex) {
		Row row = sheet.getRow(++lastRowIndex);
		return Pair.of(row != null, row);
	}
}
