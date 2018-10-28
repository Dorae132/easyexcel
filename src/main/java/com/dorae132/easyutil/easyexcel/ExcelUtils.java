package com.dorae132.easyutil.easyexcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dorae132.easyutil.easyexcel.export.IFillSheet;
import com.dorae132.easyutil.easyexcel.read.ConsumeRowThread;
import com.dorae132.easyutil.easyexcel.read.ExcelVersionEnums;
import com.dorae132.easyutil.easyexcel.read.IReadDoneCallBack;
import com.dorae132.easyutil.easyexcel.read.IRowConsumer;
import com.dorae132.easyutil.easyexcel.read.IRowSupplier;
import com.dorae132.easyutil.easyexcel.read.ISheetSupplier;

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
	 * support the append strategy, but not rocommend, please focus on excelExportApend() 
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
	 * 读取工具类
	 * @param properties
	 * @param sheetSupplier 默认获取第0个sheet
	 * @param rowSupplier 默认从第1个row开始获取
	 * @param rowConsumer 需要实现
	 * @param callBack if null do nothing
	 * @param threadCount 消费row的线程数
	 * @param syncCurrentThread 是否需要同步当前线程
	 * @throws Exception
	 */
	public static void excelRead(ExcelProperties properties, ISheetSupplier sheetSupplier, IRowSupplier rowSupplier,
			IRowConsumer rowConsumer, IReadDoneCallBack callBack, int threadCount, boolean syncCurrentThread) throws Exception {
		Sheet sheet = sheetSupplier.getSheet(ExcelVersionEnums.createWorkBook(properties));
		LinkedBlockingQueue<Row> queue = new LinkedBlockingQueue<>();
		AtomicBoolean moreRow = new AtomicBoolean(true);
		Runnable getRowThread = new Runnable() {
			@Override
			public void run() {
				int startRow = 0;
				try {
					while (true) {
						Pair<Boolean, Row> pair = rowSupplier.getRow(sheet, startRow);
						Row row = pair.getSecond();
						if (row == null) {
							break;
						}
						queue.put(row);
						if (!pair.getFirst()) {
							break;
						}
						startRow++;
					}
				} catch (Exception e) {
					// just return
				}
				// there are no more rows
				moreRow.set(false);
			}
		};
		THREADPOOL.execute(getRowThread);
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
		for (int i = 0; i < threadCount; i++) {
			THREADPOOL.execute(new ConsumeRowThread(moreRow, queue, rowConsumer, cyclicBarrier));
		}
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
