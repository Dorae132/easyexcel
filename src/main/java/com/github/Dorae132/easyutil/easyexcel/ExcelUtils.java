package com.github.Dorae132.easyutil.easyexcel;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.Dorae132.easyutil.easyexcel.common.EasyExcelException;
import com.github.Dorae132.easyutil.easyexcel.export.IFillSheet;
import com.github.Dorae132.easyutil.easyexcel.read.ConsumeRowThread;
import com.github.Dorae132.easyutil.easyexcel.read.ExcelVersionEnums;
import com.github.Dorae132.easyutil.easyexcel.read.IReadDoneCallBack;
import com.github.Dorae132.easyutil.easyexcel.read.IRowConsumer;
import com.github.Dorae132.easyutil.easyexcel.read.event.IHandlerContext;

/**
 * Excel utils
 * 
 * @author Dorae
 *
 */
public class ExcelUtils {

    // 并行读默认线程池
    private static final ThreadPoolExecutor READ_THREAD_POOL = new ThreadPoolExecutor(1, 2, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1024));
    
	/**
	 * support the append strategy, but not rocommend, please focus on
	 * FillSheetModeEnums.APPEND_MODE
	 * 
	 * @param properties
	 * @param iFillSheet
	 * @return
	 * @throws Exception
	 */
	public static <T, R> R excelExport(ExcelProperties<T, R> properties, IFillSheet iFillSheet) throws EasyExcelException {
		// 2.写入文件
		SXSSFWorkbook sxssfWorkbook = null;
		try {
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
			sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, properties.getRowAccessWindowsize());
			Sheet sheet = sxssfWorkbook.getSheet(properties.getSheetName());
			if (sheet == null) {
				sheet = sxssfWorkbook.createSheet(properties.getSheetName());
			}
			iFillSheet.fill(properties, sheet);
		} catch (Exception e) {
			throw new EasyExcelException(e);
		}
		if (properties.getWbProcessor() != null) {
		    try {
                return properties.getWbProcessor().process(sxssfWorkbook, properties);
            } catch (Exception e) {
                throw new EasyExcelException(e);
            }
		} else {
		    return (R)sxssfWorkbook;
		}
	}

	/**
	 * read util, enable multi sheet
	 * 
	 * @param properties
	 * @param rowConsumer
	 *            the consumer of a rowList
	 * @param callBack
	 *            if null do nothing
	 * @param threadCount
	 *            the number of the consume thread
	 * @param syncCurrentThread
	 *            synchronized the current thread or not
	 * @throws Exception
	 */
	public static void excelRead(ExcelProperties properties, IRowConsumer rowConsumer, IReadDoneCallBack callBack,
			int threadCount, boolean syncCurrentThread) throws EasyExcelException {
		// synchronized main thread
		CyclicBarrier cyclicBarrier = null;
		int barrierCount = syncCurrentThread ? threadCount + 1 : threadCount;
		if (callBack != null) {
			cyclicBarrier = new CyclicBarrier(barrierCount, () -> {
				callBack.call();
			});
		} else {
			cyclicBarrier = new CyclicBarrier(barrierCount);
		}
		try {
    		IHandlerContext context = ExcelVersionEnums.produceContext(properties);
    		for (int i = 0; i < threadCount; i++) {
    		    if (properties.getReadThreadPool() != null) {
                    properties.getReadThreadPool().execute(new ConsumeRowThread<>(context, rowConsumer, cyclicBarrier, properties.getReadThreadWaitTime()));
                } else {
                    // default impl
                    READ_THREAD_POOL.execute(new ConsumeRowThread(context, rowConsumer, cyclicBarrier, properties.getReadThreadWaitTime()));
                }
    		}
    		context.process();
    		if (syncCurrentThread) {
    			cyclicBarrier.await(properties.getReadThreadWaitTime(), TimeUnit.SECONDS);
    		}
		} catch (Exception e) {
		    throw new EasyExcelException(e);
		}
	}
	
	/**
	 * You can expand the context use this
	 * @param context
	 * @param rowConsumer
	 * @param callBack
	 * @param threadCount
	 * @param syncCurrentThread
	 * @throws Exception
	 */
	public static void excelRead(ExcelProperties properties, IHandlerContext context, IRowConsumer rowConsumer, IReadDoneCallBack callBack,
			int threadCount, boolean syncCurrentThread) throws EasyExcelException {
		// synchronized main thread
		CyclicBarrier cyclicBarrier = null;
		int barrierCount = syncCurrentThread ? ++threadCount : threadCount;
		if (callBack != null) {
			cyclicBarrier = new CyclicBarrier(barrierCount, () -> {
				callBack.call();
			});
		} else {
			cyclicBarrier = new CyclicBarrier(barrierCount);
		}
		for (int i = 0; i < threadCount; i++) {
		    if (properties.getReadThreadPool() != null) {
                properties.getReadThreadPool().execute(new ConsumeRowThread<>(context, rowConsumer, cyclicBarrier, properties.getReadThreadWaitTime()));
            } else {
                // default impl
                READ_THREAD_POOL.execute(new ConsumeRowThread(context, rowConsumer, cyclicBarrier, properties.getReadThreadWaitTime()));
            }
		}
		try {
		context.process();
		if (syncCurrentThread) {
			cyclicBarrier.await(properties.getReadThreadWaitTime(), TimeUnit.SECONDS);
		}
		} catch (Exception e) {
		    throw new EasyExcelException(e);
		}
	}
}
