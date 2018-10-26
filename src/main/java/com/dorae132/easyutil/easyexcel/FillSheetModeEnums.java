package com.dorae132.easyutil.easyexcel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Sheet;

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
			AbstractDataSupplier dataSupplier = properties.getDataSupplier();
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
		ExecutorService executorService = new ThreadPoolExecutor(0, 10, 60L, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>());
		@Override
		public void fill(ExcelProperties properties, Sheet sheet) throws Exception {
			int rowOffset = 0;
			AbstractDataSupplier dataSupplier = properties.getDataSupplier();
			LinkedBlockingQueue dataPairQueue = new LinkedBlockingQueue<>(1000);
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
					}
					// the thread that be used to get data return
					moreData.set(false);
				}
			};
			// start
			new Thread(getDataThread).start();
			// main thread fill the excel
			while (true) {
				List dataList = (List) dataPairQueue.poll();
				if (CollectionUtils.isEmpty(dataList)) {
					if (moreData.get()) {
						// there are more data then wait
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
