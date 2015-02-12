package com.kariqu.tradecenter.repository.impl.ibatis;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

/**
 * @author Athens(刘杰)
 * @Time 2012-08-27 15:57
 * @since 1.0.0
 */
public class OrderNumberUtilTest {

    private static final ExecutorService pool = Executors.newSingleThreadExecutor();

    /** 总的线程数 */
    private static final int loopCount = 10000000;

    @Test
    public void testOrderNo() {
        System.out.println(loopCount + " 个线程\n====================================");
        long start = System.currentTimeMillis();
        System.out.printf("开始  : %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        final Set<Long> set = new HashSet<Long>();
        for (int i = 0; i < loopCount; i++) {
            pool.submit(new Runnable() {
                public void run() {
                    long orderNo = OrderNumberUtil.getOrderNo();
                    set.add(orderNo);
                    //System.out.println(orderNo);
                }
            });
        }

        // 等待线程全部运行完毕.
        pool.shutdown();
        try {
            // 等待 10 分钟. 直到所有任务全部完成
            pool.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // System.out.println("exception: " + e);
        }

        long end = System.currentTimeMillis();
        System.out.printf("结束  : %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.printf("耗时  : %.4f 秒.\n", (end - start) / 1000.0);

        assertEquals(loopCount + " 个线程生成的订单编号有 " + (loopCount - set.size()) + " 个重复的.", loopCount, set.size());
        System.out.println("====================================");
    }

}
