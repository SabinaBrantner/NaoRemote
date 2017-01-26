package fragments;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nao.sabina.projectnao.ConnectionService;
import com.nao.sabina.projectnao.NetworkChecker;
import com.nao.sabina.projectnao.R;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ConnectWithNaoFragment
 * Author: Sabina Brantner
 * Description: In this class/fragment the user is able to connect with ones Nao. Moreover, there
 * is also a help button which describes how to connect.
 */
public class ConnectWithNaoFragment extends Fragment {

    private boolean connectedWithWifi = false;
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
        View v = inflater.inflate(R.layout.fragment_connect_with_nao,container,false);
        Button buttonHelp = (Button) v.findViewById(R.id.helpConnectionButton);
        Button buttonConnect = (Button) v.findViewById(R.id.connectButton);

        final EditText ipText = (EditText)v.findViewById(R.id.ipOfNaoView);

        Intent intent = new Intent(getContext(), ConnectionService.class);
        if (mBound == false)
            getActivity().bindService(intent, mConnection, Context.BIND_IMPORTANT);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetworkInformation();
                if(connectedWithWifi) {
                    if(mBound && mService.getSocket() != null){
                        Toast.makeText(getContext(), "Mit Nao verbunden", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        checkUserInput(v, ipText.getText().toString());
                    }

                }
            }
        });

        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConnectionHelp(v);
            }
        });

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

    public void showConnectionHelp(View view) {
        Intent intent = new Intent(this.getActivity(), ConnectionHelpFragment.class);
        startActivity(intent);
    }

    private void checkUserInput(View view, String ipAdress){
        if(ipAdress.isEmpty()){
            Toast.makeText(getContext(), "Es wurde keine IP eingegeben", Toast.LENGTH_SHORT).show();
        }
        else if (!isStringIPAddress(ipAdress))
            Toast.makeText(getContext(),"Keine gültige IP", Toast.LENGTH_SHORT).show();
        else if (!checkIpExists(ipAdress))
            Toast.makeText(getContext(),"Keine gültige IP", Toast.LENGTH_SHORT).show();
        else{
            if (mBound == false) {
                Intent intent = new Intent(this.getContext(), ConnectionService.class);
                getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
            try {
                Toast.makeText(getContext(), "Verbindung wird aufgebaut", Toast.LENGTH_SHORT).show();
                mService.setIp(InetAddress.getByName(ipAdress));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    private void getNetworkInformation(){
        NetworkChecker networkInformation = new NetworkChecker();

        if(networkInformation.checkWifiEnabled(getContext())){
            connectedWithWifi = networkInformation.checkWifiConnectivity(getContext());
            if(!connectedWithWifi){
                Toast.makeText(getContext(), "Please connect to your Wifi", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), "Please turn on your Wifi", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isStringIPAddress(String host) {
        try {
            InetAddress ip = InetAddress.getByName(host);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean checkIpExists(String host) {
        try {
            Process processReachability = java.lang.Runtime.getRuntime().exec("ping -c 1 " + host);
            int value = processReachability.waitFor();
            if (value != 0)
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}