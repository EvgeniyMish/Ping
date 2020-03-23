package com.example.ping.view.adapter;

import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ping.R;
import com.example.ping.model.LogModel;

import java.text.SimpleDateFormat;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

import static com.example.ping.util.LogManager.LogType.*;

public class RecyclerViewLogAdapter extends RealmRecyclerViewAdapter<LogModel, RecyclerViewLogAdapter.MyViewHolder> {

    public RecyclerViewLogAdapter(@Nullable OrderedRealmCollection data, boolean autoUpdate) {
        super(data, autoUpdate);
    }

    @NonNull
    @Override
    public RecyclerViewLogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_item, parent, false);
        return new RecyclerViewLogAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewLogAdapter.MyViewHolder holder, int position) {
        LogModel log = (LogModel) getItem(position);

        if(log.getType()==INFO.ordinal()){
            holder.log.setTextColor(Color.BLUE);
        }else if(log.getType()==ERROR.ordinal()){
            holder.log.setTextColor(Color.RED);
        }else{
            holder.log.setTextColor(Color.BLACK);
        }

        holder.log.setText(log.getLog());
        holder.time.setText(new SimpleDateFormat("dd-MM-YYYY:HH.mm.ss").format(log.getCreatedAt()));

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView log;

        public MyViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.tv_time);
            log = view.findViewById(R.id.tv_log);
        }
    }
}
