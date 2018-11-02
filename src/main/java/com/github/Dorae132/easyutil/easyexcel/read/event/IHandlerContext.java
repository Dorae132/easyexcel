package com.github.Dorae132.easyutil.easyexcel.read.event;

import java.util.List;

/**
 * handlerContext顶层接口
 * 
 * @author Dorae
 *
 * @param <C>
 *            The type that wanted in the cell
 */
public interface IHandlerContext<C> {

	/**
	 * produce a new row
	 * 
	 * @param row
	 * @throws InterruptedException
	 */
	void newRow(List<C> row) throws Exception;

	/**
	 * get a row from the context
	 * 
	 * @return
	 * @throws Exception
	 */
	List<C> getRow() throws Exception;

	/**
	 * Fire this when the file is ending.
	 * 
	 * @return
	 */
	boolean fileEnd();

	/**
	 * the processing is end or not
	 * 
	 * @return
	 */
	boolean isFileEnded();

	/**
	 * process the file
	 */
	void process() throws Exception;
}
