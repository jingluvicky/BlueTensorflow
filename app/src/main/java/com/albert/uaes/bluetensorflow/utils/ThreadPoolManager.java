package com.albert.uaes.bluetensorflow.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {
    public static final int Thread_PoolExecutor_Type = 0;
    public static final int Scheduled_ThreadPool_Executor_Type = 1;
    private static ThreadPoolManager mInstance;

    private int type;

    public static ThreadPoolManager getInstance(int type) {
        if (mInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolManager(type);
                }
            }
        }
        return mInstance;
    }
    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 1;
    private TimeUnit unit = TimeUnit.HOURS;
    private ThreadPoolExecutor executor;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private ThreadPoolManager(int type) {

        this.type = type;
        /**
         * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
         */
        this.corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //虽然maximumPoolSize用不到，但是需要赋值，否则报错
        this.maximumPoolSize = corePoolSize;
    }

    /**
     * 执行任务
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        if (executor == null) {
            //线程池执行者。
            //参1:核心线程数;参2:最大线程数;参3:线程休眠时间;参4:时间单位;参5:线程队列;参6:生产线程的工厂;参7:线程异常处理策略
            executor = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    //   Executors.defaultThreadFactory(),
                    new DefaultThreadFactory(Thread.NORM_PRIORITY, "tiaoba-pool-"),
                    new ThreadPoolExecutor.AbortPolicy());
        }
        if (runnable != null) {
            executor.execute(runnable);
        }
    }

    /**
     *
     * @param runnable  runnable线程
     * @param delayTime 延迟时间
     * @param period  周期
     * @param timeUnit 时间单元
     */
    public void scheduleAtFixedRate(Runnable runnable,long delayTime,long period,TimeUnit timeUnit) {
        if (scheduledThreadPoolExecutor == null){
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(corePoolSize,new DefaultThreadFactory(Thread.NORM_PRIORITY, "scheduled-pool-"));
        }
        if (runnable != null){
            scheduledThreadPoolExecutor.scheduleAtFixedRate(runnable,delayTime,period,timeUnit);
        }
    }

    /**
     * 移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable!=null){
            switch (type){
                case Scheduled_ThreadPool_Executor_Type:
//                    scheduledThreadPoolExecutor.remove(runnable);
                    if (scheduledThreadPoolExecutor!=null){
                        scheduledThreadPoolExecutor.shutdown();
                        scheduledThreadPoolExecutor = null;
                    }
                    break;
                case Thread_PoolExecutor_Type:
                    executor.remove(runnable);
                    break;
            }
        }
    }

    /**
     * 创建线程的工厂，设置线程的优先级，group，以及命名
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        /**
         * 线程池的计数
         */
        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        /**
         * 线程的计数
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(threadPriority);
            return t;
        }
    }
}
