package com.coolstar.makeposter.utils;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理对象
 * Created by jiguangxing on 2016/3/4.
 */
public class ThreadBuilder {
    private static final int SIZE_CORE = 2;
    private static final int SIZE_MAX = 10;
    private static final long ALIVETIME = 60;

    private static ThreadBuilder instance;
    private final java.util.concurrent.SynchronousQueue<java.lang.Runnable> workQueues;

    public static ThreadBuilder getInstance(){
        if(instance == null){
            instance = new ThreadBuilder();
        }
        return instance;
    }

    final ThreadPoolExecutor executor;
    private ThreadBuilder() {
        workQueues = new SynchronousQueue<Runnable>();
        executor =  new ThreadPoolExecutor(SIZE_CORE,SIZE_MAX,ALIVETIME, TimeUnit.SECONDS,workQueues);
    }

    public void runThread(Runnable r){
        if(executor.isTerminating()==false){
            executor.submit(r);
        }
    }

    public void cancelAll(){
        workQueues.clear();
        executor.shutdown();
    }

}
