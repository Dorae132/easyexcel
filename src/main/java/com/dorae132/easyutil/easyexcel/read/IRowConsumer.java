package com.dorae132.easyutil.easyexcel.read;

import org.apache.poi.ss.usermodel.Row;

/**
 * The consumer of the row
 * @author Dorae
 *
 */
public interface IRowConsumer {
	void consume(Row row);
}
