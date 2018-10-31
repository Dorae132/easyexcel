package com.dorae132.easyutil.easyexcel.read.event.recordhandler;

import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.Record;

/**
 * for blank cell
 * @author Dorae
 *
 */
public class BlankRecordHandler extends AbstractRecordHandler {

	private final static String BLANK = "";
	
	@Override
	protected boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return BlankRecord.sid == record.getSid();
	}

	@Override
	protected void decode(IRecordHandlerContext handlerContext, Record record) {
		BlankRecord blankRecord = (BlankRecord) record;
		int currColNum = blankRecord.getColumn();
		handlerContext.addCol2CurrRowList(BLANK);
		handlerContext.setCurrColNum(currColNum);
	}
}
