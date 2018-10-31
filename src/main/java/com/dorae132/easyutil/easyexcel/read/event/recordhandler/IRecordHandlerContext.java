package com.dorae132.easyutil.easyexcel.read.event.recordhandler;

import java.util.List;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

/**
 * The interface for handlerContext
 * @author Dorae
 * @param <T> The target type for the cell value
 */
public interface IRecordHandlerContext<T> {

	void handle(Record record) throws Exception;
	/**
	 * Enable the capacity that could insert the custom handler
	 * @param recordHandler
	 */
	void registRecordHandler(AbstractRecordHandler recordHandler);
	
	/**
	 * Set the current col num.
	 * @param currColNum
	 */
	void setCurrColNum(int currColNum);
	
	/**
	 * Get the current col num.
	 * @return
	 */
	int getCurrColNum();
	
	/**
	 * Get the constants table
	 * @return
	 */
	SSTRecord getSSTRecord();
	
	/**
	 * Init the current row list, let it be empty.
	 * @param colNum
	 */
	void initCurrRowList(int colNum);
	
	/**
	 * Get the current row list
	 * @return
	 */
	List<T> getCurrRowList();
	
	/**
	 * Add a value of the col to the current row list.
	 * @param t
	 */
	void addCol2CurrRowList(T colValue);
	
	/**
	 * Set the constants table
	 * @param sstRecord
	 */
	void setSSTRecord(SSTRecord sstRecord);
	
	/**
	 * Produce a new row that we need.
	 * @param row
	 */
	void newRow(List<T> row) throws Exception;
}
