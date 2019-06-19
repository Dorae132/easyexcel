package com.github.Dorae132.easyutil.easyexcel.read;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.github.Dorae132.easyutil.easyexcel.common.EasyExcelException;
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
	
	private CountDownLatch lanch;

	@Override
	public void run() {
	    try {
    		while (true) {
    			List<C> row = null;
    			row = context.getRow();
    			if (CollectionUtils.isEmpty(row)) {
    				if (!context.isFileEnded()) {
    					// there are more rows
    				    TimeUnit.MILLISECONDS.sleep(100);
    					continue;
    				} else {
    					// there are no more rows
    					break;
    				}
    			}
    			rowConsumer.consume(row);
    		}
	    } catch (Exception e) {
            throw new EasyExcelException(e);
        } finally {
            if (lanch != null) {
                lanch.countDown();
            }
        }
	}

	public ConsumeRowThread(IHandlerContext<C> context, IRowConsumer<C> rowConsumer, CountDownLatch latch) {
		super();
		this.context = context;
		this.rowConsumer = rowConsumer;
		this.lanch = latch;
	}

}
