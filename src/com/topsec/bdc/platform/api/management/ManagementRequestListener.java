package com.topsec.bdc.platform.api.management;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.topsec.bdc.platform.api.server.IServer;
import com.topsec.bdc.platform.api.server.http.HttpSoopRequestListener;
import com.topsec.bdc.platform.api.services.APIEngineService;
import com.topsec.bdc.platform.core.services.ServiceHelper;


public class ManagementRequestListener extends HttpSoopRequestListener {

    private static APIEngineService aPIEngineService = null;

    public ManagementRequestListener() {

    }

    @Override
    public String fireSucceed(Object messageObj, Map<String, List<String>> parameterMap, String path, String message) throws Exception {

        if (aPIEngineService == null) {
            aPIEngineService = ServiceHelper.findService(APIEngineService.class);
            if (aPIEngineService == null) {
                return "{message:\"APIEngineService is not ready.\"}";
            }
        }

        JSONArray apiArray = new JSONArray();
        List<IServer> httpServerList = aPIEngineService.getApiServersList();

        if (httpServerList != null) {
            for (int i = 0; i < httpServerList.size(); i++) {
                JSONObject apiObject = new JSONObject();
                IServer server = httpServerList.get(i);
                if (server == null) {
                    continue;
                }
                apiObject.put("name", server.getName());
                apiObject.put("id", server.getID());
                apiObject.put("desc", server.getDescription());
                apiArray.put(apiObject);
            }
        }

        return apiArray.toString();
    }

    @Override
    public String fireError(Object messageObj, Map<String, List<String>> parameterMap, String path, String message) throws Exception {

        System.err.println(">>>>>>>>>>" + messageObj);
        return "OK";
    }
}
