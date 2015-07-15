package com.topsec.bdc.platform.api.http.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.byw.dalek.platform.api.client.Client;
import com.byw.dalek.platform.core.exception.PlatformException;
import com.byw.dalek.platform.core.utils.Assert;
import com.byw.platform.log.utils.PlatformLogger;

/**
 * @author baiyanwei
 * 
 *         Jan 1, 2014
 * 
 */
public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
	private static final PlatformLogger theLogger = PlatformLogger.getLogger(HttpResponseHandler.class);
	protected HbaseHttpClient _client = null;
	private boolean _readingChunks = false;
	private final StringBuffer _chucksContent = new StringBuffer();
	private final static long maxResponseSize = 20000;

	public HttpResponseHandler(HbaseHttpClient client) {
		_client = client;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// 200<=response.code<300, size<maxResponseSize,
		//
		if (!_readingChunks) {
			HttpResponse response = (HttpResponse) e.getMessage();
			// adjust the response code ,if 20x is OK.
			HttpResponseStatus status = response.getStatus();
			if (status.getCode() < 200 || status.getCode() >= 300) {
				fireError(new PlatformException("HTTP Response Code Exception, The Response code is " + status.getCode()));
				ctx.getChannel().close();
				return;
			}
			// if content length is over maxResponseSize
			String contentLength = response.getHeader(HttpHeaders.Names.CONTENT_LENGTH);
			if (Assert.isEmptyString(contentLength) == false) {
				long contentLengthVal = 0;
				try {
					contentLengthVal = Long.parseLong(contentLength);
				} catch (NumberFormatException numberFormatException) {
					theLogger.exception(numberFormatException);
				}
				if (contentLengthVal > maxResponseSize) {
					fireError(new PlatformException("Too Many Content on Response Body ,over " + maxResponseSize));
					ctx.getChannel().close();
					return;
				}
			}
			if (response.isChunked()) {
				_chucksContent.setLength(0);
				_readingChunks = true;
			} else {
				ChannelBuffer content = response.getContent();
				if (content.readable()) {
					fireSucceed(content.toString(CharsetUtil.UTF_8));
				}
				ctx.getChannel().close();
			}
		} else {
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				_readingChunks = false;
				fireSucceed(_chucksContent.toString());
				ctx.getChannel().close();
			} else {
				if (_chucksContent.length() > maxResponseSize) {
					fireError(new PlatformException("Too Many Content on Response Body ,over " + maxResponseSize));
					ctx.getChannel().close();
					return;
				}
				_chucksContent.append(chunk.getContent().toString(CharsetUtil.UTF_8));
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Channels.fireExceptionCaught(ctx, e.getCause());
		fireError(new PlatformException(e.getCause().getMessage(), e.getCause()));
		ctx.getChannel().close();
	}

	/**
	 * @param exception
	 */
	private void fireError(PlatformException exception) {
		if (_client.getConfiguration()._responseListener != null) {
			try {
				_client.getConfiguration()._responseListener.fireError(exception);
			} catch (PlatformException e) {
				theLogger.exception(e);
			}
		}
	}

	/**
	 * @param content
	 */
	private void fireSucceed(String content) {
		if (_client.getConfiguration()._responseListener != null) {
			try {
				_client.getConfiguration()._responseListener.fireSucceed(content);
			} catch (PlatformException e) {
				theLogger.exception(e);
			}
		}
	}
}
