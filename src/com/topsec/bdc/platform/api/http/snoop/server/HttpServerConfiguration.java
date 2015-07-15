package com.topsec.bdc.platform.api.http.snoop.server;

import com.topsec.bdc.platform.api.server.IHandler;
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
public class HttpServerConfiguration implements IConfiguration {

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
    //
    public long _requestTotal = 0;
    //
    public IHandler _requestHandler = null;

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
