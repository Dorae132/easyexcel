package com.github.Dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

import com.github.Dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * the handler for constants
 * excel中的常量表
 * @author Dorae
 *
 */
public class SSTRecordHandler extends Abstract03RecordHandler {

	public SSTRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}

	@Override
	public boolean couldDecode(Record record) {
		return SSTRecord.sid == record.getSid();
	}

	@Override
	public void decode(Record record) {
		SSTRecord sstRecord = (SSTRecord) record;
		handlerContext.setSSTRecord(sstRecord);
	}

}
