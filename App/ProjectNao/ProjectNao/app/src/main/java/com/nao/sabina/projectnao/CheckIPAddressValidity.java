package com.nao.sabina.projectnao;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Melanie on 11.05.16.
 */
public class CheckIPAddressValidity {

    private String host;

    private boolean isReachable = false;
    private InetAddress ip = null;
    final private int timeout = 5000;

    public CheckIPAddressValidity(String host){
        this.host = host;
    }

    public boolean checkIpExists(){
        try {
            ip = InetAddress.getByName(this.host);
        } catch (UnknownHostException e) {
            isReachable = false;
        }

        try {
            if(ip.isReachable(timeout)){
                isReachable = true;
            }
        } catch (IOException e) {
            isReachable = false;
        }
        return isReachable;
    }
}
