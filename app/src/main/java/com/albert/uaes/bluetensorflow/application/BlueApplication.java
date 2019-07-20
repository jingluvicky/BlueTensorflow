package com.albert.uaes.bluetensorflow.application;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BlueApplication extends Application {
    public  static Context context;
    public static ScheduledExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }

//    public static ScheduledExecutorService getExecutorService(){
//        if (executorService == null){
//            executorService = Executors.newScheduledThreadPool(10);
//        }
//        return executorService;
//    }
}
