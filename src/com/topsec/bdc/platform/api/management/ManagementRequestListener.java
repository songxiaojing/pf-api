package com.topsec.bdc.platform.api.management;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.topsec.bdc.platform.api.server.IServer;
import com.topsec.bdc.platform.api.server.http.HttpSoopRequestListener;
import com.topsec.bdc.platform.api.services.APIEngineService;
import com.topsec.bdc.platform.core.exception.PlatformException;
import com.topsec.bdc.platform.core.services.ServiceHelper;


public class ManagementRequestListener extends HttpSoopRequestListener {

    private static APIEngineService aPIEngineService = null;

    public ManagementRequestListener() {

    }

    @Override
    public String fireSucceed(Object[] parameters) throws PlatformException {

        if (aPIEngineService == null) {
            aPIEngineService = ServiceHelper.findService(APIEngineService.class);
            if (aPIEngineService == null) {
                return "{message:\"APIEngineService is not ready.\"}";
            }
        }

        Iterable<Map.Entry<String, String>> head = (Iterable<Entry<String, String>>) parameters[0];
        Map<String, List<String>> parameterMap = (Map<String, List<String>>) parameters[1];
        String path = (String) parameters[2];
        String message = (String) parameters[3];
        JSONArray apiArray = new JSONArray();
        List<IServer> httpServerList = aPIEngineService.getApiServersList();

        if (httpServerList != null) {
            for (int i = 0; i < httpServerList.size(); i++) {
                JSONObject apiObject = new JSONObject();
                IServer server = httpServerList.get(i);
                if (server == null) {
                    continue;
                }
                try {
                    apiObject.put("name", server.getName());
                    apiObject.put("id", server.getID());
                    apiObject.put("desc", server.getDescription());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                apiArray.put(apiObject);
            }
        }

        return apiArray.toString();
    }

    @Override
    public String fireError(Object[] parameters) throws PlatformException {

        Iterable<Map.Entry<String, String>> head = (Iterable<Entry<String, String>>) parameters[0];
        Map<String, List<String>> parameterMap = (Map<String, List<String>>) parameters[1];
        String path = (String) parameters[2];
        String message = (String) parameters[3];
        System.err.println(">>>>>>>>>>" + message);
        return "OK";
    }
}
