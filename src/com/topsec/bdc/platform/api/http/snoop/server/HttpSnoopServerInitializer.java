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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;


/**
 * 
 * Your class summary,end with '.'.
 * 
 * Your class Detail description,end with '.'.
 * 
 * @title HttpSnoopServerInitializer
 * @package com.topsec.bdc.platform.api.http.snoop.server
 * @author baiyanwei
 * @version
 * @date Jul 15, 2015
 * 
 */
public class HttpSnoopServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext _sslCtx;
    private final HttpServerConfiguration _serverConfig;

    public HttpSnoopServerInitializer(SslContext sslCtx, HttpServerConfiguration serverConfig) {

        this._sslCtx = sslCtx;
        this._serverConfig = serverConfig;
    }

    @Override
    public void initChannel(SocketChannel ch) {

        ChannelPipeline p = ch.pipeline();
        if (_sslCtx != null) {
            p.addLast(_sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        p.addLast(new HttpContentCompressor());
        //
        p.addLast(new HttpSnoopServerHandler(_serverConfig));
        //

    }
}
