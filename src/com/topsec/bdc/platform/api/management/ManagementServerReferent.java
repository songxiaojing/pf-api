package com.topsec.bdc.platform.api.management;

import com.topsec.bdc.platform.api.server.IRequestListener;
import com.topsec.bdc.platform.api.server.ServerReferent;


public class ManagementServerReferent extends ServerReferent {

    //
    public String _id = null;
    public String _name = null;
    public String _description = null;

    public ManagementServerReferent() {

        // TODO Auto-generated constructor stub
    }

    @Override
    public String getDescription() {

        return this._description;
    }

    @Override
    public String getName() {

        return this._name;
    }

    @Override
    public void setName(String name) {

        this._name = name;
    }

    @Override
    public void setDescription(String description) {

        this._description = description;
    }

    @Override
    public void setID(String id) {

        this._id = id;
    }

    @Override
    public String getID() {

        return this._id;
    }

    @Override
    public void setRequestListener(IRequestListener handler) {

        this._requestListener = handler;

    }
}
