package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.IRecordHandlerContext;

/**
 * the handler that process the record for the xls file
 * @author Dorae
 *
 * @param <T> The target type of the cell.
 */
public abstract class AbstractRecordHandler {

	public AbstractRecordHandler next;
	
	public AbstractRecordHandler setNext(AbstractRecordHandler next) {
		this.next = next;
		return next;
	}
	
	/**
	 * handle
	 * @param handlerContext
	 * @param record
	 */
	public void handle(IRecordHandlerContext handlerContext, Record record) throws Exception {
		if (this.couldDecode(handlerContext, record)) {
			this.decode(handlerContext, record);
		} else if (next != null) {
			next.handle(handlerContext, record);
		} else {
			// just do nothing
			return;
		}
	}
	
	/**
	 * could decode or not
	 * @param handlerContext
	 * @param record
	 * @return
	 */
	public abstract boolean couldDecode(IRecordHandlerContext handlerContext, Record record);
	
	/**
	 * decode
	 * @param handlerContext
	 * @param record
	 */
	public abstract void decode(IRecordHandlerContext handlerContext, Record record) throws Exception;
}
