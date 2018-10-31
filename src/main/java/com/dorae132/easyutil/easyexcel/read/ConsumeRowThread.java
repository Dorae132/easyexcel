package com.dorae132.easyutil.easyexcel.read;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.poi.ss.usermodel.Row;

/**
 * 消费rowthread
 * @author Dorae
 *
 */
public class ConsumeRowThread implements Runnable {

	private AtomicBoolean moreRow;

	private LinkedBlockingQueue<Row> queue;

	private IRowConsumer rowConsumer;

	private CyclicBarrier cyclicBarrier;

	@Override
	public void run() {
		while (true) {
			Row row = queue.poll();
			if (row == null) {
				if (moreRow.get()) {
					// there are more rows
					continue;
				} else {
					// there are no more rows
					break;
				}
			}
			rowConsumer.consume(row);
		}
		try {
			cyclicBarrier.await();
		} catch (InterruptedException e) {
			return;
		} catch (BrokenBarrierException e) {
			// do nothing
		}
	}

	public ConsumeRowThread(AtomicBoolean moreRow, LinkedBlockingQueue<Row> queue, IRowConsumer rowConsumer,
			CyclicBarrier cyclicBarrier) {
		super();
		this.moreRow = moreRow;
		this.queue = queue;
		this.rowConsumer = rowConsumer;
		this.cyclicBarrier = cyclicBarrier;
	}

}
