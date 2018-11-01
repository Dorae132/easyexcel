package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * the bound of the workbook
 * @author Dorae
 *
 */
public class BoundSheetRecordHandler extends Abstract03RecordHandler {

	public BoundSheetRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}

	@Override
	public boolean couldDecode(Record record) {
		return BoundSheetRecord.sid == record.getSid();
	}

	@Override
	public void decode(Record record) {
		handlerContext.increaseSheetNumbers();
	}

}
