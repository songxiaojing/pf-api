package com.topsec.bdc.platform.api.server;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;
import java.util.Map;

import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * @author Martin Bai. define a handler for doing yourself logic. Jun 8, 2012
 */
public interface IRequestListener extends IConfiguration {

    public String fireSucceed(Object messageObj) throws Exception;

    public String fireError(Object messageObj) throws Exception;

    public void setHttpHeader(HttpHeaders headers);

    public void setHttpParameter(Map<String, List<String>> params);
}
