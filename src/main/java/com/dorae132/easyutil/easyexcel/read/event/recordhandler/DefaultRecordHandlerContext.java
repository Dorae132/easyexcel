package com.dorae132.easyutil.easyexcel.read.event.recordhandler;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

import com.google.common.collect.Lists;

/**
 * The defaut context for the handler
 * @author Dorae
 *
 */
public class DefaultRecordHandlerContext implements IRecordHandlerContext<String> {

	private int currColNum;
	
	private SSTRecord sstRecord;
	
	private List<String> currRowList;
	
	private LinkedBlockingQueue<List<String>> rowQueue;
	
	private AbstractRecordHandler headRecordHandler;

	public static class DefaultRecordContextFactory {
		public static DefaultRecordHandlerContext getContext() {
			DefaultRecordHandlerContext context = new DefaultRecordHandlerContext();
			context.currColNum = 0;
			context.currRowList = Lists.newArrayList();
			context.rowQueue = new LinkedBlockingQueue<>();
			context.initHeadHandler();
			return context;
		}
	}
	
	protected void initHeadHandler() {
		// The head handler do nothing, just pass
		headRecordHandler = new AbstractRecordHandler() {
			@Override
			protected void decode(IRecordHandlerContext handlerContext, Record record) throws Exception {
			}		
			@Override
			protected boolean couldDecode(IRecordHandlerContext handlerContext, Record record) {
				return false;
			}
		};
		SSTRecordHandler sstRecordHandler = new SSTRecordHandler();
		StringRecordHandler stringRecordHandler = new StringRecordHandler();
		NumberRecordHandler numberRecordHandler = new NumberRecordHandler();
		BlankRecordHandler blankRecordHandler = new BlankRecordHandler();
		RowEndRecordHandler rowEndRecordHandler = new RowEndRecordHandler();
		TailRecordHandler tailRecordHandler = new TailRecordHandler();
		headRecordHandler.setNext(sstRecordHandler).setNext(stringRecordHandler).setNext(numberRecordHandler)
				.setNext(blankRecordHandler).setNext(rowEndRecordHandler).setNext(tailRecordHandler);
	}
	
	private DefaultRecordHandlerContext() {
		super();
	}

	@Override
	public void handle(Record record) throws Exception {
		this.headRecordHandler.handle(this, record);
	}
	
	@Override
	public void registRecordHandler(AbstractRecordHandler recordHandler) {
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
