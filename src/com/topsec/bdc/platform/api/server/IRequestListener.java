package com.topsec.bdc.platform.api.server;

import java.util.List;
import java.util.Map;

import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * @author Martin Bai. define a handler for doing yourself logic. Jun 8, 2012
 */
public interface IRequestListener extends IConfiguration {

    public String fireSucceed(Object messageObj, Map<String, List<String>> parameterMap, String path, String message) throws Exception;

    public String fireError(Object messageObj, Map<String, List<String>> parameterMap, String path, String message) throws Exception;
}
