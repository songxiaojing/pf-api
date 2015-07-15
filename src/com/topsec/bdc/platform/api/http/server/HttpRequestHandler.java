package com.topsec.bdc.platform.api.http.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.topsec.bdc.platform.api.client.InterfaceParameter;
import com.topsec.bdc.platform.api.http.HttpConstant;
import com.topsec.bdc.platform.api.server.IHttpRequestHandler;


/**
 * @author baiyanwei HTTP request handler.
 */
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {

    private HttpServer _httpServer = null;
    private HttpRequest _request = null;
    private boolean _readingChunks = false;

    private List<Entry<String, String>> _chunkTrailerList = new ArrayList<Entry<String, String>>();
    /** Buffer that stores the response content */
    private final StringBuilder _contentBuffer = new StringBuilder();

    public HttpRequestHandler(HttpServer httpServer) {

        this._httpServer = httpServer;
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

        super.channelDisconnected(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        // package the HTTP basic request.
        if (!_readingChunks) {
            HttpRequest request = this._request = (HttpRequest) e.getMessage();
            // get remote address IP from caller.
            try {
                SocketAddress remoteAddress = ctx.getChannel().getRemoteAddress();
                request.addHeader(InterfaceParameter.HttpHeaderParameter.REMOTE_HOST_ADDR_IP, ((InetSocketAddress) remoteAddress).getAddress().getHostAddress());
            } catch (Throwable t) {
            }
            //
            this._httpServer._requestTotal++;
            //
            if (is100ContinueExpected(request)) {
                send100Continue(e);
            }
            //
            _contentBuffer.setLength(0);
            _chunkTrailerList.clear();
            //
            if (request.isChunked()) {
                _readingChunks = true;
            } else {
                ChannelBuffer content = request.getContent();
                if (content.readable()) {
                    _contentBuffer.append(content.toString(CharsetUtil.UTF_8));
                }
                writeResponse(e);
            }
        } else {
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                _readingChunks = false;
                //
                HttpChunkTrailer trailer = (HttpChunkTrailer) chunk;
                if (trailer.getHeaders().isEmpty() == false) {
                    _chunkTrailerList.addAll(trailer.getHeaders());
                }
                writeResponse(e);
            } else {
                _contentBuffer.append(chunk.getContent().toString(CharsetUtil.UTF_8));
            }
        }
    }

    private void writeResponse(MessageEvent e) {

        //
        Object[] operations = doOperation();
        //
        // Decide whether to close the connection or not.
        // boolean keepAlive = isKeepAlive(request);
        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, (HttpResponseStatus) (operations[0]));
        response.setContent(ChannelBuffers.copiedBuffer((String) (operations[1]), CharsetUtil.UTF_8));
        response.setHeader(CONTENT_TYPE, HttpConstant.RESPONSE_CONTENT_TPYE);
        // if (keepAlive) {
        // Add 'Content-Length' header only for a keep-alive connection.
        response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
        // }

        // Encode the cookie.
        /*
         * String cookieString = request.getHeader(COOKIE); if (cookieString !=
         * null) { CookieDecoder cookieDecoder = new CookieDecoder();
         * Set<Cookie> cookies = cookieDecoder.decode(cookieString); if
         * (!cookies.isEmpty()) { // Reset the cookies if necessary.
         * CookieEncoder cookieEncoder = new CookieEncoder(true); for (Cookie
         * cookie : cookies) { cookieEncoder.addCookie(cookie); }
         * response.addHeader(SET_COOKIE, cookieEncoder.encode()); } }
         */
        // Write the response.
        ChannelFuture future = e.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is
        // done.
        // if (!keepAlive) {
        future.addListener(ChannelFutureListener.CLOSE);
        // }
    }

    /**
     * @return do operation for handler.
     */
    private Object[] doOperation() {

        HttpResponseStatus responseStatus = HttpResponseStatus.OK;
        String content = responseStatus.getReasonPhrase();
        // access path
        String path = _request.getUri();
        if (path == null || path.trim().length() == 0) {
            path = HttpConstant.REQUEST_ROOT_PATH;
        } else if (path.indexOf(HttpConstant.REQUEST_QUERY_SPLITE_CHAR) != -1) {
            path = path.substring(0, path.indexOf(HttpConstant.REQUEST_QUERY_SPLITE_CHAR));
        }
        path = path.trim();
        //
        IHttpRequestHandler handler = (IHttpRequestHandler) this._httpServer.getHandler(path);
        if (handler == null) {
            responseStatus = HttpResponseStatus.NOT_FOUND;
            content = responseStatus.getReasonPhrase();
        } else {
            try {

                Method method = handler.getClass().getMethod(_request.getMethod().getName(), HttpRequest.class, Object.class);
                content = (String) method.invoke(handler, _request, _contentBuffer.toString());
                if (content == null) {
                    content = "";
                }
            } catch (NoSuchMethodException noSuchMethodException) {
                responseStatus = HttpResponseStatus.METHOD_NOT_ALLOWED;
                content = responseStatus.getReasonPhrase();
            } catch (Exception serverException) {
                responseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR;
                content = serverException.toString();
                //
                serverException.printStackTrace();
            }
        }
        return new Object[] { responseStatus, content };
    }

    private void send100Continue(MessageEvent e) {

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
        e.getChannel().write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {

        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
