package com.dorae132.easyutil.easyexcel.read.event.recordhandler;

import org.apache.poi.hssf.record.Record;

/**
 * The tail handler, just in case there are no handler to be useed to resolve the col value.
 * @author Dorae
 *
 */
public class TailRecordHandler extends AbstractRecordHandler {

	@Override
	protected boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
		return true;
	}

	@Override
	protected void decode(IRecordHandlerContext handlerContext, Record record) throws Exception {
		// just discard the record
		
	}

}
