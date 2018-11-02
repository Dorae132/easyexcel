package com.github.Dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.Record;

import com.github.Dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * The handler for string cel.
 * @author Dorae
 *
 */
public class StringRecordHandler extends Abstract03RecordHandler {

	public StringRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}

	@Override
	public boolean couldDecode(Record record) {
		return LabelSSTRecord.sid == record.getSid();
	}

	@Override
	public void decode(Record record) {
		LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
		int currColNum = labelSSTRecord.getColumn();
		handlerContext.addCol2CurrRowList(handlerContext.getSSTRecord().getString(labelSSTRecord.getSSTIndex()).toString());
		handlerContext.setCurrColNum(currColNum);
	}

}
