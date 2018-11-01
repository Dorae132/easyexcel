package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * record the end of the file/sheet
 * @author Dorae
 *
 */
public class EofRecordHandler extends Abstract03RecordHandler {

	public EofRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}

	@Override
	public boolean couldDecode(Record record) {
		return EOFRecord.sid == record.getSid();
	}

	@Override
	public void decode(Record record) throws Exception {
		handlerContext.decreaseSheetNumbers();
	}

}
