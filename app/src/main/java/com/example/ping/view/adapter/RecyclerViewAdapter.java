package com.example.ping.view.adapter;


import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ping.R;
import com.example.ping.model.MessageModel;
import com.example.ping.util.Crypto;

import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class RecyclerViewAdapter extends RealmRecyclerViewAdapter<MessageModel, RecyclerViewAdapter.MyViewHolder> {

    public RecyclerViewAdapter(@Nullable OrderedRealmCollection data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessageModel model = (MessageModel) getItem(position);
            holder.message.setText(Crypto.getInstance().verifySign(Base64.decode(model.getSignature(),Base64.NO_WRAP),model.getMessage()));
            holder.time.setText(new SimpleDateFormat("dd-MM-YYYY:HH.mm.ss").format(model.getCreatedAt()));

            if(model.getSendStatus()== MessageModel.Status.NOTIFICATION.ordinal()){
                holder.isNew.setVisibility(View.VISIBLE);
            }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView message;
        public TextView isNew;

        public MyViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.time);
            message = view.findViewById(R.id.message);
            isNew = view.findViewById(R.id.is_new);
        }
    }
}
