package com.nao.sabina.projectnao;

import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
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
    private BufferedReader bufferedReader = null;
    private Socket socketCon;

    public FileManager(Socket socket){
        this.socketCon = socket;
        if (socket != null){
            this.ip = socket.getInetAddress();
            this.port = socket.getPort();
        }
    }

    public void writeFile(String path, Socket socket){
        try {
            this.socketCon = socket;
            writeFile(readFile(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(List<String> fileLines){
        if (fileLines != null) {
            try {
                if (this.socketCon.isClosed())
                    this.socketCon.connect(this.socketCon.getRemoteSocketAddress());
                OutputStreamWriter streamWriter = new OutputStreamWriter(this.socketCon.getOutputStream(), StandardCharsets.UTF_8);
                streamWriter.write("SET Ac");
                for (int i = 0; i < fileLines.size(); i++) {
                    streamWriter.write(fileLines.get(i) + "\n");
                    System.out.println(fileLines.get(i));
                }
                streamWriter.flush();
                streamWriter.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<String> readFile(String path) throws IOException {
        this.filePath = path;
        return readFile();
    }

    public List<String> readFile() throws IOException {
        List<String> inputList = new ArrayList<>();
        String input = bufferedReader.readLine();
        while(input != null){
            inputList.add(input);
            input = bufferedReader.readLine();
        }
        return inputList;
    }

    public void setBufferedReader(BufferedReader br){
        this.bufferedReader = br;
    }
}
