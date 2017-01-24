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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nao.sabina.projectnao.ConnectionService;
import com.nao.sabina.projectnao.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * NaoInfoFragment
 * Author: Sabina Brantner
 * Description: This Fragment show all the infos about a Nao(f.i. Battery  status, temperature)
 */
public class NaoInfoFragment extends Fragment{

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nao_info,container,false);
        ImageView imageViewBattery = ((ImageView) v.findViewById(R.id.imageBattery));
        Picasso.with(getContext()).load(R.drawable.full_battery).fit().centerInside().into(imageViewBattery);
        imageViewBattery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService == null || mService.getSocket() == null){
                    Toast.makeText(getContext(), "Verbinden Sie sich mit dem Nao", Toast.LENGTH_SHORT).show();
                }
                else{
                    getBatteryInformation();
                }
            }
        });

        ImageView imageViewTemperature = ((ImageView) v.findViewById(R.id.temperatureImage));
        Picasso.with(getContext()).load(R.drawable.temperature).fit().centerInside().into(imageViewTemperature);
        imageViewTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService == null || mService.getSocket() == null){
                    Toast.makeText(getContext(), "Verbinden Sie sich mit dem Nao", Toast.LENGTH_SHORT).show();
                }
                else{
                    getTemperatureInformation();
                }
            }
        });

        Intent intent = new Intent(this.getContext(), ConnectionService.class);
        if (mBound == false) {
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        return v;
    }

    @Override
    public void onStart(){
        NameManager nameManager = new NameManager();
        new Thread(nameManager).start();
        super.onStart();
    }
    @Override
    public void onStop(){
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    public void getBatteryInformation(){
        if (mService.getSocket().isClosed())
            mService.setIp(mService.getSocket().getInetAddress());
        BatteryManager manager = new BatteryManager();
        new Thread(manager).start();
    }

    public void getTemperatureInformation(){

    }

    public class BatteryManager implements Runnable{
        @Override
        public void run(){
            try {
                mService.connectionRetry();
                while(mService == null || mService.getSocket() == null){

                }
                OutputStreamWriter streamWriter = new OutputStreamWriter(mService.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                streamWriter.write("GET Ba");
                streamWriter.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mService.getSocket().getInputStream(), StandardCharsets.UTF_8));
                final String input = bufferedReader.readLine();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), input + "%", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();}
        }
    }

    public class NameManager implements Runnable{
        @Override
        public void run(){
            try {
                while(mService == null || mService.getSocket() == null){

                }
                mService.connectionRetry();
                while(mService.getSocket() == null || mService.getSocket().isClosed()){

                }
                OutputStreamWriter streamWriter = new OutputStreamWriter(mService.getSocket().getOutputStream(), StandardCharsets.UTF_8);
                streamWriter.write("GET Na");
                streamWriter.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mService.getSocket().getInputStream(), StandardCharsets.UTF_8));
                final String input = bufferedReader.readLine();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        View v = getActivity().findViewById(R.id.helloIm);
                        ((TextView)v).setText("Hello I'm " + input);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
