package com.example.ping.model;

import com.example.ping.util.LogManager;
import com.example.ping.util.RealmManager;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LogModel extends RealmObject {

    @PrimaryKey
    long createdAt;
    int type;
    String log;


    public LogModel(){};

    public LogModel(int type, String log){
        createdAt = System.currentTimeMillis();
        this.type = type;
        this.log = log;
        saveObject();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    private void saveObject(){
        RealmManager.getInstance().save(this, true, MessageModel.class.getName(), new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogManager.getInstance().setLog(LogManager.LogType.INFO,"LogModel","save object",false);
            }
        });
    }
}
