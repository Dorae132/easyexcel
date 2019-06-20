package com.github.Dorae132.easyutil.easyexcel.read;

import java.io.FileInputStream;

import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.github.Dorae132.easyutil.easyexcel.ExcelProperties;
import com.github.Dorae132.easyutil.easyexcel.read.event.IHandlerContext;
import com.github.Dorae132.easyutil.easyexcel.read.event.excel03.Default03RecordHandlerContext;
import com.github.Dorae132.easyutil.easyexcel.read.event.excel07.Default07RecordHandlerContext;
import com.github.Dorae132.easyutil.easyexcel.read.event.excel07.XlsxHandler;

/**
 * the enums of the excel versions
 * 
 * @author Dorae
 *
 */
public enum ExcelVersionEnums {

	V2003("xls"), V2007("xlsx");

	private String suffix;

	private ExcelVersionEnums(String suffix) {
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}

	public static IHandlerContext produceContext(ExcelProperties properties) throws Exception {
		StringBuilder fileNameSB = new StringBuilder(properties.getFileName());
		String fileNameSufix = fileNameSB.substring(fileNameSB.lastIndexOf(".") + 1, fileNameSB.length());
		String absolutePath = fileNameSB.insert(0, properties.getFilePath()).toString();
		try {
    		if (V2003.getSuffix().equals(fileNameSufix)) {
    		    FileInputStream inputStream = new FileInputStream(absolutePath);
    			HSSFRequest request = new HSSFRequest();
    			POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);
    			Default03RecordHandlerContext context = Default03RecordHandlerContext.Default03RecordContextFactory
    					.getContext(request, fileSystem);
    			MissingRecordAwareHSSFListener missingRecordAwareHSSFListener = new MissingRecordAwareHSSFListener(context);
    			FormatTrackingHSSFListener formatTrackingHSSFListener = new FormatTrackingHSSFListener(
    					missingRecordAwareHSSFListener);
    			request.addListenerForAllRecords(formatTrackingHSSFListener);
    			// hssfRequest.addListenerForAllRecords(new
    			// SheetRecordCollectingListener(formatTrackingHSSFListener));
    			return context;
    		} else if (V2007.getSuffix().equals(fileNameSufix)) {
    			XlsxHandler xlsxHandler = new XlsxHandler();
    			Default07RecordHandlerContext context = Default07RecordHandlerContext.Default07RecordContextFactory
    					.getContext(xlsxHandler, absolutePath);
    			xlsxHandler.setContext(context);
    			return context;
    		} else {
    			throw new RuntimeException("不支持的文件类型");
    		}
		} finally {
            // the stream will be closed int the context.
        }
	};
}
