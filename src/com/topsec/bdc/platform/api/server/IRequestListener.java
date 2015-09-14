package com.topsec.bdc.platform.api.server;

import com.topsec.bdc.platform.core.exception.PlatformException;
import com.topsec.bdc.platform.core.services.IConfiguration;


/**
 * 
 * 平台服务API请求响应监听器.
 * 
 * 平台中的请求监听器必须现实此接口.
 * 
 * @title IRequestListener
 * @package com.topsec.bdc.platform.api.server
 * @author baiyanwei
 * @version
 * @date Sep 11, 2015
 * 
 */
public interface IRequestListener extends IConfiguration {

    /**
     * 请求接收成功响应事件.
     * 
     * @param object
     * @return
     * @throws PlatformException
     */
    public String fireSucceed(Object[] object) throws PlatformException;

    /**
     * 请求接收失败响应事件.
     * 
     * @param object
     * @return
     * @throws PlatformException
     */
    public String fireError(Object[] object) throws PlatformException;
}
