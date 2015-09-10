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

    public String _id = null;
    public String _name = null;
    public String _description = null;
    //
    @XmlElement(name = "serverIpAddress", type = String.class)
    public String _serverIpAddress = null;
    @XmlElement(name = "serverPort", type = Integer.class)
    public int _serverPort = 50000;
    @XmlElement(name = "bossGroupSize", type = Integer.class, defaultValue = "100")
    public int _bossGroupSize = 100;
    @XmlElement(name = "workerGroupSize", type = Integer.class, defaultValue = "100")
    public int _workerGroupSize = 100;
    @XmlElement(name = "enableTimeout", type = Boolean.class, defaultValue = "true")
    public boolean _enableTimeout = true;
    @XmlElement(name = "soTimeout", type = Integer.class)
    public Integer _soTimeout = 100000;
    @XmlElement(name = "readTimeoutSecond", type = Integer.class)
    public int _readTimeoutSecond = 3;
    @XmlElement(name = "writeTimeoutSecond", type = Integer.class)
    public int _writeTimeoutSecond = 3;
    //
    @XmlElement(name = "enableSSL", type = Boolean.class)
    public boolean _enableSSL = false;
    @XmlElement(name = "enableCompressor", type = Boolean.class)
    public boolean _enableCompressor = false;
    //
    public long _requestTotal = 0;

    //
    public abstract void addRequestListener(String name, IRequestListener listener);

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

    public String getServerIpAddress() {

        return _serverIpAddress;
    }

    public int getServerPort() {

        return _serverPort;
    }

}
