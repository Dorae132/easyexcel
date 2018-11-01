package com.dorae132.easyutil.easyexcel.read.event.excel07;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.dorae132.easyutil.easyexcel.read.event.IHandlerContext;

/**
 * The defaut context for the handler
 * @author Dorae
 *
 */
public class Default07RecordHandlerContext implements IHandlerContext<String> {
	
	private LinkedBlockingQueue<List<String>> rowQueue;
	
	private XlsxHandler headRecordHandler;

	public static class DefaultRecordContextFactory {
		public static Default07RecordHandlerContext getContext() {
			Default07RecordHandlerContext context = new Default07RecordHandlerContext();
			context.rowQueue = new LinkedBlockingQueue<>();
			return context;
		}
	}
	
	private Default07RecordHandlerContext() {
		super();
	}

	@Override
	public void newRow(List<String> row) throws Exception {
		this.rowQueue.put(row);
	}
}
