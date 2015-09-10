package com.topsec.bdc.platform.api.client;

import com.topsec.bdc.platform.core.exception.PlatformException;
import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * @author baiyanwei
 * 
 *         Jul 11, 2013
 * 
 *         Define The listener behavior of IClient, the listener just fire succeed and error method, you should do yourself logic it them.
 */
public interface IResponseListener extends IConfiguration {

    public String fireSucceed(Object[] object) throws PlatformException;

    public String fireError(Object[] object) throws PlatformException;

}
