package com.topsec.bdc.platform.api.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.net.UnknownHostException;


public class IpAddress {

    public static void main(String[] args) throws UnknownHostException {

        // TODO Auto-generated method stub
        //        String host = "127.0.0.1";
        //        InetSocketAddress inetAddress = new InetSocketAddress(InetAddress.getByName(host), 8000);
        //        System.out.println(inetAddress);

        ByteBuf requestBodyBuf = ByteBufAllocator.DEFAULT.heapBuffer(10);
        for (int i = 0; i < 20000; i++) {

            requestBodyBuf.writeByte((byte) 1);

        }

        System.out.println(requestBodyBuf.capacity());
        System.out.println(requestBodyBuf.readableBytes());
        System.out.println(requestBodyBuf.arrayOffset());
        System.out.println(requestBodyBuf.toString());

    }

}
