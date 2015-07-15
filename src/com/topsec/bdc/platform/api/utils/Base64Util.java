package com.topsec.bdc.platform.api.utils;

import io.netty.channel.Channel;

import java.nio.channels.Channels;

import com.topsec.bdc.platform.core.utils.Constants;


public class Base64Util {

    /**
     * encode into base64 String
     * 
     * @param digest
     * @return
     */
    public static String encdoeToBase64(byte[] digest) {

        Channel hashChannelBuffer = Channels.buffer(digest.length);
        hashChannelBuffer.writeBytes(digest);
        Channel b64 = io.netty.handler.codec.base64.Base64.encode(hashChannelBuffer);
        return b64.toString(Constants.DEFAULT_CHARSET);
    }
}
