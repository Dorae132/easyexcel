package com.dorae132.easyutil.easyexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dorae132.easyutil.easyexcel.export.IFillSheet;
import com.dorae132.easyutil.easyexcel.read.ConsumeRowThread;
import com.dorae132.easyutil.easyexcel.read.ExcelVersionEnums;
import com.dorae132.easyutil.easyexcel.read.IReadDoneCallBack;
import com.dorae132.easyutil.easyexcel.read.IRowConsumer;
import com.dorae132.easyutil.easyexcel.read.event.IHandlerContext;

/**
 * Excel utils
 * 
 * @author Dorae
 *
 */
public class ExcelUtils {

	private static ThreadPoolExecutor THREADPOOL = new ThreadPoolExecutor(0, 10, 1, TimeUnit.SECONDS,
			new SynchronousQueue<Runnable>());

	/**
	 * support the append strategy, but not rocommend, please focus on
	 * excelExportApend()
	 * 
	 * @param properties
	 * @param iFillSheet
	 * @return
	 * @throws Exception
	 */
	public static Object excelExport(ExcelProperties properties, IFillSheet iFillSheet) throws Exception {
		// 1.创建目录
		validateFileDir(properties.getFilePath());
		File file = new File(new StringBuilder(properties.getFilePath()).append(properties.getFileName()).toString());
		if (!file.exists()) {
			try (FileOutputStream tmpOutPutStream = new FileOutputStream(file)) {
				XSSFWorkbook tmpWorkBook = new XSSFWorkbook();
				tmpWorkBook.write(tmpOutPutStream);
			} catch (Exception e) {
				throw e;
			}
		}
		// 2.写入文件
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
			SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, properties.getRowAccessWindowsize());
			Sheet sheet = sxssfWorkbook.getSheet(properties.getSheetName());
			if (sheet == null) {
				sheet = sxssfWorkbook.createSheet(properties.getSheetName());
			}
			iFillSheet.fill(properties, sheet);
			outputStream = new FileOutputStream(file);
			sxssfWorkbook.write(outputStream);
		} catch (Exception e) {
			throw e;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		// 3.处理结果
		if (properties.getProcessor() != null) {
			return properties.getProcessor().process(file);
		} else {
			return file;
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
			int threadCount, boolean syncCurrentThread) throws Exception {
		// synchronized main thread
		CyclicBarrier cyclicBarrier = null;
		threadCount = syncCurrentThread ? ++threadCount : threadCount;
		if (callBack != null) {
			cyclicBarrier = new CyclicBarrier(threadCount, () -> {
				callBack.call();
			});
		} else {
			cyclicBarrier = new CyclicBarrier(threadCount);
		}
		IHandlerContext context = ExcelVersionEnums.produceContext(properties);
		for (int i = 0; i < threadCount; i++) {
			THREADPOOL.execute(new ConsumeRowThread(context, rowConsumer, cyclicBarrier));
		}
		context.process();
		if (syncCurrentThread) {
			cyclicBarrier.await();
		}
	}

	/**
	 * 校验目录是否存在
	 * 
	 * @param filePath
	 */
	private static void validateFileDir(String filePath) {
		File tempDir = new File(filePath);
		if (!tempDir.exists() && !tempDir.isDirectory()) {
			tempDir.mkdir();
		}
	}
}
