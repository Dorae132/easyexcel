package com.github.Dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.Record;

import com.github.Dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * for blank cell
 * @author Dorae
 *
 */
public class BlankRecordHandler extends Abstract03RecordHandler {

	private final static String BLANK = "";
	
	public BlankRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}
	
	@Override
	public boolean couldDecode(Record record) {
		return BlankRecord.sid == record.getSid();
	}

	@Override
	public void decode(Record record) {
		BlankRecord blankRecord = (BlankRecord) record;
		int currColNum = blankRecord.getColumn();
		handlerContext.addCol2CurrRowList(BLANK);
		handlerContext.setCurrColNum(currColNum);
	}
}
