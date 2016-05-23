/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nao.client.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sabina
 */
public class FileClient implements Runnable {
    private String path;
    private OutputStreamWriter streamWriter;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private final InetAddress ip;
    private final int port;

    FileClient(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    @SuppressWarnings("SleepWhileInLoop")
    public boolean connect() {
        boolean suc = false;
        try {
            if (ip.isReachable(port)) {
                while (!suc) {
                    try {
                        socket = new Socket(ip, port);
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        streamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                        System.out.println("[Client]Connected to Server, Host: " + ip + ", Port: " + port);
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

    public List<String> readFile() {
        BufferedReader in = null;
        List<String> inputList = new ArrayList<String>();

        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(this.path), "UTF8"));
            
            String input = in.readLine();
            while(input != null){
                inputList.add(input);
                input = in.readLine();
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return inputList;
    }

    public boolean sendFileToServer(List<String> fileLines) {
        boolean suc = false;
        if (fileLines != null) {
            try {
                for (int i = 0; i < fileLines.size(); i++) {
                    streamWriter.write(fileLines.get(i) + "\n");
                    System.out.println(fileLines.get(i));
                }
                streamWriter.flush();
                System.out.println("Daten wurden erfolgreich gesendet");
                suc = true;
            } catch (IOException ex) {
                System.out.println("Beim Senden ist ein Fehler aufgetreten");
                return suc;
            }
        }
        return suc;
    }

    public void closeAll() {
        try {
            inputStream.close();
            streamWriter.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Schließen fehlgeschlagen");
        }
    }

    @Override
    public void run() {
        if (connect()) {
            sendFileToServer(readFile());
        }
    }
}
