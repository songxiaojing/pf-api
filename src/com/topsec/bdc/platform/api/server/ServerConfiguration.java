package com.topsec.bdc.platform.api.server;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;

import com.topsec.bdc.platform.api.client.IClientResponseListener;


/**
 * This class is used to pass all configuration data that is required by a client network connection.
 * 
 * @author baiyanwei Jul 8, 2013
 * 
 */
public class ServerConfiguration {

    @XmlElement(name = "endPointPort", type = Integer.class, defaultValue = "80")
    public int _endPointPort = 80;
    public String _protocolType = "HTTP";
    public int _readBufferSize = 1000000;
    public int _packetBufferSize = 10000;
    public boolean _bUseStaticThreadPool = true;
    public String _id = "";
    public String _trafficRecordId = "";
    public boolean _isPrefetch = false;
    public Object _httpRequest = null;
    public IClientResponseListener _responseListener = null;
    public HashMap<String, String> _parameterMap = null;
    // The Client content text ,like HTTP client body.
    public String _content = null;
    public boolean _bEnableTLS = false;
}
