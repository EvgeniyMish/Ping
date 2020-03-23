package com.example.ping.util;

import android.util.Log;

import com.example.ping.model.LogModel;


public class LogManager {

    private static LogManager logManager;

    public static LogManager getInstance(){
        if (logManager == null) {
            logManager = new LogManager();
        }
        return logManager;
    }

    public void setLog(LogType type, String tag, String log, boolean isSave){
        if(tag!=null&&log!=null) {
            switch (type) {
                case INFO:
                    Log.i(tag, log);
                    break;
                case ERROR:
                    Log.e(tag, log);
                    break;
                case DEBUG:
                    Log.d(tag, log);
                    break;
            }
            if(isSave) {
                new LogModel(type.ordinal(), log);
            }
        }

    }


    public  enum LogType{
        INFO,
        ERROR,
        DEBUG
    }


}
