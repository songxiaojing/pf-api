package com.topsec.bdc.platform.api.server;

import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * @author Martin Bai. define a handler for doing yourself logic. Jun 8, 2012
 */
public interface IHandler extends IConfiguration {

    public void fireSucceed(Object messageObj) throws Exception;

    public void fireError(Object messageObj) throws Exception;
}
