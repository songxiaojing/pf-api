package com.topsec.bdc.platform.api.http.server;

import io.netty.handler.codec.http.HttpRequest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.xml.bind.annotation.XmlElement;

import com.topsec.bdc.platform.api.server.IHttpRequestHandler;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * @author baiyanwei
 * 
 *         Jan 1, 2014
 * 
 */
public class SimpleHttpRequstHandler implements IHttpRequestHandler {

    final private static PlatformLogger theLogger = PlatformLogger.getLogger(SimpleHttpRequstHandler.class);
    protected String id = null;
    protected String name = null;
    protected String description = null;
    @XmlElement(name = "path", type = String.class)
    public String path = "";

    @Override
    public Object DELETE(HttpRequest request, Object messageObj) throws Exception {

        long startPoint = System.currentTimeMillis();
        theLogger.debug("theLogger", String.valueOf(request.hashCode()), String.valueOf(System.currentTimeMillis() - startPoint), "DELETE", messageObj.toString());

        return "DELETE";
    }

    @Override
    public Object HEAD(HttpRequest request, Object messageObj) throws Exception {

        return "HEAD";
    }

    @Override
    public Object OPTIONS(HttpRequest request, Object messageObj) throws Exception {

        return "OPTIONS";
    }

    @Override
    public Object PUT(HttpRequest request, Object messageObj) throws Exception {

        return this.POST(request, messageObj);
    }

    @Override
    public Object TRACE(HttpRequest request, Object messageObj) throws Exception {

        return "TRACE";
    }

    @Override
    public Object GET(HttpRequest request, Object messageObj) throws Exception {

        return "GET";
    }

    @Override
    public Object POST(HttpRequest request, Object messageObj) throws Exception {

        String path = "/home/baiyanwei/research/moniotring/run/output/" + Utils.getUUID32();
        saveToFile(path, messageObj);
        return "SAVE:" + messageObj;
    }

    @Override
    public void setID(String id) {

        this.id = id;
    }

    @Override
    public String getID() {

        return this.id;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public String getName() {

        return this.name;
    }

    @Override
    public void setDescription(String description) {

        this.description = description;
    }

    @Override
    public String getDescription() {

        return this.description;
    }

    @Override
    public String getRequestMappingPath() {

        return this.path;
    }

    /**
     * @param fileName
     * @param saveObj
     * 
     *            save Object to file
     */
    private void saveToFile(String fileName, Object saveObj) {

        //
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(saveObj);
        } catch (FileNotFoundException e) {
            theLogger.exception(e);
        } catch (IOException e) {
            theLogger.exception(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
