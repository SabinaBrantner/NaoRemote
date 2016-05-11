/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nao.client.socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sabina
 */
public class FileClient implements Runnable{

    private String path;
    private DataOutputStream dataOutputStream;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private final InetAddress ip;
    private final int port;

    FileClient(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    public void setPath(String path){
        this.path = path;
    }
    
    public String getPath(){
        return this.path;
    }
    
    @SuppressWarnings("SleepWhileInLoop")
    public boolean connect(){
        boolean suc = false;
        try {
            if (ip.isReachable(port)) {
                while (!suc) {
                    try {
                        socket = new Socket(ip, port);
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        dataOutputStream = new DataOutputStream(outputStream);
                        System.out.println("[Client]Connected to Server, Host: " + ip + ", Port: " + port);
 //                       BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
 //                       System.out.println(reader.read());
                        suc = true;
                    } catch (Exception e) {
                        try {
                            System.out.println("[Client] Next connection in 1 sec");
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            System.out.println("Verbinden Fehlgeschlagen");
                        }
                    }
                }
            }
        } catch (IOException ex) {
           System.out.println("Ip ist nicht erreichbar");
        }
        return suc;
    }
    
    public byte[] readFileToByteArray(){
        byte[] input = null;
        File file = new File(path);
        if (file.exists()) {
            try {
                input = Files.readAllBytes(file.toPath());
            } catch (IOException ex) {
                Logger.getLogger(NaoClientSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        return input;
    }
    
    public boolean sendBytesToServer(byte[] bytes){
        boolean suc = false;
        if (bytes != null) {
            try {
                dataOutputStream.write(bytes,0,bytes.length);
                System.out.println("Daten wurden erfolgreich gesendet");
                suc = true;
            } catch (IOException ex) {
                System.out.println("Beim Senden ist ein Fehler aufgetreten");
                return suc;
            }
        }
        return suc;
    }
    
    public void closeAll(){
        try {
            inputStream.close();
            dataOutputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Schließen fehlgeschlagen");
        }
    }

    @Override
    public void run() {
        if (connect()) {
            sendBytesToServer(readFileToByteArray());
        }
    }
}
