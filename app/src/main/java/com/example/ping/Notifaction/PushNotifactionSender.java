package com.example.ping.Notifaction;


import com.example.ping.model.SendModel;

import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public class PushNotifactionSender {
    /*{
        "to": "/topics/ping",
            "data":{
               "mess":[
                        { "title": "Notification title",
                          "message": "Notification message",
                          "key1" : "value1",
                          "key2" : "value2"
                        }
  	                   ]
  	                }
      }*/

    final private String FCM_API = "https://fcm.googleapis.com/fcm/";
    private  ApiService service = null;
    private OkHttpClient.Builder httpClient  = null;
    private static PushNotifactionSender pushNotifactionSender = null;

    public static PushNotifactionSender getInstance(){
        if(pushNotifactionSender==null){
            pushNotifactionSender = new PushNotifactionSender();
        }
        return pushNotifactionSender;
    }

    private PushNotifactionSender(){
        OkHttpClient.Builder httpClient  = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
         interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FCM_API)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

         service = retrofit.create(ApiService.class);
    }

    public Call sendPush(ArrayList data) throws JSONException {
        return service.listRepos(new SendModel(data));
    }




    public interface ApiService {
        @Headers({
                "Authorization: key=AIzaSyAuGJTZjZ2M6ZueWsEEurZp09xf7yVUAas",
                "Content-Type:application/json"
        })
        @POST("send")
        Call<String> listRepos(@Body SendModel s);
    }
}