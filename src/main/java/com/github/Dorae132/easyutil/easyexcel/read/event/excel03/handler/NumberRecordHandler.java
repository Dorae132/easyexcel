package com.github.Dorae132.easyutil.easyexcel.read.event.excel03.handler;

import java.text.DecimalFormat;

import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;

import com.github.Dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * The handler for number cell
 * @author Dorae
 *
 */
public class NumberRecordHandler extends Abstract03RecordHandler {

	private static final DecimalFormat DF = new DecimalFormat("0.00"); 
	
	public NumberRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}
	
	@Override
	public boolean couldDecode(Record record) {
		return NumberRecord.sid == record.getSid();
	}

	@Override
	public void decode(Record record) {
		NumberRecord numberRecord = (NumberRecord) record;
		int currColNum = numberRecord.getColumn();
		handlerContext.addCol2CurrRowList(DF.format(numberRecord.getValue()));
		handlerContext.setCurrColNum(currColNum);
	}

}
