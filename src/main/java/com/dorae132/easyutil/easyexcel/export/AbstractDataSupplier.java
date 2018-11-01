package com.dorae132.easyutil.easyexcel.export;

import java.util.List;

import com.dorae132.easyutil.easyexcel.common.Pair;

/**
 * for the append strategy
 * @author Dorae
 *
 * @param <T>
 */
public abstract class AbstractDataSupplier<T> {
	
	public abstract Pair<List<T>, Boolean> getDatas();

}
