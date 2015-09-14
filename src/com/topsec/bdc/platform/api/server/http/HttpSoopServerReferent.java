package com.topsec.bdc.platform.api.server.http;

import java.util.HashMap;

import com.topsec.bdc.platform.api.server.IRequestListener;
import com.topsec.bdc.platform.api.server.ServerReferent;
import com.topsec.bdc.platform.core.utils.Assert;


/**
 * 
 * HTTP 服务API参考.
 * 
 * HTTP 服务API参考.
 * 
 * @title HttpSoopServerReferent
 * @package com.topsec.bdc.platform.api.server.http
 * @author baiyanwei
 * @version
 * @date Sep 11, 2015
 * 
 */
public class HttpSoopServerReferent extends ServerReferent {

    /**
     * 绑定服务响应请求的监听器的集合
     */
    public HashMap<String, IRequestListener> _RequestMap = new HashMap<String, IRequestListener>();

    @Override
    public void addRequestListener(String name, IRequestListener listener) {

        _RequestMap.put(name, listener);
    }

    @Override
    public IRequestListener getResquestListener(String name) {

        if (Assert.isEmptyString(name) == true || this._RequestMap.containsKey(name) == false) {
            return null;
        }
        return this._RequestMap.get(name);
    }

}
