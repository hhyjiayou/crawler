package com.xiaomi.zhibo.crawler.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hhy on 16-8-25.
 */
public class ExecutorFactory {
    private static final ExecutorService threadPool = new ThreadPoolExecutor(6, 50, 100, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public static ExecutorService getExecutor() {
        return threadPool;
    }
}
