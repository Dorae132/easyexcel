package com.dorae132.easyutil.easyexcel.export;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * for the map of the excel and meta
 * @author Dorae
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExcelCol {

	// 对应列标题
	String title();
	
	int order() default 0;
}
