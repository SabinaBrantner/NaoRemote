package fragments;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nao.sabina.projectnao.ConnectionManager;
import com.nao.sabina.projectnao.FileManager;
import com.nao.sabina.projectnao.NetworkChecker;
import com.nao.sabina.projectnao.R;

import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ConnectWithNaoFragment
 * Author: Sabina Brantner
 * Description: In this class/fragment the user is able to connect with ones Nao. Moreover, there
 * is also a help button which describes how to connect.
 */
public class ConnectWithNaoFragment extends Fragment {

    private boolean connectedWithWifi = false;
    private ConnectionManager connectionManager;
    private static Socket socketCon;
    private static FileManager fileManager;
    private static ConnectionManager conectionManager;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_connect_with_nao,container,false);
        Button buttonHelp = (Button) v.findViewById(R.id.helpConnectionButton);
        Button buttonConnect = (Button) v.findViewById(R.id.connectButton);

        final EditText ipText = (EditText)v.findViewById(R.id.ipOfNaoView);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetworkInformation();
                if(connectedWithWifi) {
                    checkUserInput(v, ipText.getText().toString());
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

    public void showConnectionHelp(View view) {
        Intent intent = new Intent(this.getActivity(), ConnectionHelpFragment.class);
        startActivity(intent);
    }

    public void setFileManager(FileManager fM){
        fileManager = fM;
    }
    public void setConnectionManager(ConnectionManager cM){conectionManager = cM;}
    public ConnectionManager getConnectionManager(){return connectionManager;}

    private void checkUserInput(View view, String ipAdress){
        String message = "";

        if(ipAdress.isEmpty()){
            Toast.makeText(getContext(), "Please enter a IP-Address!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(isStringIPAddress(ipAdress)){
                if(checkIpExists(ipAdress)){
                    connectionManager = new ConnectionManager(ipAdress);
                    try {
                        connectionManager.get(1000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    connectionManager.execute();
                    message = String.format("Verbindung wird aufgebaut");
                }
            }
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void getNetworkInformation(){
        String message = "";
        boolean wifiEnabled = false;

        NetworkChecker networkInformation = new NetworkChecker();
        wifiEnabled = networkInformation.checkWifiEnabled(getContext());

        if(wifiEnabled){
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