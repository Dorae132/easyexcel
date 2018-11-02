package com.github.Dorae132.easyutil.easyexcel.read;

/**
 * this while be called when there are no more rows
 * @author Dorae
 * @param <R>
 */
public interface IReadDoneCallBack<R> {

	R call();
}
