package com.example.ping.util;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ping.Notifaction.PushNotifactionSender;
import com.example.ping.model.MessageModel;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class RequestWorker extends Worker {

    private static String TAG = RequestWorker.class.getName();
    public RequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
       RealmManager.getInstance().realExecute(new Runnable() {
           @Override
           public void run() {
               LogManager.getInstance().setLog(LogManager.LogType.INFO,TAG,"Sending messages",true);
              final ArrayList<MessageModel> senders =  new ArrayList<>(RealmManager.getInstance().getRealm().copyFromRealm(RealmManager.getInstance().getRealm().where(MessageModel.class).equalTo("sendStatus", MessageModel.Status.WAIT.ordinal()).findAll()));

                   if (!senders.isEmpty()) {
                       try {

                       PushNotifactionSender.getInstance().sendPush(senders).enqueue(new retrofit2.Callback() {
                           @Override
                           public void onResponse(Call call, Response response) {
                               LogManager.getInstance().setLog(LogManager.LogType.DEBUG,TAG,"Sent messages",false);
                               for (MessageModel mode : senders) {
                                   RealmManager.getInstance().realExecute(new Runnable() {
                                       @Override
                                       public void run() {
                                           RealmManager.getInstance().getRealm().where(MessageModel.class).equalTo("createdAt", mode.getCreatedAt()).findFirst().setStatus(MessageModel.Status.SENT.ordinal());
                                       }
                                   });

                               }
                           }

                           @Override
                           public void onFailure(Call call, Throwable t) {
                               LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"Send messages exception ",true);
                           }
                       });
                   } catch(JSONException e){
                       e.printStackTrace();
                   }
               }
           }
       });
        return Worker.Result.success();
    }
}
