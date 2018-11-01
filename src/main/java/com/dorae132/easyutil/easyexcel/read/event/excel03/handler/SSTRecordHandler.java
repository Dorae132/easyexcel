package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

import com.dorae132.easyutil.easyexcel.read.event.IRecordHandlerContext;

/**
 * the handler for constants
 * excel中的常量表
 * @author Dorae
 *
 */
public class SSTRecordHandler extends AbstractRecordHandler {

	@Override
	public boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return SSTRecord.sid == record.getSid();
	}

	@Override
	public void decode(IRecordHandlerContext handlerContext, Record record) {
		SSTRecord sstRecord = (SSTRecord) record;
		handlerContext.setSSTRecord(sstRecord);
	}

}
