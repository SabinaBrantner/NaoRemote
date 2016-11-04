package fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nao.sabina.projectnao.ConnectionService;
import com.nao.sabina.projectnao.R;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * SpeakingFragment
 * Author: Melanie Muehleder
 * Description: In this class/fragment the user is able to let ones Nao speak.
 */
public class SpeakingFragment extends Fragment {

    private ConnectionService mService;
    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ConnectionService.LocalBinder binder = (ConnectionService.LocalBinder) service;
            mService = binder.getService();
            if (mService != null)
                mBound = true;
            else
                mBound = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_speaking,container,false);

        Button buttonSpeak = (Button) v.findViewById(R.id.speakButton);
        final EditText text = (EditText)v.findViewById(R.id.userInputSpeaking);

        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(text.getText().toString());
            }
        });

        Intent intent = new Intent(this.getContext(), ConnectionService.class);
        if (mBound == false) {
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

        return v;
    }

    @Override
    public void onStop(){
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    private void speak(String text){
        Handler handler = new Handler(Looper.getMainLooper());
        if (text.isEmpty() == false)
        {
            while(mService == null || mService.getSocket() == null){

            }
            mService.connectionRetry();
            while(mService == null || mService.getSocket() == null){

            }

            OutputStreamWriter streamWriter = null;
            try {
                streamWriter = new OutputStreamWriter(mService.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                streamWriter.write("SET Sp");
                streamWriter.flush();
                streamWriter.write(text);
                streamWriter.flush();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Nachricht wurde gesendet...", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Leere Eingabe", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
