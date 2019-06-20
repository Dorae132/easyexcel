package com.github.Dorae132.easyutil.easyexcel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.Dorae132.easyutil.easyexcel.common.EasyExcelException;
import com.github.Dorae132.easyutil.easyexcel.common.Pair;
import com.github.Dorae132.easyutil.easyexcel.export.IFillSheet;
import com.github.Dorae132.easyutil.easyexcel.read.ConsumeRowThread;
import com.github.Dorae132.easyutil.easyexcel.read.DefaultReadDoneCallBackProcessor;
import com.github.Dorae132.easyutil.easyexcel.read.ExcelVersionEnums;
import com.github.Dorae132.easyutil.easyexcel.read.IReadDoneCallBack;
import com.github.Dorae132.easyutil.easyexcel.read.IReadDoneCallBackProcessor;
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
    private static final ThreadPoolExecutor READ_THREAD_POOL = new ThreadPoolExecutor(0, 2, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1024));
    
	/**
	 * support the append strategy, but not rocommend, please focus on
	 * FillSheetModeEnums.APPEND_MODE
	 * 
	 * @param properties
	 * @param iFillSheet
	 * @return
	 * @throws Exception
	 */
	public static <T, R> R excelExport(ExcelProperties<T, R> properties, IFillSheet iFillSheet) throws Exception {
		// 2.写入文件
		SXSSFWorkbook sxssfWorkbook = null;
		XSSFWorkbook xssfWorkbook = null;
		try {
			xssfWorkbook = new XSSFWorkbook();;
			sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, properties.getRowAccessWindowsize());
			Sheet sheet = sxssfWorkbook.getSheet(properties.getSheetName());
			if (sheet == null) {
				sheet = sxssfWorkbook.createSheet(properties.getSheetName());
			}
			iFillSheet.fill(properties, sheet);
    		if (properties.getWbProcessor() != null) {
    		    return properties.getWbProcessor().process(sxssfWorkbook, properties);
    		} else {
    		    return (R)sxssfWorkbook;
    		}
		} catch (Exception e) {
            throw new EasyExcelException(e);
        } finally {
            if (properties.getWbProcessor() != null) {
                if (xssfWorkbook != null) {
                    xssfWorkbook.close();
                }
                if (sxssfWorkbook != null) {
                    sxssfWorkbook.close();
                }
            }
        }
	}

	/**
	 * read util, enable multi sheet<br>
	 * when the syncCurrentThread is false and the callBack is not null, not recomand 
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
			int threadCount, boolean syncCurrentThread) throws Exception {
	    IHandlerContext context = ExcelVersionEnums.produceContext(properties);
	    excelRead(properties, context, rowConsumer, callBack, threadCount, syncCurrentThread);
	}
	
	/**
	 * You can expand the context use this <br>
	 * when the syncCurrentThread is false and the callBack is not null, not recomand
	 * @param context
	 * @param rowConsumer
	 * @param callBack
	 * @param threadCount
	 * @param syncCurrentThread
	 * @throws Exception
	 */
	public static void excelRead(ExcelProperties properties, IHandlerContext context, IRowConsumer rowConsumer, IReadDoneCallBack callBack,
			int threadCount, boolean syncCurrentThread) throws Exception {
	    CountDownLatch latch = new CountDownLatch(threadCount);
        try {
            for (int i = 0; i < threadCount; i++) {
                if (properties.getReadThreadPool() != null) {
                    properties.getReadThreadPool().execute(new ConsumeRowThread<>(context, rowConsumer, latch));
                } else {
                    // default impl
                    READ_THREAD_POOL.execute(new ConsumeRowThread(context, rowConsumer, latch));
                }
            }
            // main thread get the rows
            context.process();
            if (syncCurrentThread) {
                latch.await(properties.getReadThreadWaitTime(), TimeUnit.SECONDS);
                if (callBack != null) {
                    callBack.call();
                }
            } else if (callBack != null) {
                // not recomand, because there will be a new thread for every request.
                IReadDoneCallBackProcessor readDoneCallBackProcessor = properties.getReadDoneCallBackProcessor();
                if (readDoneCallBackProcessor == null) {
                    readDoneCallBackProcessor = new DefaultReadDoneCallBackProcessor();
                }
                readDoneCallBackProcessor.process(Pair.of(latch, callBack), properties);
            }
        } catch (Exception e) {
            throw new EasyExcelException(e);
        } finally {
            context.close();
        }
	}
}
