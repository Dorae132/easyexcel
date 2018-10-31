package com.dorae132.easyutil.easyexcel.read.event.recordhandler;

import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.Record;

/**
 * The handler for string cel.
 * @author Dorae
 *
 */
public class StringRecordHandler extends AbstractRecordHandler {

	@Override
	protected boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return LabelSSTRecord.sid == record.getSid();
	}

	@Override
	protected void decode(IRecordHandlerContext handlerContext, Record record) {
		LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
		int currColNum = labelSSTRecord.getColumn();
		handlerContext.addCol2CurrRowList(handlerContext.getSSTRecord().getString(labelSSTRecord.getSSTIndex()).toString());
		handlerContext.setCurrColNum(currColNum);
	}

}
