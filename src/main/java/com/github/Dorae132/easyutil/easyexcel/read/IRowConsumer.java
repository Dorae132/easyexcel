package com.github.Dorae132.easyutil.easyexcel.read;

import java.util.List;

/**
 * The consumer of the row
 * @author Dorae
 * @param <C>
 */
public interface IRowConsumer<C> {
	void consume(List<C> row);
}
