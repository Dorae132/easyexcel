package com.dorae132.easyutil.easyexcel.read.event.recordhandler;

import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.record.Record;

/**
 * the row end handler
 * @author Dorae
 *
 */
public class RowEndRecordHandler extends AbstractRecordHandler {

	@Override
	protected boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return record instanceof LastCellOfRowDummyRecord;
	}

	@Override
	protected void decode(IRecordHandlerContext handlerContext, Record record) throws Exception {
		// 产生新行，结束当前行
		if (handlerContext.getCurrColNum() != 0) {
			handlerContext.newRow(handlerContext.getCurrRowList());
			handlerContext.setCurrColNum(0);
		}
		handlerContext.initCurrRowList(0);
	}
}
