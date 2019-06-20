package com.github.Dorae132.easyutil.easyexcel.read;

import java.util.concurrent.CountDownLatch;

import com.github.Dorae132.easyutil.easyexcel.ExcelProperties;
import com.github.Dorae132.easyutil.easyexcel.common.Pair;

/**
 * the processor that will be called after the read thread have been done.
 * @author Dorae
 *
 */
public interface IReadDoneCallBackProcessor<R, C> {
    
    R process(Pair<CountDownLatch, IReadDoneCallBack> callback, ExcelProperties properties) throws Exception;

}
