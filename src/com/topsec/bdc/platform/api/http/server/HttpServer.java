package com.topsec.bdc.platform.api.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

import javax.xml.bind.annotation.XmlElement;

import com.topsec.bdc.platform.api.http.client.HbaseHttpClient;
import com.topsec.bdc.platform.api.server.IHttpRequestHandler;
import com.topsec.bdc.platform.api.server.IServer;
import com.topsec.bdc.platform.core.metrics.AbstractMetricMBean;
import com.topsec.bdc.platform.core.metrics.Metric;
import com.topsec.bdc.platform.core.metrics.MetricUtils;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * @author baiyanwei Jul 11, 2013 HTTP Server
 */
public class HttpServer extends AbstractMetricMBean implements IServer {

    private static PlatformLogger theLogger = PlatformLogger.getLogger(HttpServer.class);
    private ServerBootstrap _bootstrap = null;
    private String _id = null;
    private String _name = null;
    private String description = null;
    @Metric(description = "http server port")
    @XmlElement(name = "port", type = Integer.class)
    public int _port = 8080;
    @Metric(description = "http server port")
    public int _requestTotal = 0;

    public String _ipAddress = null;

    private HashMap<String, IHttpRequestHandler> _pathMap = new HashMap<String, IHttpRequestHandler>();

    @Override
    public void start() throws Exception {

        _bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        // Set up the event pipeline factory.
        _bootstrap.setPipelineFactory(new HttpServerPipelineFactory(this));
        // Bind and start to accept incoming connections.
        InetSocketAddress inetAddress = new InetSocketAddress(_port);
        _ipAddress = inetAddress.getAddress().getHostAddress();
        _bootstrap.bind(inetAddress);
        MetricUtils.registerMBean(this);
    }

    @Override
    public void stop() throws Exception {

        try {
            if (_bootstrap != null && _bootstrap.getPipelineFactory() != null && _bootstrap.getPipelineFactory().getPipeline() != null) {
                if (_bootstrap.getPipelineFactory().getPipeline().get(HbaseHttpClient.READ_TIME_OUT_PIPE_LINE) != null) {
                    ReadTimeoutHandler readTimerHandler = (ReadTimeoutHandler) _bootstrap.getPipelineFactory().getPipeline().remove(HbaseHttpClient.READ_TIME_OUT_PIPE_LINE);
                    // stop the read timer
                    readTimerHandler.releaseExternalResources();
                }
                if (_bootstrap.getPipelineFactory().getPipeline().get(HbaseHttpClient.WRITE_TIME_OUT_PIPE_LINE) != null) {
                    WriteTimeoutHandler writeTimerHandler = (WriteTimeoutHandler) _bootstrap.getPipelineFactory().getPipeline().remove(HbaseHttpClient.WRITE_TIME_OUT_PIPE_LINE);
                    // stop the write timer
                    writeTimerHandler.releaseExternalResources();
                }
            }
        } catch (Throwable t) {
            theLogger.exception(t);
        }
        _bootstrap.releaseExternalResources();
    }

    @Override
    public void addHandler(IHttpRequestHandler handler) throws Exception {

        if (handler == null) {
            return;
        }
        this._pathMap.put(handler.getRequestMappingPath(), handler);
    }

    @Override
    public IHttpRequestHandler getHandler(String path) {

        if (path == null || path.trim().equals("")) {
            return null;
        }
        return this._pathMap.get(path.trim());
    }

    @Override
    public IHttpRequestHandler removeHandler(IHttpRequestHandler handler) {

        if (handler == null) {
            return null;
        }
        return this._pathMap.get(handler.getName());
    }

    @Metric(description = "description of the server")
    @Override
    public String getDescription() {

        return description;
    }

    @Metric(description = "server name")
    @Override
    public String getName() {

        return this._name;
    }

    @Override
    public void setName(String name) {

        this._name = name;
    }

    @Override
    public void setDescription(String description) {

        this.description = description;
    }

    @Override
    public void setID(String id) {

        this._id = id;
    }

    @Metric(description = "server id")
    @Override
    public String getID() {

        return this._id;
    }

    public String toString() {

        return theLogger.MessageFormat("toString", this._name, this._port);
    }

    @Metric(description = "Server running status")
    public String serverStatus() {

        return "everything is fine.";
    }

    public HashMap<String, IHttpRequestHandler> getHttpRequestHandlerMap() {

        return this._pathMap;
    }
}
