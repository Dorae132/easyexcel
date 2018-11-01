package com.dorae132.easyutil.easyexcel.read.event;

import java.util.List;

/**
 * handlerContext顶层接口
 * @author Dorae
 *
 * @param <R> The type of the record
 * @param <C> The type that wanted in the cell
 */
public interface IHandlerContext <C> {

	/**
	 * produce a new row
	 * @param row
	 * @throws InterruptedException
	 */
	public void newRow(List<C> row) throws Exception;
}
