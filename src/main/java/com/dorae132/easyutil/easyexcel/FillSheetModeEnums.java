package com.dorae132.easyutil.easyexcel;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * 写入模式枚举
 * 
 * @author Dorae
 *
 */
public enum FillSheetModeEnums {

	COMMON_MODE(new IFillSheet() {

		@Override
		public void fill(ExcelProperties excelProperties, Sheet sheet) throws Exception {
			createHeadRow(excelProperties, sheet);
			fillContentRow(excelProperties, sheet);
		}
		
	}),
	APPEND_MODE(new IFillSheet() {

		@Override
		public void fill(ExcelProperties properties, Sheet sheet) throws Exception {
			int rowOffset = 0;
			Pair datasPair = null;
			List dataList = null;
			boolean hashNext = false;
			while (true) {
				AbstractDataSupplier dataSupplier = properties.getDataSupplier();
				if (dataSupplier == null) {
					break;
				}
				datasPair = dataSupplier.getDatas();
				dataList = (List<?>) datasPair.getFirst();
				boolean hasNext = (boolean) datasPair.getSecond();
				if (hasNext) {
					properties.setDataList(dataList);
					properties.setRowOffset(rowOffset);
					createHeadRow(properties, sheet);
					fillContentRow(properties, sheet);
					rowOffset += dataList.size();
				} else {
					break;
				}
			}
		}
		
	});
	
	private IFillSheet iFillSheet;

	private FillSheetModeEnums(IFillSheet iFillSheet) {
		this.iFillSheet = iFillSheet;
	}
	
	public IFillSheet getValue() {
		return this.iFillSheet;
	}
}
