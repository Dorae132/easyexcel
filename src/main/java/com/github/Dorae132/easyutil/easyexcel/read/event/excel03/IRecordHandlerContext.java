package com.github.Dorae132.easyutil.easyexcel.read.event.excel03;

import java.util.List;

import org.apache.poi.hssf.record.SSTRecord;

import com.github.Dorae132.easyutil.easyexcel.read.event.IHandlerContext;
import com.github.Dorae132.easyutil.easyexcel.read.event.excel03.handler.Abstract03RecordHandler;

/**
 * The interface for handlerContext
 * @author Dorae
 * @param <T> The target type for the cell value
 */
public interface IRecordHandlerContext<R, C> extends IHandlerContext<C> {

	/**
	 * handle new record
	 * @param record
	 * @throws Exception
	 */
	public void handle(R record) throws Exception;
	
	/**
	 * Enable the capacity that could insert the custom handler
	 * @param recordHandler
	 */
	void registRecordHandler(Abstract03RecordHandler recordHandler);
	
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
	List<C> getCurrRowList();
	
	/**
	 * Add a value of the col to the current row list.
	 * @param t
	 */
	void addCol2CurrRowList(C colValue);
	
	/**
	 * Set the constants table
	 * @param sstRecord
	 */
	void setSSTRecord(SSTRecord sstRecord);
	
	/**
	 * set the number of the sheet.
	 * this is used to judge whither the file has been ended or not.
	 * @param nums
	 */
	void increaseSheetNumbers();
	
	/**
	 * decrease the number of the sheet
	 */
	void decreaseSheetNumbers();
}
