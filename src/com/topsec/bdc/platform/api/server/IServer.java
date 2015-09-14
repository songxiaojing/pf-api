package com.topsec.bdc.platform.api.server;

import com.topsec.bdc.platform.core.services.IConfiguration;
import com.topsec.bdc.platform.core.services.ILife;


/**
 * 
 * API服务行为接口.
 * 
 * 定义平台系统中API服务应该具有的行为，系统平台中的服务类必须现实此接口.
 * 
 * @title IServer
 * @package com.topsec.bdc.platform.api.server
 * @author baiyanwei
 * @version
 * @date Sep 11, 2015
 * 
 */
public interface IServer extends ILife, IConfiguration {

    /**
     * 设置API服务的参考配置.
     * 
     * @param serverConfig
     */
    public void setServerReferent(ServerReferent serverConfig);
}
