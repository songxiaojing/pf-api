package com.topsec.bdc.platform.api.server;

import com.topsec.bdc.platform.core.exception.PlatformException;
import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * @author Martin Bai. define a handler for doing yourself logic. Jun 8, 2012
 */
public interface IRequestListener extends IConfiguration {

    public String fireSucceed(Object[] object) throws PlatformException;

    public String fireError(Object[] object) throws PlatformException;
}
