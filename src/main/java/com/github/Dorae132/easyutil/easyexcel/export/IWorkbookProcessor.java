package com.github.Dorae132.easyutil.easyexcel.export;

import org.apache.poi.ss.usermodel.Workbook;

import com.github.Dorae132.easyutil.easyexcel.ExcelProperties;

/**
 * process the file that has been created
 * @author Dorae
 *
 * @param <T>
 * @param <F>
 */
@FunctionalInterface
public interface IWorkbookProcessor <R> {
	R process(Workbook wb, ExcelProperties<?, R> properties) throws Exception;
}
