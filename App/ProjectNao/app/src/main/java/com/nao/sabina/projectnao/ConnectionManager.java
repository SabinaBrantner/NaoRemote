package com.nao.sabina.projectnao;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Melanie on 11.05.16.
 * Description: This class checks the Validity of the entered IP-Address.
 */
public class ConnectionManager extends AsyncTask<Socket, Void, String> {

    private String host;
    private static int port = 9999;

    private Socket socketCon;

    public ConnectionManager(String host) {
        this.host = host;
    }

    public Socket getSocketCon(){
        return this.socketCon;
    }

    public boolean isConnected(){
        if (this.socketCon == null)
            return false;
        return true;
    }

    public boolean existsConnection(){
        if (this.socketCon == null)
            return false;
        return true;
    }

    protected String doInBackground(Socket... params) {
        try {
            socketCon = new Socket();
            socketCon.connect(new InetSocketAddress(InetAddress.getByName(host), port), 9000);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "not Done";
        }
        return "Done";
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("\n\n\n\n"+  result + "\n\n\n\n");
    }
}