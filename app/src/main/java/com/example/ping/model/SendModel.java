package com.example.ping.model;

import java.util.ArrayList;

public class SendModel {

    String to = "/topics/ping";
    Messages data = new Messages();

    public SendModel(ArrayList<MessageModel> newList){
        data = new Messages(newList);
    }

    public class Messages {
      public  ArrayList<MessageModel> messages = new ArrayList<>();
        public Messages(){}

        public Messages(ArrayList<MessageModel> newList){
            messages = newList;
        }

        public ArrayList<MessageModel> getList(){return messages;}
    }
}
