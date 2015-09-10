package com.topsec.bdc.platform.api.server.http;

import java.util.HashMap;

import com.topsec.bdc.platform.api.server.IRequestListener;
import com.topsec.bdc.platform.api.server.ServerReferent;
import com.topsec.bdc.platform.core.utils.Assert;


public class HttpSoopServerReferent extends ServerReferent {

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
