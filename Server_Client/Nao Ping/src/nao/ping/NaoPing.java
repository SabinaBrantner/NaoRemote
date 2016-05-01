/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nao.ping;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author Sabina
 */
public class NaoPing {

    /**
     * @param args the command line arguments
     */
    //192.168.0.142 9559
    private static String path = "C:\\Users\\Sabina\\Downloads\\behavior.xar";
    
    public static void main(String[] args) throws UnknownHostException{
        int port = 8081;
        InetAddress ip = InetAddress.getByName("192.168.45.50");
        FileClient client = new FileClient(ip, port);
        client.setPath(path);
        Thread thread = new Thread(client);
        thread.run();
        client.closeAll();
    }
}
