package com.github.Dorae132.easyutil.easyexcel.export;

import java.util.List;

import com.github.Dorae132.easyutil.easyexcel.common.Pair;

/**
 * for the append strategy
 * @author Dorae
 *
 * @param <T>
 */
public interface IDataSupplier<T> {
	
	public Pair<List<T>, Boolean> getDatas();

}
