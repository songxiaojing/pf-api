/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.topsec.bdc.platform.api.http.snoop.server;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;


public class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {

    //private HttpRequest _request = null;
    /** Buffer that stores the response content */
    private final StringBuilder _requestBodyBuf = new StringBuilder();

    private final HttpServerConfiguration _serverConfig;

    public HttpSnoopServerHandler(HttpServerConfiguration serverConfig) {

        this._serverConfig = serverConfig;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof HttpRequest) {
            System.out.println("handler:" + this.hashCode());
            //HttpRequest request = this._request = (HttpRequest) msg;
            HttpRequest request = (HttpRequest) msg;
            //
            this._serverConfig._requestListener.setHttpHeader(request.headers());
            this._serverConfig._requestListener.setHttpParameter(new QueryStringDecoder(request.getUri()).parameters());
            _requestBodyBuf.setLength(0);

        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                _requestBodyBuf.append(content.toString(CharsetUtil.UTF_8));
                //content.release();
            }

            if (msg instanceof LastHttpContent) {
                //
                LastHttpContent trailer = (LastHttpContent) msg;
                //
                String responseContent = null;
                //
                HttpResponseStatus responseStatus = null;
                //fill response
                if (trailer.getDecoderResult().isSuccess() == false) {
                    responseStatus = HttpResponseStatus.BAD_REQUEST;
                    responseContent = HttpResponseStatus.BAD_REQUEST.reasonPhrase();
                } else {
                    try {
                        responseStatus = HttpResponseStatus.OK;
                        responseContent = this._serverConfig._requestListener.fireSucceed(_requestBodyBuf.toString());
                    } catch (Throwable e) {
                        e.printStackTrace();
                        responseStatus = HttpResponseStatus.SERVICE_UNAVAILABLE;
                        responseContent = e.toString();
                    }
                }

                try {
                    //send back response and close connection
                    writeResponseAndClose(ctx, responseStatus, responseContent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    _requestBodyBuf.setLength(0);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();
        try {
            this._serverConfig._requestListener.fireError(cause);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        //
        writeResponseAndClose(ctx, HttpResponseStatus.SERVICE_UNAVAILABLE, cause.toString());
    }

    /**
     * send back response and close the connection in server side.
     * 
     * @param ctx
     * @param responseStatus
     * @param responseContent
     */
    private void writeResponseAndClose(ChannelHandlerContext ctx, HttpResponseStatus responseStatus, String responseContent) {

        ByteBuf responseContentBuf = Unpooled.copiedBuffer(responseContent, CharsetUtil.UTF_8);
        // Decide whether to close the connection or not.
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, responseStatus, responseContentBuf);
        //
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        //disallow keepAlive
        //boolean keepAlive = HttpHeaders.isKeepAlive(_request);
        //response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        // Add 'Content-Length' header only for a keep-alive connection.
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        // Add keep alive header as per:
        // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
        response.headers().set(CONNECTION, HttpHeaders.Values.CLOSE);
        /*
        // Encode the cookie.
        String cookieString = _request.headers().get(COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = CookieDecoder.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                for (Cookie cookie : cookies) {
                    response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
                }
            }
        } else {
            // Browser sent no cookie.  Add some.
            response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key1", "value1"));
            response.headers().add(SET_COOKIE, ServerCookieEncoder.encode("key2", "value2"));
        }
        */
        try {
            // Write the response.
            ctx.write(response);
            // close connection
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        } catch (Throwable t) {
            t.printStackTrace();
            ctx.close();
        }
    }
}
