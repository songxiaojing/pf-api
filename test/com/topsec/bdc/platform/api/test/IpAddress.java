package com.topsec.bdc.platform.api.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class IpAddress {

    public static void main(String[] args) throws UnknownHostException {

        // TODO Auto-generated method stub
        String host = "127.0.0.1";
        InetSocketAddress inetAddress = new InetSocketAddress(InetAddress.getByName(host), 8000);
        System.out.println(inetAddress);
    }

}
