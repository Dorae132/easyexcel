package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.IRecordHandlerContext;

/**
 * The handler for string cel.
 * @author Dorae
 *
 */
public class StringRecordHandler extends AbstractRecordHandler {

	@Override
	public boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return LabelSSTRecord.sid == record.getSid();
	}

	@Override
	public void decode(IRecordHandlerContext handlerContext, Record record) {
		LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
		int currColNum = labelSSTRecord.getColumn();
		handlerContext.addCol2CurrRowList(handlerContext.getSSTRecord().getString(labelSSTRecord.getSSTIndex()).toString());
		handlerContext.setCurrColNum(currColNum);
	}

}
