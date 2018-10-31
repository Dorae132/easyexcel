package com.dorae132.easyutil.easyexcel.read;

/**
 * this while be called when there are no more rows
 * @author Dorae
 *
 */
public interface IReadDoneCallBack<R> {

	R call();
}
