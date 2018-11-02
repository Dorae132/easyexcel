package com.github.Dorae132.easyutil.easyexcel.read;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.collections.CollectionUtils;

import com.github.Dorae132.easyutil.easyexcel.read.event.IHandlerContext;

/**
 * 消费rowthread
 * 
 * @author Dorae
 *
 */
public class ConsumeRowThread<C> implements Runnable {

	private IHandlerContext<C> context;

	private IRowConsumer<C> rowConsumer;

	private CyclicBarrier cyclicBarrier;

	@Override
	public void run() {
		while (true) {
			List<C> row = null;
			try {
				row = context.getRow();
			} catch (Exception e) {
				// do nothing
			}
			if (CollectionUtils.isEmpty(row)) {
				if (!context.isFileEnded()) {
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

	public ConsumeRowThread(IHandlerContext<C> context, IRowConsumer<C> rowConsumer, CyclicBarrier cyclicBarrier) {
		super();
		this.context = context;
		this.rowConsumer = rowConsumer;
		this.cyclicBarrier = cyclicBarrier;
	}

}
