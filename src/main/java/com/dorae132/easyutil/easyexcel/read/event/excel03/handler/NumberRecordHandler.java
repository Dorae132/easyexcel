package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import java.text.DecimalFormat;

import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.IRecordHandlerContext;

/**
 * The handler for number cell
 * @author Dorae
 *
 */
public class NumberRecordHandler extends AbstractRecordHandler {

	private static final DecimalFormat DF = new DecimalFormat("0.00"); 
	
	@Override
	public boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return NumberRecord.sid == record.getSid();
	}

	@Override
	public void decode(IRecordHandlerContext handlerContext, Record record) {
		NumberRecord numberRecord = (NumberRecord) record;
		int currColNum = numberRecord.getColumn();
		handlerContext.addCol2CurrRowList(DF.format(numberRecord.getValue()));
		handlerContext.setCurrColNum(currColNum);
	}

}
