package com.example.ping.model;

import android.util.Log;

import com.example.ping.util.LogManager;
import com.example.ping.util.RealmManager;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MessageModel extends RealmObject {

    @PrimaryKey
    long createdAt;

    String message;
    String signature;
    int sendStatus;

    public MessageModel(){}

    public MessageModel(String message, String signature){
        createdAt = System.currentTimeMillis();
        this.message = message;

        this.signature = signature;
        sendStatus = Status.WAIT.ordinal();
        saveObject();
    }

    public void setStatus(int mysendStatus){

        RealmManager.getInstance().getRealm().beginTransaction();
                sendStatus = mysendStatus;
                RealmManager.getInstance().getRealm().commitTransaction();


    }

    public int getSendStatus(){
        return sendStatus;
    }

    public long getCreatedAt(){
        return createdAt;
    }

    public String getMessage(){
        return message;
    }

    public String getSignature(){return signature;}

    private void saveObject(){
        RealmManager.getInstance().save(this, true, MessageModel.class.getName(), new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                LogManager.getInstance().setLog(LogManager.LogType.INFO,"MessageModel","save object",false);
            }
        });
    }


    public enum Status{
        WAIT,
        SENT,
        RECIVEID,
        NOTIFICATION,
        SEEN

    }


}
