package com.topsec.bdc.platform.api.client;

import com.topsec.bdc.platform.core.services.ILife;


/**
 * 
 * Your class summary,end with '.'.
 * 
 * This interface is used to communicate with the Client implementations. Code will need to all several methods on the Client.
 * 
 * @title IClient
 * @package com.topsec.bdc.platform.api.client
 * @author baiyanwei
 * @version
 * @date Jul 15, 2015
 * 
 */
public interface IClient extends ILife {

    /**
     * Clients and servers need to provide the id that they were given to all the processing workflow to find service they require.
     * 
     * @return
     */
    public String getId();

    /**
     * set the client running parameter or configuration
     * 
     * @param client
     */
    public void configure(ClientConfiguration client);

    /**
     * get configuration.
     * 
     * @return
     */
    public ClientConfiguration getConfiguration();

}
