package com.dorae132.easyutil.easyexcel.read.event.excel03;

import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.Record;

/**
 * for xls
 * 
 * @author Dorae
 *
 */
public class XlsListener implements HSSFListener {

	private IRecordHandlerContext context = Default03RecordHandlerContext.DefaultRecordContextFactory.getContext();
	
	@Override
	public void processRecord(Record record) {
		try {
			context.handle(record);
		} catch (Exception e) {
			// do nothing
		}
	}

}
