package com.nao.sabina.projectnao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabina on 29.05.2016.
 */

public class FileManager{
    private int port;
    private InetAddress ip;
    private String filePath;
    private Socket socketCon;
    private OutputStreamWriter streamWriter;

    public int getPort(){
        return this.port;
    }

    public InetAddress getIp(){
        return this.ip;
    }

    public void setPort(int port){
        //TODO: check Port validity before
        this.port = port;
    }

    public void setIp(InetAddress ip){
        //TODo: check Ip validity before
        this.ip = ip;
    }

    public FileManager(Socket socket, String path){
        this.filePath = path;
        this.socketCon = socket;
        if (socket != null){
            this.ip = socket.getInetAddress();
            this.port = socket.getPort();
        }
    }

    public FileManager(InetAddress ip, int port, String path) throws Exception {
        //TODO: check port and ip validity before
        this.ip = ip;
        this.port = port;
        this.filePath = path;
        this.socketCon = null;
    }

    public void openSocketConnection() {
        boolean suc = false;
        try {
            if (ip.isReachable(port)) {
                while (!suc) {
                    try {
                        if (this.socketCon == null) {
                            this.socketCon = new Socket(this.ip, this.port);
                        }
                        if (this.socketCon.isConnected() == false || this.socketCon.isClosed())
                            this.socketCon.connect(this.socketCon.getRemoteSocketAddress());
                        System.out.println("Connected to Nao, Host: " + this.ip + ", Port: " + this.port);
                        suc = true;
                    } catch (Exception e) {
                        try {
                            System.out.println("Next connection try in 1 sec");
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            System.out.println("Verbinden Fehlgeschlagen");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Ip ist nicht mehr erreichbar");
        }
    }

    public void writeFile(String path){
        try {
            writeFile(readFile(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(List<String> fileLines){
        if (fileLines != null) {
            try {
                if (streamWriter == null)
                    streamWriter = new OutputStreamWriter(this.socketCon.getOutputStream(), StandardCharsets.UTF_8);
                for (int i = 0; i < fileLines.size(); i++) {
                    streamWriter.write(fileLines.get(i) + "\n");
                    System.out.println(fileLines.get(i));
                }
                streamWriter.flush();
                System.out.println("Daten wurden erfolgreich gesendet");
            } catch (IOException ex) {
                System.out.println("Beim Senden ist ein Fehler aufgetreten");
            }
        }
    }

    public List<String> readFile(String path) throws IOException {
        this.filePath = path;
        return readFile();
    }

    public List<String> readFile() throws IOException {
        BufferedReader in = null;
        List<String> inputList = new ArrayList<String>();
        in = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath), "UTF8"));

        String input = in.readLine();

        while(input != null){
            inputList.add(input);
            input = in.readLine();
        }
        return inputList;
    }

    public void closeAll() {
        try {
            this.streamWriter.flush();
            this.streamWriter.close();
            this.socketCon.close();
        } catch (IOException ex) {
            System.out.println("Beim Schließen der Verbindungen ist etwas schiefgelaufen");
        }
    }
}
