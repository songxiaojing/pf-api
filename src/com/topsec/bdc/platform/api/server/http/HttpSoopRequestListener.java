package com.topsec.bdc.platform.api.server.http;

import java.util.List;
import java.util.Map;

import com.topsec.bdc.platform.api.server.IRequestListener;


/**
 * 
 * HTTP API服务请求监听器抽象类.
 * 
 * HTTP API服务请求监听器必须承继此类.
 * 
 * @title HttpSoopRequestListener
 * @package com.topsec.bdc.platform.api.server.http
 * @author baiyanwei
 * @version
 * @date Sep 11, 2015
 * 
 */
public abstract class HttpSoopRequestListener implements IRequestListener {

    /**
     * ID.
     */
    public String _id = null;
    /**
     * 配置参数的名称.
     */
    public String _name = null;
    /**
     * 描述信息.
     */
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

    /**
     * 获取HTTP头部信息集合.
     * 
     * @param objects
     * @return
     */
    @SuppressWarnings("unchecked")
    public Iterable<Map.Entry<String, String>> getHttpHead(Object[] objects) {

        if (objects.length == 0) {
            return null;
        }
        return (Iterable<Map.Entry<String, String>>) objects[0];
    }

    /**
     * 取得以GET方式中URL的参数集合.
     * 
     * @param objects
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getURLParameterMap(Object[] objects) {

        if (objects.length == 0) {
            return null;
        }
        return (Map<String, List<String>>) objects[1];
    }

    /**
     * 取得HTTP请求的URL PATH.
     * 
     * @param objects
     * @return
     */
    public String getPath(Object[] objects) {

        if (objects.length == 0) {
            return null;
        }
        return (String) objects[2];
    }

    /**
     * 取得HTTP消息主体.
     * 
     * @param objects
     * @return
     */
    public String getMessage(Object[] objects) {

        if (objects.length == 0) {
            return null;
        }
        return (String) objects[3];
    }
}
