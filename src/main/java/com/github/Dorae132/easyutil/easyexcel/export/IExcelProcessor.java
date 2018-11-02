package com.github.Dorae132.easyutil.easyexcel.export;

/**
 * process the file that has been created
 * @author Dorae
 *
 * @param <T>
 * @param <F>
 */
@FunctionalInterface
public interface IExcelProcessor <T, F> {
	public T process(F f);
}
