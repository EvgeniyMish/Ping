package com.example.ping.util;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.ping.MyApp;

import java.util.concurrent.TimeUnit;

public class AppLifecycleObserver implements LifecycleObserver {

    public static final String TAG = AppLifecycleObserver.class.getName();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        LogManager.getInstance().setLog(LogManager.LogType.INFO,TAG,"App in background detected",false);

        OneTimeWorkRequest workerRequest = new OneTimeWorkRequest.Builder(RequestWorker.class)
                .setInitialDelay(2, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(MyApp.getInstance().getApplicationContext()).enqueue(workerRequest);
    }

}