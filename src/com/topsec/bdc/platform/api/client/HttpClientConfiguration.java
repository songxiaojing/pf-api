package com.topsec.bdc.platform.api.client;

import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * This class is used to pass all configuration data that is required by a client network connection.
 * 
 * @author baiyanwei Jul 8, 2013
 * 
 */
public class HttpClientConfiguration implements IConfiguration {

    public String _id = null;
    public String _name = null;
    public String _description = null;
    //
    public String _serverIpAddress = null;
    public int _serverPort = 50000;
    public int _bossGroupSize = 100;
    public int _workerGroupSize = 100;
    public boolean _enableTimeout = true;
    public Integer _timeout = 100000;
    //
    public boolean _enableSSL = false;
    public boolean _enableCompressor = false;
    //
    public boolean _synchronousConnection = true;
    //
    public IResponseListener _clientResponseListener = null;

    //

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

}
