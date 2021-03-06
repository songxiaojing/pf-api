package com.topsec.bdc.platform.api.client.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.topsec.bdc.platform.api.client.ClientReferent;
import com.topsec.bdc.platform.api.client.IClient;
import com.topsec.bdc.platform.core.exception.PlatformException;
import com.topsec.bdc.platform.core.utils.Assert;


/**
 * 
 * Your class summary,end with '.'.
 * 
 * Your class Detail description,end with '.'.
 * 
 * @title HttpSnoopClient
 * @package com.topsec.bdc.platform.api.http.snoop.client
 * @author baiyanwei
 * @version
 * @date Jul 17, 2015
 * 
 */
public class HttpSnoopClient implements IClient {

    private ClientReferent _httpClientReferent;
    private SslContext sslCtx = null;
    private EventLoopGroup _group = null;
    private Bootstrap _clientBootstrap = null;
    private Channel _channel = null;
    //
    public HashMap<String, String> _headerMap = new HashMap<String, String>();
    private Set<Cookie> _cookies = new HashSet<Cookie>();
    public String _method = "GET";
    public String _content = null;

    public HttpSnoopClient() {

    }

    /**
     * add header value .
     * 
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {

        _headerMap.put(key, value);
    }

    /**
     * set cookies.
     * 
     * @param key
     * @param value
     */
    public void addCookies(String key, String value) {

        _cookies.add(new DefaultCookie(key, value));
    }

    public void setContent(String content) {

        this._content = content;
    }

    public void setHttpMethod(String method) {

        this._method = method;
    }

    public void start() throws Exception {

        // identify the client configuration object
        if (this._httpClientReferent == null) {
            throw new PlatformException("Client clientConfiguration is invalid");
        }
        // if has a host.
        if (Assert.isEmptyString(this._httpClientReferent._serverIpAddress) == true) {
            throw new Exception("invalid _endPointHost.");
        }

        if (this._httpClientReferent._serverPort <= 1024) {
            //throw new PlatformException("HttpClientConfiguration._serverPort is invalid,port should be over 1024.");
        }
        //        if (this._httpClientConfiguration._responseListener == null) {
        //            this._httpClientConfiguration._responseListener = new SimpleResponseListener();
        //        }
        //

        if (_httpClientReferent._enableSSL == true) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.

        try {
            //build HTTP request
            HttpMethod httpMethod = HttpMethod.GET;
            if (Assert.isEmptyString(this._method) == false) {
                httpMethod = HttpMethod.valueOf(this._method.toUpperCase());
            }
            if (Assert.isEmptyString(_content) == true) {
                _content = "";
            }
            // Prepare the HTTP request.
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, "/", Unpooled.copiedBuffer(_content, CharsetUtil.UTF_8));
            request.headers().set(HttpHeaders.Names.HOST, _httpClientReferent._serverIpAddress);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());

            for (Iterator<String> keyIter = this._headerMap.keySet().iterator(); keyIter.hasNext();) {
                String keyName = keyIter.next();
                request.headers().set(keyName, this._headerMap.get(keyName));
            }
            if (_cookies.isEmpty() == false) {
                // Set cookies.
                request.headers().set(HttpHeaders.Names.COOKIE, ClientCookieEncoder.LAX.encode(_cookies));
            }

            //
            _group = new NioEventLoopGroup(_httpClientReferent._eventGroupSize);
            _clientBootstrap = new Bootstrap();
            //
            //
            if (_httpClientReferent._enableTimeout == true) {
                _clientBootstrap.option(ChannelOption.TCP_NODELAY, true);
                _clientBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, _httpClientReferent._connectTimeoutMillis);
            }
            _clientBootstrap.group(_group);
            _clientBootstrap.channel(NioSocketChannel.class);
            _clientBootstrap.handler(new HttpSnoopClientInitializer(sslCtx, _httpClientReferent));

            // Make the connection attempt.
            _channel = _clientBootstrap.connect(this._httpClientReferent._serverIpAddress, _httpClientReferent._serverPort).sync().channel();
            Thread.sleep(10000L);
            // Send the HTTP request.
            _channel.writeAndFlush(request);

            // Wait for the server to close the connection.
            _channel.closeFuture().sync();
        } catch (Throwable t) {
            // Shut down executor threads to exit.
            t.printStackTrace();

        } finally {
            if (_group != null) {
                _group.shutdownGracefully();
            }
        }
    }

    @Override
    public void stop() throws Exception {

        if (_group != null) {
            _group.shutdownGracefully();
        }

    }

    @Override
    public void setClientReferent(ClientReferent clientReferent) {

        this._httpClientReferent = clientReferent;
    }

    @Override
    public ClientReferent getClientReferent() {

        return this._httpClientReferent;
    }
}
