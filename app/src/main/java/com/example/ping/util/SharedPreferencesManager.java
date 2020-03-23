package com.example.ping.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ping.MyApp;

public  class SharedPreferencesManager {
    private static String PREFERENCES_NAME = "pingApp";
    private static SharedPreferencesManager sharedPreferencesManager = null;
    private static SharedPreferences.Editor editor = null;
    private static SharedPreferences preferences = null;


    private String PUBLIK_KEY = "publicKey";
    private String SIGNATURE_PUBLIK_KEY = "signaturePublicKey";
    private String FCM_TOKEN = "fcmToken";
    private String DB_KEY = "dbKey";




    public static SharedPreferencesManager  getInstance(){
        if(sharedPreferencesManager==null){
            sharedPreferencesManager = new SharedPreferencesManager();
            preferences = MyApp.getInstance().getApplicationContext().getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return sharedPreferencesManager;
    }




    public void setPublicKey(String publicKey){
        editor.putString(PUBLIK_KEY, publicKey).commit();
    }

    public String getPublicKey(){return preferences.getString(PUBLIK_KEY, null);}

    public void setFCMToken(String fcmToken){
        editor.putString(FCM_TOKEN, fcmToken).commit();
    }

    public String getFCMToken(){
        return preferences.getString(FCM_TOKEN, null);
    }

    public void setDBKey(String dbKey){
         editor.putString(DB_KEY, dbKey).commit();
     }

    public String getDBKey(){
         return preferences.getString(DB_KEY, null);
     }

    public String getSignaturePublicKey(){return preferences.getString(SIGNATURE_PUBLIK_KEY, null);}


}
