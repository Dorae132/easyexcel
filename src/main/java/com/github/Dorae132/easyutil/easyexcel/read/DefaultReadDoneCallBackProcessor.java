package com.github.Dorae132.easyutil.easyexcel.read;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.github.Dorae132.easyutil.easyexcel.ExcelProperties;
import com.github.Dorae132.easyutil.easyexcel.common.EasyExcelException;
import com.github.Dorae132.easyutil.easyexcel.common.Pair;

/**
 * the default processor<br>
 * this implemet will not be recomanded.
 * @author Dorae
 *
 */
public class DefaultReadDoneCallBackProcessor implements IReadDoneCallBackProcessor<Void, Void> {

    @Override
    public Void process(Pair<CountDownLatch, IReadDoneCallBack> callback, ExcelProperties<?, Void> properties)
            throws Exception {
        new Thread(() -> {
            try {
                callback.getFirst().await(properties.getReadThreadWaitTime(), TimeUnit.SECONDS);
            } catch (Exception e) {
                throw new EasyExcelException(e);
            }
            callback.getSecond().call();
        }).start();
        return null;
    }

}
