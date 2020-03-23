package com.example.ping.Notifaction;

import android.util.Log;
import androidx.annotation.NonNull;

import com.example.ping.model.MessageModel;
import com.example.ping.model.SendModel;
import com.example.ping.util.LogManager;
import com.example.ping.util.RealmManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

    /*Response exaample
        [
            {
            "createdAt": 1584786453470,
            "signature": "CJQdMeA/VjLPVG4DkgoB6ZQRcHXGSiSXg7MDbqY17vP492TjNyBCNyCIr6RtUWWZvsfVKsyiEDuS\nNI8jcMflma/FTzPklMPh+oyyA0QquzrOj+H9aclk0MalPE7vYsAhoqlTtOyGiawGlEe4eUPD1I34\nGo8x45Uxy9ZuWbYr0Ck=\n",
            "sendStatus": false,
            "message": ""
            },
            {
            "createdAt": 1584786455429,
            "signature": "Xl1i9CxUkl28+lKyOjV0Phd56gqai54VbVWq2xesuqs0WwFU+i0Cat9SxNHlvvJ+1vGVKEL/PZYc\nX+EC/Dspj8Kot6Q9Y5mYxTSyZCo811/oYc20hLNon1N/qolOvONAI0D9Z9n9ma8gL37JmUXSQ+gn\nsfCeaBHRH8xKiNku8Hw=\n",
            "sendStatus": false,
            "message": ""
            }
        ]*/

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){

        SendModel.Messages mess  = new Gson().fromJson(remoteMessage.getData().toString(), (Type) SendModel.Messages.class);
        ArrayList<MessageModel>  list = mess.getList();

        LogManager.getInstance().setLog(LogManager.LogType.INFO,"Received push","get "+list.size()+" messages",true);

        for(MessageModel mode:list){
            RealmManager.getInstance().realExecute(new Runnable() {
                @Override
                public void run() {
                    RealmManager.getInstance().getRealm().where(MessageModel.class).equalTo("createdAt", mode.getCreatedAt()).findFirst().setStatus(MessageModel.Status.RECIVEID.ordinal());
                }
            });
        }
     }
}
