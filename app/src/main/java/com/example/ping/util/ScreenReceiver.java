package com.example.ping.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.ping.view.MainActivity;
import com.example.ping.MyApp;
import com.example.ping.R;
import com.example.ping.model.MessageModel;

import io.realm.RealmResults;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ScreenReceiver extends BroadcastReceiver {

    private static String TAG = ScreenReceiver.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            RealmManager.getInstance().realExecute(new Runnable() {
                @Override
                public void run() {
                 RealmResults<MessageModel> results =  RealmManager.getInstance().getRealm().where(MessageModel.class).equalTo("sendStatus", MessageModel.Status.RECIVEID.ordinal()).findAll();

                 if(!results.isEmpty()) {
                        NotificationManager nm = (NotificationManager) MyApp.getInstance().getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                        Intent fullScreenIntent = new Intent(MyApp.getInstance().getApplicationContext(), MainActivity.class);
                                fullScreenIntent.putExtra("bio",true);
                        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(MyApp.getInstance().getApplicationContext(), 0,
                                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyApp.getInstance().getApplicationContext(), "ping")
                                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)


                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setFullScreenIntent(fullScreenPendingIntent, true);
                     builder.setContentTitle("New messages");
                     if(results.size()==1){

                         builder.setContentText("You have one message");
                 }else if(results.size()>1){
                         builder.setContentText("You have "+results.size()+" messages");
                 }
                    nm.notify(123, builder.build());

                     for(MessageModel model: results){
                         model.setStatus(MessageModel.Status.NOTIFICATION.ordinal());
                     }
                }
                }
            });
            LogManager.getInstance().setLog(LogManager.LogType.INFO,TAG,"Screen Off detected",true);
        }

    }
}