package com.example.ping.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.ping.R;
import com.example.ping.util.LogManager;
import com.example.ping.util.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SharedPreferencesManager.getInstance().getFCMToken()==null){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {

                                return;
                            }
                            String token = task.getResult().getToken();

                            SharedPreferencesManager.getInstance().setFCMToken(token);
                            LogManager.getInstance().setLog(LogManager.LogType.DEBUG,"MainActivity","Save fcm token",false);
                        }
                    });
        }
    }
}
