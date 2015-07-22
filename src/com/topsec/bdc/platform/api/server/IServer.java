package com.topsec.bdc.platform.api.server;

import com.topsec.bdc.platform.core.services.IConfiguration;
import com.topsec.bdc.platform.core.services.ILife;


/**
 * @author baiyanwei Jul 11, 2013 Define a HTTP Server behavior.
 * 
 */
public interface IServer extends ILife, IConfiguration {

    public void setHttpServerReferent(ServerReferent serverConfig);
}
