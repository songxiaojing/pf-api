package com.topsec.bdc.platform.api.client.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import com.topsec.bdc.platform.api.client.ClientReferent;


/**
 * 
 * Your class summary,end with '.'.
 * 
 * Your class Detail description,end with '.'.
 * 
 * @title HttpSnoopClientInitializer
 * @package com.topsec.bdc.platform.api.http.snoop.client
 * @author baiyanwei
 * @version
 * @date Jul 17, 2015
 * 
 */
public class HttpSnoopClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    private final ClientReferent _clientReferent;

    public HttpSnoopClientInitializer(SslContext sslCtx, ClientReferent httpClientConfiguration) {

        this.sslCtx = sslCtx;
        this._clientReferent = httpClientConfiguration;
    }

    @Override
    public void initChannel(SocketChannel ch) {

        ChannelPipeline p = ch.pipeline();

        // Enable HTTPS if necessary.
        if (sslCtx != null) {
            p.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //timeout
        if (_clientReferent._enableTimeout) {
            p.addLast("readTimeoutHandler", new ReadTimeoutHandler(_clientReferent._readTimeoutSecond));
            p.addLast("writeTimeoutHandler", new WriteTimeoutHandler(_clientReferent._writeTimeoutSecond));
        }
        //
        p.addLast(new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        p.addLast(new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpContents.
        //p.addLast(new HttpObjectAggregator(1048576));

        p.addLast(new HttpSnoopClientHandler(_clientReferent));
    }
}
