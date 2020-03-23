package com.example.ping.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ping.R;
import com.example.ping.view.adapter.RecyclerViewAdapter;
import com.example.ping.model.MessageModel;
import com.example.ping.util.RealmManager;

import io.realm.RealmResults;

public class BiometricsApprovingFragment extends Fragment {

    RecyclerView rvMessages;
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.biometrics_approving_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view , Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMessages = view.findViewById(R.id.rvMessages);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(linearLayoutManager);
        rvMessages.addItemDecoration(
                new DividerItemDecoration(
                        getContext(),
                        DividerItemDecoration.VERTICAL
                )
        );

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(RealmManager.getInstance().getRealm().where(MessageModel.class).findAll(),true);
        rvMessages.setAdapter(adapter);

    }

    @Override
    public void onPause() {
        super.onPause();
        RealmResults<MessageModel> results = RealmManager.getInstance().getRealm().where(MessageModel.class).equalTo("sendStatus", MessageModel.Status.NOTIFICATION.ordinal()).findAll();
        if(results.size()>0){
            for(MessageModel model:results){
                RealmManager.getInstance().realExecute(new Runnable() {
                    @Override
                    public void run() {
                       model.setStatus(MessageModel.Status.SEEN.ordinal());
                    }
                });

            }
        }
    }
}
