package com.topsec.bdc.platform.api.server.http;

import com.topsec.bdc.platform.api.server.IRequestListener;



public abstract class HttpSoopRequestListener implements IRequestListener {

    public String _id = null;
    public String _name = null;
    public String _description = null;

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

}
