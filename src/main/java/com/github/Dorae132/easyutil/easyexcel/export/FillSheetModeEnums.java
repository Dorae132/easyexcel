package com.github.Dorae132.easyutil.easyexcel.export;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;

import com.github.Dorae132.easyutil.easyexcel.ExcelProperties;
import com.github.Dorae132.easyutil.easyexcel.common.EasyExcelException;
import com.github.Dorae132.easyutil.easyexcel.common.Pair;

/**
 * 写入模式枚举
 * 
 * @author Dorae
 *
 */
public enum FillSheetModeEnums {

	/**
	 * 普通模式
	 */
	COMMON_MODE(new IFillSheet() {

		@Override
		public void fill(ExcelProperties excelProperties, Sheet sheet) throws Exception {
			createHeadRow(excelProperties, sheet);
			fillContentRow(excelProperties, sheet);
		}
		
	}),
	/**
	 * 追加模式
	 */
	APPEND_MODE(new IFillSheet() {
		@Override
		public void fill(ExcelProperties properties, Sheet sheet) throws Exception {
			int rowOffset = 0;
			Pair datasPair = null;
			List dataList = null;
			boolean hasNext = false;
			IDataSupplier dataSupplier = properties.getDataSupplier();
			while (dataSupplier != null) {
				datasPair = dataSupplier.getDatas();
				dataList = (List<?>) datasPair.getFirst();
				hasNext = (boolean) datasPair.getSecond();
				if (CollectionUtils.isNotEmpty(dataList)) {
					properties.setDataList(dataList);
					properties.setRowOffset(rowOffset);
					createHeadRow(properties, sheet);
					fillContentRow(properties, sheet);
					rowOffset += dataList.size();
				} else {
					break;
				}
				if (!hasNext) {
					break;
				}
			}
		}
		
	}),
	/**
	 * 并行追加
	 */
	PARALLEL_APPEND_MODE(new IFillSheet() {
		ExecutorService executorService = new ThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(1024));
		@Override
		public void fill(ExcelProperties properties, Sheet sheet) throws Exception {
			int rowOffset = 0;
			IDataSupplier dataSupplier = properties.getDataSupplier();
			LinkedBlockingQueue dataPairQueue = new LinkedBlockingQueue<>();
			// more data or not
			AtomicBoolean moreData = new AtomicBoolean(true);
			// the get data thread
			Runnable getDataThread = new Runnable() {
				@Override
				public void run() {
					try {
						while (dataSupplier != null) {
							Pair pair = dataSupplier.getDatas();
							List dataList = (List) pair.getFirst();
							boolean hasNext = (boolean) pair.getSecond();
							if (CollectionUtils.isNotEmpty(dataList)) {
								dataPairQueue.put(dataList);
							} else {
								break;
							}
							if (!hasNext) {
								break;
							}
						}
					} catch (Exception e) {
						// just return
					     throw new EasyExcelException(e);
					}
					// the thread that be used to get data return
					moreData.set(false);
				}
			};
			// start
			if (properties.getWriteThreadPool() != null) {
                properties.getWriteThreadPool().execute(getDataThread);
            } else {
                executorService.execute(getDataThread);
            }
			// main thread fill the excel
			while (true) {
				List dataList = (List) dataPairQueue.poll();
				if (CollectionUtils.isEmpty(dataList)) {
					if (moreData.get()) {
						// there are more data then wait
					    TimeUnit.MILLISECONDS.sleep(100);
						continue;
					} else {
						// no more data then return
						break;
					}
				}
				properties.setDataList(dataList);
				properties.setRowOffset(rowOffset);
				createHeadRow(properties, sheet);
				fillContentRow(properties, sheet);
				rowOffset += dataList.size();
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
