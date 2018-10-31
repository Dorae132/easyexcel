package com.dorae132.easyutil.easyexcel.read.event.recordhandler;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

/**
 * the handler for constants
 * excel中的常量表
 * @author Dorae
 *
 */
public class SSTRecordHandler extends AbstractRecordHandler {

	@Override
	protected boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return SSTRecord.sid == record.getSid();
	}

	@Override
	protected void decode(IRecordHandlerContext handlerContext, Record record) {
		SSTRecord sstRecord = (SSTRecord) record;
		handlerContext.setSSTRecord(sstRecord);
	}

}
