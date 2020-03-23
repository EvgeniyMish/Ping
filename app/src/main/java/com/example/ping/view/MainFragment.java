package com.example.ping.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ping.R;
import com.example.ping.model.LogModel;
import com.example.ping.model.MessageModel;
import com.example.ping.util.Crypto;
import com.example.ping.util.LogManager;
import com.example.ping.util.RealmManager;
import com.example.ping.view.adapter.RecyclerViewLogAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executor;

public class MainFragment extends Fragment {


    private EditText messageText;
    private Button sendMessageBtn;
    private RecyclerView rvLog;
    private FloatingActionButton chat;



    @Override
     public View onCreateView( LayoutInflater inflater , ViewGroup container,
             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
         return inflater.inflate(R.layout.activity_main_fragment, container, false);
     }

    @Override
    public void onViewCreated(View view , Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageText = view.findViewById(R.id.message_text);
        sendMessageBtn = view.findViewById(R.id.send_btn);
        rvLog = view.findViewById(R.id.rvLog);
        chat = view.findViewById(R.id.chat);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvLog.setLayoutManager(layoutManager);
        rvLog.addItemDecoration(
                new DividerItemDecoration(
                        getContext(),
                        DividerItemDecoration.VERTICAL
                )
        );

        RecyclerViewLogAdapter adapter = new RecyclerViewLogAdapter(RealmManager.getInstance().getRealm().where(LogModel.class).findAll(),true);
        rvLog.setAdapter(adapter);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog(view);
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = null;
                String signature=null;

                    mess = Crypto.getInstance().getEncoded(messageText.getText().toString());


                    signature = Crypto.getInstance().sign(messageText.getText().toString());

                if(mess!=null&&!mess.isEmpty()&signature!=null&&!signature.isEmpty()) {
                    new MessageModel(mess, signature);
                    messageText.getText().clear();
             }

            }
        });

        if(RealmManager.getInstance().getRealm().where(MessageModel.class).equalTo("sendStatus", MessageModel.Status.NOTIFICATION.ordinal()).findAll().size()>0) {
            setDialog(view);
        }


    }

    void setDialog(View view){
        String TAG = "BIO";
         Executor executor;
         BiometricPrompt biometricPrompt;
         BiometricPrompt.PromptInfo promptInfo;
         NavController navController = Navigation.findNavController(view);
         executor = ContextCompat.getMainExecutor(getContext());
         biometricPrompt = new BiometricPrompt(getActivity(),
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"onAuthenticationError",true);

                navController.navigate(R.id.action_mainFragment_to_biometricsApprovingFragment);

            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                LogManager.getInstance().setLog(LogManager.LogType.INFO,TAG,"onAuthenticationSucceeded",true);
                navController.navigate(R.id.action_mainFragment_to_biometricsApprovingFragment);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                LogManager.getInstance().setLog(LogManager.LogType.ERROR,TAG,"onAuthenticationFailed",true);
                navController.navigate(R.id.action_mainFragment_to_biometricsApprovingFragment);

            }

        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Open with your biometric credential")
                .setNegativeButtonText("Cancel")
                .build();


        AlertDialog.Builder dialog= new AlertDialog.Builder(getContext());
        dialog.setTitle("Biometric Authentication");
        dialog.setMessage("If you want get messages, use Biometric Authentication");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener( ) {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }
}
