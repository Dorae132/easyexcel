package com.github.Dorae132.easyutil.easyexcel.read.event.excel07;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.Dorae132.easyutil.easyexcel.read.event.IHandlerContext;

/**
 * The defaut context for the handler
 * @author Dorae
 *
 */
public class Default07RecordHandlerContext implements IHandlerContext<String> {
	
	private LinkedBlockingQueue<List<String>> rowQueue;
	
	private XlsxHandler handler;
	
	private AtomicBoolean fileEndFlag = new AtomicBoolean(false);

	private String fileName;

	public static class Default07RecordContextFactory {
		public static Default07RecordHandlerContext getContext(XlsxHandler xlsxHandler, String fileName) {
			Default07RecordHandlerContext context = new Default07RecordHandlerContext();
			context.rowQueue = new LinkedBlockingQueue<>();
			context.handler = xlsxHandler;
			context.fileName = fileName;
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

	@Override
	public List<String> getRow() throws Exception {
		return this.rowQueue.poll();
	}

	@Override
	public boolean fileEnd() {
		fileEndFlag.set(true);
		return fileEndFlag.get();
	}

	@Override
	public boolean isFileEnded() {
		return fileEndFlag.get();
	}

	@Override
	public void process() throws Exception {
		handler.process(fileName);
	}
}
