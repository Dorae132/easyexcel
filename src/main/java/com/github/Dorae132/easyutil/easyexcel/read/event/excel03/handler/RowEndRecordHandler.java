package com.github.Dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.record.Record;

import com.github.Dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * the row end handler
 * @author Dorae
 *
 */
public class RowEndRecordHandler extends Abstract03RecordHandler {

	@Override
	public boolean couldDecode(Record record) {
		return record instanceof LastCellOfRowDummyRecord;
	}

	public RowEndRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}

	@Override
	public void decode(Record record) throws Exception {
		// 产生新行，结束当前行
		if (handlerContext.getCurrColNum() != 0) {
			handlerContext.newRow(handlerContext.getCurrRowList());
			handlerContext.setCurrColNum(0);
		}
		handlerContext.initCurrRowList(0);
	}
}
