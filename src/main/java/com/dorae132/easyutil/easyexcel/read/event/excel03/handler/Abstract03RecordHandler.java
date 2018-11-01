package com.dorae132.easyutil.easyexcel.read.event.excel03.handler;

import org.apache.poi.hssf.record.Record;

import com.dorae132.easyutil.easyexcel.read.event.excel03.IRecordHandlerContext;

/**
 * the handler that process the record for the xls file
 * @author Dorae
 *
 * @param <T> The target type of the cell.
 */
public abstract class Abstract03RecordHandler {

	public Abstract03RecordHandler next;
	
	public IRecordHandlerContext handlerContext;
	
	private Abstract03RecordHandler() {
		super();
	}

	public Abstract03RecordHandler(IRecordHandlerContext handlerContext) {
		super();
		this.handlerContext = handlerContext;
	}

	public Abstract03RecordHandler setNext(Abstract03RecordHandler next) {
		this.next = next;
		return next;
	}
	
	/**
	 * handle
	 * @param handlerContext
	 * @param record
	 */
	public void handle(Record record) throws Exception {
		if (this.couldDecode(record)) {
			this.decode(record);
		} else if (next != null) {
			next.handle(record);
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
	public abstract boolean couldDecode(Record record);
	
	/**
	 * decode
	 * @param handlerContext
	 * @param record
	 */
	public abstract void decode(Record record) throws Exception;
}
