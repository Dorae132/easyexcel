package com.github.Dorae132.easyutil.easyexcel.export;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;

import com.github.Dorae132.easyutil.easyexcel.ExcelProperties;

/**
 * 填充sheet
 * @author Dorae
 *
 */
public interface IFillSheet {

	void fill(ExcelProperties excelProperties, Sheet sheet) throws Exception;

	/**
	 * 填充内容
	 * 
	 * @param dataList
	 * @param titleToFieldObjs
	 * @param sheet
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	default void fillContentRow(ExcelProperties<?, ?> excelProperties, Sheet sheet) throws Exception {
		if (CollectionUtils.isEmpty(excelProperties.getDataList())) {
			return;
		}
		List<Field> fields = excelProperties.getFields();
		Map<String, Field> fieldNameMap = excelProperties.getFieldNameMap();
		int row = 1 + excelProperties.getRowOffset();
		for (Object object : excelProperties.getDataList()) {
			int col = 0;
			Row createRow = sheet.createRow(row);
			for (Field field : fields) {
				field.setAccessible(true);
				Object value = field.get(object);
				CellUtil.createCell(createRow, col, value == null ? "" : value.toString());
				col++;
			}
			row++;
		}
	}

	/**
	 * 创建表头行（第0行创建）
	 *
	 * @param titleMap
	 *            对象属性名称->表头显示名称
	 */
	default void createHeadRow(ExcelProperties properties, Sheet sheet) {
		List<String> titles = properties.getTitles();
		// 偏移不为0
		if (properties.getRowOffset() != 0) {
			return;
		}
		Row headRow = sheet.createRow(0);
		int i = 0;
		for (String title : titles) {
			headRow.createCell(i++).setCellValue(title);
		}
	}
}
