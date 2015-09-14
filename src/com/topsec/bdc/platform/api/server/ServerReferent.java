package com.topsec.bdc.platform.api.server;

import javax.xml.bind.annotation.XmlElement;

import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * 
 * 平台API服务的参考配置.
 * 
 * 配置服务API的连接地址，连接端口，处理方式等内容，所有平台的API服务参考必须承继此抽象类.
 * 
 * @title HttpServerConfiguration
 * @package com.topsec.bdc.platform.api.http.snoop.server
 * @author baiyanwei
 * @version
 * @date Jul 15, 2015
 * 
 */
public abstract class ServerReferent implements IConfiguration {

    /**
     * ID.
     */
    public String _id = null;
    /**
     * 配置参数的名称.
     */
    public String _name = null;
    /**
     * 描述信息.
     */
    public String _description = null;
    /**
     * API服务绑定的IP地址.
     */
    @XmlElement(name = "serverIpAddress", type = String.class)
    public String _serverIpAddress = null;
    /**
     * API服务绑定的端口，这个端口范围为 大于1024.
     */
    @XmlElement(name = "serverPort", type = Integer.class)
    public int _serverPort = 50000;
    /**
     * NETTY的管理线程池大小.
     */
    @XmlElement(name = "bossGroupSize", type = Integer.class, defaultValue = "100")
    public int _bossGroupSize = 100;
    /**
     * NETTY的工作线程池大小.
     */
    @XmlElement(name = "workerGroupSize", type = Integer.class, defaultValue = "100")
    public int _workerGroupSize = 100;
    /**
     * 是否启动API服务的请示响应超时处理.
     */
    @XmlElement(name = "enableTimeout", type = Boolean.class, defaultValue = "true")
    public boolean _enableTimeout = true;

    /**
     * 数据上行通道读取超时设置，单位为秒
     */
    @XmlElement(name = "readTimeoutSecond", type = Integer.class, defaultValue = "50")
    public int _readTimeoutSecond = 50;
    /**
     * 数据下行通道写入超时设置，单位为秒.
     */
    @XmlElement(name = "writeTimeoutSecond", type = Integer.class, defaultValue = "50")
    public int _writeTimeoutSecond = 50;
    //
    /**
     * 是否启动HTTPS设置
     */
    @XmlElement(name = "enableSSL", type = Boolean.class)
    public boolean _enableSSL = false;
    /**
     * 是否启动消息体压缩功能
     */
    @XmlElement(name = "enableCompressor", type = Boolean.class)
    public boolean _enableCompressor = false;
    //
    /**
     * API服务响应的请求数统计.
     */
    public long _requestTotal = 0;

    /**
     * API服务响应的错误请求数统计.
     */
    public long _requestErrorTotal = 0;

    //
    /**
     * 设置API服务请求响应的处理监听器.
     * 
     * @param name
     * @param listener
     */
    public abstract void addRequestListener(String name, IRequestListener listener);

    /**
     * 取得处理请求监听器.
     * 
     * @param name
     * @return
     */
    public abstract IRequestListener getResquestListener(String name);

    @Override
    public String getDescription() {

        return this._description;
    }

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

        this._description = description;
    }

    @Override
    public void setID(String id) {

        this._id = id;
    }

    @Override
    public String getID() {

        return this._id;
    }

    /**
     * API服务的IP地址.
     * 
     * @return
     */
    public String getServerIpAddress() {

        return _serverIpAddress;
    }

    /**
     * API服务的端口.
     * 
     * @return
     */
    public int getServerPort() {

        return _serverPort;
    }

    /**
     * 累加服务请求的响应数.
     * 
     */
    public void requestTotal() {

        _requestTotal++;
    }

    /**
     * 取得API服务从服务开启到现在响应的请求数.
     * 
     * @return
     */
    public long getResquestTotal() {

        return this._requestTotal < 0 ? (this._requestTotal - (Long.MAX_VALUE + 1) + 1) : this._requestTotal;
    }

    /**
     * 累加服务错误请求的响应数.
     * 
     */
    public void requestErrorTotal() {

        _requestTotal++;
    }

    /**
     * 取得API从服务开启到现在响应的错误请求数.
     * 
     * @return
     */
    public long getResquestErrorTotal() {

        return this._requestErrorTotal < 0 ? (this._requestErrorTotal - (Long.MAX_VALUE + 1) + 1) : this._requestErrorTotal;
    }
}
