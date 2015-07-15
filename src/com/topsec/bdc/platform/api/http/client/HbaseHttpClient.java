package com.topsec.bdc.platform.api.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;

import com.topsec.bdc.platform.api.client.ClientConfiguration;
import com.topsec.bdc.platform.api.client.IClient;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * Client description
 * 
 * @author baiyanwei Jul 8, 2013
 * 
 */
public abstract class HbaseHttpClient implements IClient {

    public final static String READ_TIME_OUT_PIPE_LINE = "readTimeout";
    public final static String WRITE_TIME_OUT_PIPE_LINE = "writeTimeout";
    public final static String NETWORK_ERROR_CONNECTION_REFUSED = "Connection refused";
    //
    final private static PlatformLogger theLogger = PlatformLogger.getLogger(HbaseHttpClient.class);
    // client configuration ,client target ,port ,request.
    protected ClientConfiguration _clientConfiguration = null;
    // server IP address
    protected InetSocketAddress _serverAddress = null;
    //protected ChannelFactory _factory = null;
    //protected ChannelPipelineFactory _pipelineFactory = null;
    protected ChannelHandler _handler = null;
    protected ChannelFuture _future = null;
    protected Channel _channel = null;
    //
    EventLoopGroup _group = null;
    Bootstrap _bootstrap = null;
    ChannelInitializer _channelInitializer = null;
    SocketChannel _socketChannel = null;
    // request timing.
    protected long _keepTimeing = 0;

    @Override
    public void configure(ClientConfiguration config) {

        _clientConfiguration = config;
    }

    @Override
    public ClientConfiguration getConfiguration() {

        return _clientConfiguration;
    }

    @Override
    public String getId() {

        return _clientConfiguration._id;
    }

    @Override
    public void stop() {

        // This will close the socket. This happens asynchronously.
        if (_channel != null) {
            ChannelFuture future = _channel.close();
            future.addListener(new ChannelFutureListener() {

                // register the listener for closed the channel.
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                    if (future.isDone() == true) {
                        // check timeout handle exist or not
                        shutDownTimeoutTimer();
                        // release external resources
                        //if (_bootstrap != null) {
                        // Shut down executor threads to exit.
                        // _bootstrap.releaseExternalResources();
                        //}
                        _group.shutdownGracefully();
                        // This will stop the timeout handler

                        //_factory = null;
                        _group = null;
                        _bootstrap = null;
                    }
                }

            });

        }

        theLogger.debug("clientClosed", _clientConfiguration._endPointHost, _clientConfiguration._endPointPort);
    }

    /**
     * shut down the timeout timer
     * 
     * @throws Exception
     */
    protected void shutDownTimeoutTimer() throws Exception {

        // calculate the request using timing.
        /*
        _keepTimeing = System.currentTimeMillis() - _keepTimeing;
        if (_bootstrap != null && _bootstrap.getPipelineFactory() != null && _bootstrap.getPipelineFactory().getPipeline() != null) {
            if (_bootstrap.getPipelineFactory().getPipeline().get(READ_TIME_OUT_PIPE_LINE) != null) {
                ReadTimeoutHandler readTimerHandler = (ReadTimeoutHandler) _bootstrap.getPipelineFactory().getPipeline().remove(READ_TIME_OUT_PIPE_LINE);
                // stop the read timer
                readTimerHandler.releaseExternalResources();
            }
            if (_bootstrap.getPipelineFactory().getPipeline().get(WRITE_TIME_OUT_PIPE_LINE) != null) {
                WriteTimeoutHandler writeTimerHandler = (WriteTimeoutHandler) _bootstrap.getPipelineFactory().getPipeline().remove(WRITE_TIME_OUT_PIPE_LINE);
                // stop the write timer
                writeTimerHandler.releaseExternalResources();
            }
        }
        */
    }
}
