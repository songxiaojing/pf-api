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

    public void setClientReferent(ClientReferent ClientReferent);

    public ClientReferent getClientReferent();
}
