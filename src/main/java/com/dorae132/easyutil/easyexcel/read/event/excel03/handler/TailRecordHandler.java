package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * The tail handler, just in case there are no handler to be useed to resolve the col value.
 * @author Dorae
 *
 */
public class TailRecordHandler extends Abstract03RecordHandler {

	public TailRecordHandler(IRecordHandlerContext handlerContext) {
		super(handlerContext);
	}

	@Override
	public boolean couldDecode(Record record) {
		return true;
	}

	@Override
	public void decode(Record record) throws Exception {
		// just discard the record
		
	}

}
