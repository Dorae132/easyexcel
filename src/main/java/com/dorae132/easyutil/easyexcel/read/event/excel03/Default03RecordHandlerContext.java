package com.dorae132.easyutil.easyexcel.read.event.excel03;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

import com.dorae132.easyutil.easyexcel.read.event.excel03.handler.Abstract03RecordHandler;
import com.dorae132.easyutil.easyexcel.read.event.excel03.handler.BlankRecordHandler;
import com.dorae132.easyutil.easyexcel.read.event.excel03.handler.NumberRecordHandler;
import com.dorae132.easyutil.easyexcel.read.event.excel03.handler.RowEndRecordHandler;
import com.dorae132.easyutil.easyexcel.read.event.excel03.handler.SSTRecordHandler;
import com.dorae132.easyutil.easyexcel.read.event.excel03.handler.StringRecordHandler;
import com.dorae132.easyutil.easyexcel.read.event.excel03.handler.TailRecordHandler;
import com.google.common.collect.Lists;

/**
 * The defaut context for the handler
 * @author Dorae
 *
 */
public class Default03RecordHandlerContext implements IRecordHandlerContext<Record, String> {

	private int currColNum;
	
	private SSTRecord sstRecord;
	
	private List<String> currRowList;
	
	private LinkedBlockingQueue<List<String>> rowQueue;
	
	private Abstract03RecordHandler headRecordHandler;

	public static class DefaultRecordContextFactory {
		public static Default03RecordHandlerContext getContext() {
			Default03RecordHandlerContext context = new Default03RecordHandlerContext();
			context.currColNum = 0;
			context.currRowList = Lists.newArrayList();
			context.rowQueue = new LinkedBlockingQueue<>();
			context.initHeadHandler();
			return context;
		}
	}
	
	protected void initHeadHandler() {
		// The head handler do nothing, just pass
		headRecordHandler = new Abstract03RecordHandler(this) {
			@Override
			public void decode(Record record) throws Exception {
			}		
			@Override
			public boolean couldDecode(Record record) {
				return false;
			}
		};
		SSTRecordHandler sstRecordHandler = new SSTRecordHandler(this);
		StringRecordHandler stringRecordHandler = new StringRecordHandler(this);
		NumberRecordHandler numberRecordHandler = new NumberRecordHandler(this);
		BlankRecordHandler blankRecordHandler = new BlankRecordHandler(this);
		RowEndRecordHandler rowEndRecordHandler = new RowEndRecordHandler(this);
		TailRecordHandler tailRecordHandler = new TailRecordHandler(this);
		headRecordHandler.setNext(sstRecordHandler).setNext(stringRecordHandler).setNext(numberRecordHandler)
				.setNext(blankRecordHandler).setNext(rowEndRecordHandler).setNext(tailRecordHandler);
	}
	
	private Default03RecordHandlerContext() {
		super();
	}

	@Override
	public void handle(Record record) throws Exception {
		this.headRecordHandler.handle(record);
	}
	
	@Override
	public void registRecordHandler(Abstract03RecordHandler recordHandler) {
		// Let the SSTRecordHandler has the highest priority.
		recordHandler.setNext(headRecordHandler.next.next);
		headRecordHandler.next.setNext(recordHandler);
	}

	@Override
	public void setCurrColNum(int currColNum) {
		this.currColNum = currColNum;
	}

	@Override
	public int getCurrColNum() {
		return this.currColNum;
	}

	@Override
	public SSTRecord getSSTRecord() {
		return this.sstRecord;
	}

	@Override
	public void initCurrRowList(int colNum) {
		if (colNum <= 0) {
			this.currRowList = Lists.newArrayList();
		} else {
			this.currRowList = Lists.newArrayListWithExpectedSize(colNum);
		}
	}

	@Override
	public List<String> getCurrRowList() {
		return this.currRowList;
	}

	@Override
	public void addCol2CurrRowList(String colValue) {
		currRowList.add(colValue);
	}

	@Override
	public void setSSTRecord(SSTRecord sstRecord) {
		this.sstRecord = sstRecord;
	}

	@Override
	public void newRow(List<String> row) throws InterruptedException {
		this.rowQueue.put(row);
	}
}
