package com.example.ping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import java.security.SecureRandom;

import com.example.ping.util.AppLifecycleObserver;
import com.example.ping.util.LogManager;
import com.example.ping.util.RealmManager;
import com.example.ping.util.ScreenReceiver;
import com.example.ping.util.SharedPreferencesManager;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyApp extends MultiDexApplication {

    private static final String TAG = MyApp.class.getName();

    private static MyApp myApp=null;
    private BroadcastReceiver mReceiver;

    public static MyApp getInstance(){
        LogManager.getInstance().setLog(LogManager.LogType.DEBUG,TAG,"getInstance",false);
        return myApp;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initComponents();
   }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initComponents(){
        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
        myApp = this;
        if (SharedPreferencesManager.getInstance().getDBKey() != null) {
            RealmManager.getInstance();
        }else{

            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);
            SharedPreferencesManager.getInstance().setDBKey(Base64.encodeToString(key, Base64.NO_WRAP));
            RealmManager.getInstance();
        }


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/ping");


        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }


}