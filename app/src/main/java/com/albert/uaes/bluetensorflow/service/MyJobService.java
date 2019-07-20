package com.albert.uaes.bluetensorflow.service;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class MyJobService extends JobService {
    private int kJobId = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyJobDaemonService", "jobService启动");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStartJob方法");
        boolean isLocalServiceWork = isServiceWork(this, "com.wangjing.processlive.service.MyService");
        if(!isLocalServiceWork){
            this.startService(new Intent(this,MyService.class));
//            this.startService(new Intent(this,Service2.class));
//            Toast.makeText(this, "进程启动", Toast.LENGTH_SHORT).show();
            Log.i("onStartJob", "启动service1");
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("MyJobDaemonService", "执行了onStopJob方法");
//        scheduleJob(getJobInfo());
        boolean isLocalServiceWork = isServiceWork(this, "com.wangjing.processlive.service.MyService");
        if(!isLocalServiceWork){
            this.startService(new Intent(this,MyService.class));
//            this.startService(new Intent(this,Service2.class));
//            Toast.makeText(this, "进程启动", Toast.LENGTH_SHORT).show();
            Log.i("onStartJob", "启动service1");
        }
        return true;
    }

    //将任务作业发送到作业调度中去
    public void scheduleJob(JobInfo t) {
        Log.i("MyJobDaemonService", "调度job");
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    public JobInfo getJobInfo(){
        JobInfo.Builder builder = new JobInfo.Builder(kJobId++, new ComponentName(this, MyJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        //间隔1000毫秒
        builder.setPeriodic(1000);
        return builder.build();
    }

    // 判断服务是否正在运行
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}
