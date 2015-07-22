package com.topsec.bdc.platform.api.server;

import javax.xml.bind.annotation.XmlElement;

import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * 
 * Your class summary,end with '.'.
 * 
 * Your class Detail description,end with '.'.
 * 
 * @title HttpServerConfiguration
 * @package com.topsec.bdc.platform.api.http.snoop.server
 * @author baiyanwei
 * @version
 * @date Jul 15, 2015
 * 
 */
public abstract class ServerReferent implements IConfiguration {

    //
    @XmlElement(name = "serverIpAddress", type = String.class)
    public String _serverIpAddress = null;
    @XmlElement(name = "serverPort", type = Integer.class)
    public int _serverPort = 50000;
    @XmlElement(name = "bossGroupSize", type = Integer.class, defaultValue = "100")
    public int _bossGroupSize = 100;
    @XmlElement(name = "workerGroupSize", type = Integer.class, defaultValue = "100")
    public int _workerGroupSize = 100;
    @XmlElement(name = "enableTimeout", type = Boolean.class)
    public boolean _enableTimeout = true;
    @XmlElement(name = "timeout", type = Integer.class)
    public Integer _timeout = 100000;
    //
    @XmlElement(name = "enableSSL", type = Boolean.class)
    public boolean _enableSSL = false;
    @XmlElement(name = "enableCompressor", type = Boolean.class)
    public boolean _enableCompressor = false;
    //
    public long _requestTotal = 0;
    //
    public IRequestListener _requestListener = null;

    abstract public void setRequestListener(IRequestListener handler);
}
