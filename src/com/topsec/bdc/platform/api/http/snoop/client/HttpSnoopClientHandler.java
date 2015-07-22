package com.topsec.bdc.platform.api.http.snoop.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

import com.topsec.bdc.platform.api.client.HttpClientConfiguration;
import com.topsec.bdc.platform.core.exception.PlatformException;


public class HttpSnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final StringBuilder _responseBodyBuf = new StringBuilder();

    private final HttpClientConfiguration _httpClientConfiguration;

    public HttpSnoopClientHandler(HttpClientConfiguration httpClientConfiguration) {

        _httpClientConfiguration = httpClientConfiguration;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {

        if (msg instanceof HttpResponse) {
            //
            _responseBodyBuf.setLength(0);
            //
            HttpResponse response = (HttpResponse) msg;
            HttpResponseStatus status = response.getStatus();
            //
            if (status.code() < 200 || status.code() >= 300) {
                fireError(new PlatformException("HTTP Response Code Exception, The Response code is " + status.code()));
                ctx.close();
                return;
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            //if (content.content().isReadable() == true) {
            _responseBodyBuf.append(content.content().toString(CharsetUtil.UTF_8));
            //}

            if (content instanceof LastHttpContent) {
                fireSucceed(_responseBodyBuf.toString());
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        fireError(new PlatformException("exceptionCaught", cause));
        ctx.close();
    }

    /**
     * @param exception
     */
    private void fireError(PlatformException exception) {

        if (_httpClientConfiguration._clientResponseListener != null) {
            try {
                _httpClientConfiguration._clientResponseListener.fireError(exception);
            } catch (PlatformException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param content
     */
    private void fireSucceed(String content) {

        if (_httpClientConfiguration._clientResponseListener != null) {
            try {
                _httpClientConfiguration._clientResponseListener.fireSucceed(content);
            } catch (PlatformException e) {
                e.printStackTrace();
            }
        }
    }
}
