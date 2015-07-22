package com.topsec.bdc.platform.api.management;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.topsec.bdc.platform.api.server.IRequestListener;


public class ManagementRequestListener implements IRequestListener {

    public String _id = null;
    public String _name = null;
    public String _description = null;

    @XmlElement(name = "name", type = String.class)
    public String name = null;

    public ManagementRequestListener() {

    }

    @Override
    public String fireSucceed(Object messageObj) throws Exception {

        System.out.println(name + ">>>>>>>>>>" + messageObj);
        return "OK";
    }

    @Override
    public String fireError(Object messageObj) throws Exception {

        System.err.println(">>>>>>>>>>" + messageObj);
        return "OK";
    }

    @Override
    public void setHttpHeader(HttpHeaders headers) {

    }

    @Override
    public void setHttpParameter(Map<String, List<String>> params) {

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

}
