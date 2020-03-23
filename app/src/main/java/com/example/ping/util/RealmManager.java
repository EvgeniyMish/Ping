package com.example.ping.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.example.ping.MyApp;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class RealmManager {

    private static String TAG = RealmManager.class.getName();

    private static final int SCHEMA_VERSION = 0;
    private static final String DB_NAME = "ping.realm";

    private static RealmManager instance = null;
    private final Handler mHandler;
    private final SharedPreferencesManager mPrefs;

    private Realm mRealm;

    private RealmManager() {
        mHandler = new Handler(Looper.getMainLooper());
        Realm.init(MyApp.getInstance().getApplicationContext());

        mPrefs = SharedPreferencesManager.getInstance();
        createRealmEncrypted();
    }

    public static RealmManager getInstance() {
        if (instance == null) {
            instance = new RealmManager();
        }

        if (instance.getRealm() == null) {
            instance.createRealmEncrypted();
        }

        return instance;
    }

    public Realm getRealm() {
        return mRealm;
    }

    private void createRealmEncrypted() {
        byte[] dbKey;
        try {
            dbKey = Base64.decode(mPrefs.getDBKey(), Base64.DEFAULT);
            if (dbKey == null) {
                return;
            }
        } catch (Exception e) {
            LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"Created DB exception",false);
            return;
        }

        try {
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                    .encryptionKey(dbKey)
                    .schemaVersion(SCHEMA_VERSION).deleteRealmIfMigrationNeeded()
                    .name(DB_NAME)
                    .build();

            Realm.setDefaultConfiguration(realmConfiguration);

            mRealm = Realm.getDefaultInstance();

            LogManager.getInstance().setLog(LogManager.LogType.INFO,TAG,"Created DB ",false);

        } catch (Throwable t) {
            t.printStackTrace();

        }
    }

    public void realExecute(final Runnable r) {
        if (Thread.currentThread().getId() == mHandler.getLooper().getThread().getId()) {

            r.run();
        } else {

            mHandler.post(r);
        }
    }

    public void save(final RealmObject obj, final boolean bOverride, final String classObject,
                     Realm.Transaction.OnSuccess success) {

        Realm.Transaction.OnSuccess finalSuccess = success;
        mHandler.post(() -> {

            getRealm().executeTransactionAsync(
                    realm -> {

                        if (bOverride)
                            realm.insertOrUpdate(obj);

                        else
                            realm.insert(obj);

                    },
                    finalSuccess,
                    error ->
                            LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"Created object exception",false)
            );
        });
    }

    public void closeRealm() {
        if (!getRealm().isClosed()) {
            getRealm().close();

        }
    }
 }
