package com.topsec.bdc.platform.api.client.http;

import com.topsec.bdc.platform.api.client.ClientReferent;
import com.topsec.bdc.platform.api.client.IResponseListener;


/**
 * This class is used to pass all configuration data that is required by a client network connection.
 * 
 * @author baiyanwei Jul 8, 2013
 * 
 */
public class HttpClientReferent extends ClientReferent {

    //
    public IResponseListener _clientResponseListener = null;

    @Override
    public IResponseListener getClientResponseListener() {

        return _clientResponseListener;
    }

    @Override
    public void setClientResponseListener(IResponseListener listener) {

        this._clientResponseListener = listener;

    }

}
