package com.dorae132.easyutil.easyexcel.read.event;

import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.recordhandler.DefaultRecordHandlerContext;
import com.dorae132.easyutil.easyexcel.read.event.recordhandler.IRecordHandlerContext;

/**
 * for xls
 * 
 * @author Dorae
 *
 */
public class XlsListener implements HSSFListener {

	private IRecordHandlerContext context = DefaultRecordHandlerContext.DefaultRecordContextFactory.getContext();
	
	@Override
	public void processRecord(Record record) {
		try {
			context.handle(record);
		} catch (Exception e) {
			// do nothing
		}
	}

}
