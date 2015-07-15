package com.topsec.bdc.platform.api.http.server;

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.byw.dalek.platform.api.client.Client;

/**
 * @author Martin Bai.
 * 
 *         Jun 20, 2012
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory {

	private final static HashedWheelTimer _readTimer = new HashedWheelTimer();;
	private final static HashedWheelTimer _writeTimer = new HashedWheelTimer();;

	private int _readTimeoutSeconds = 30;
	private int _writeTimeoutSeconds = 30;

	private HttpServer httpServer = null;

	public HttpServerPipelineFactory(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		// Enable Reading and writing timeOut.
		if (_readTimeoutSeconds > 0) {
			// 1 HTTP response timeOut timer.
			pipeline.addFirst(HbaseHttpClient.READ_TIME_OUT_PIPE_LINE, new ReadTimeoutHandler(_readTimer, _readTimeoutSeconds));
		}
		if (_writeTimeoutSeconds > 0) {
			// 2 HTTP request timeOut timer.
			pipeline.addFirst(HbaseHttpClient.WRITE_TIME_OUT_PIPE_LINE, new WriteTimeoutHandler(_writeTimer, _writeTimeoutSeconds));
		}
		// 1 HTTP request decoder
		pipeline.addLast("decoder", new HttpRequestDecoder());
		// 2 HTTP response Encode
		pipeline.addLast("encoder", new HttpResponseEncoder());
		// 3 automatic content compression
		pipeline.addLast("deflater", new HttpContentCompressor(1));
		// 4 automatic content decompression.
		pipeline.addLast("inflater", new HttpContentDecompressor());
		// 5 HTTP handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		// 6 business operation handler
		pipeline.addLast("handler", new HttpRequestHandler(this.httpServer));
		return pipeline;
	}
}
