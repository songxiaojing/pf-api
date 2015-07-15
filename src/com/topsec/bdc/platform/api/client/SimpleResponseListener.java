package com.topsec.bdc.platform.api.client;

import com.topsec.bdc.platform.core.exception.PlatformException;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * @author baiyanwei Jul 11, 2013
 * 
 *         simple client response listener
 * 
 */
public class SimpleResponseListener implements IClientResponseListener {

    final private static PlatformLogger theLogger = PlatformLogger.getLogger(SimpleResponseListener.class);
    //
    private String _id = "SimpleResponseListener";
    private String _name = "SimpleResponseListener";
    private String _description = "SimpleResponseListener";

    @Override
    public void setID(String id) {

        _id = id;
    }

    @Override
    public String getID() {

        return _id;
    }

    @Override
    public void setName(String name) {

        _name = name;

    }

    @Override
    public String getName() {

        return _name;
    }

    @Override
    public void setDescription(String description) {

        _description = description;

    }

    @Override
    public String getDescription() {

        return _description;
    }

    @Override
    public void fireSucceed(String message) throws PlatformException {

        theLogger.debug("fireSucceed", (message != null ? message.length() : 0));

    }

    @Override
    public void fireError(PlatformException exception) throws PlatformException {

        theLogger.error("fireError", (exception != null ? exception.getExceptionMassage() : "Null Exception"));
    }

}
