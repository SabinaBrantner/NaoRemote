package com.nao.sabina.projectnao;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

/**
 * Created by Sabina on 13.06.2016.
 */
public class ConnectionService extends Service{

    private static InetAddress ip;
    private static int port = 9999;
    private static Socket clientSocket;

    private final IBinder myBinder = new LocalBinder();

    public ConnectionService(){
        ip = null;
    }

    public class LocalBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    class ClientThread implements Runnable{
        public void run(){
            try {
                clientSocket = new Socket(ip, port);
                clientSocket.setKeepAlive(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        if (intent != null) {
            super.onStartCommand(intent, flags, startId);
        }
        else
            onDestroy();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        try {
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void setIp(InetAddress vIp){
        ip = vIp;
        if (ip != null){
            Runnable client = new ClientThread();
            Thread t = new Thread(client);
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        }
    }

    public Socket getSocket()  {
        if (clientSocket == null || clientSocket.isConnected() == false)
        {
            return null;
        }

        return clientSocket;
    }

    public void connectionRetry(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setIp(ip);
    }
}
