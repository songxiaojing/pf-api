package com.topsec.bdc.platform.api.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.topsec.bdc.platform.api.server.IRequestListener;
import com.topsec.bdc.platform.api.server.IServer;
import com.topsec.bdc.platform.api.server.ServerReferent;
import com.topsec.bdc.platform.core.metrics.AbstractMetricMBean;
import com.topsec.bdc.platform.core.metrics.MetricUtils;
import com.topsec.bdc.platform.core.services.IConfiguration;
import com.topsec.bdc.platform.core.services.IService;
import com.topsec.bdc.platform.core.services.PropertyLoaderService;
import com.topsec.bdc.platform.core.services.ServiceHelper;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * 
 * 接口引擎.
 * 
 * 将配置的接口，实例化，并启动，注册.
 * 
 * @title APIEngineService
 * @package com.topsec.bdc.platform.api.services
 * @author baiyanwei
 * @version
 * @date Jul 20, 2015
 * 
 */
public class APIEngineService extends AbstractMetricMBean implements IService {

    final public static String LISTENER_CONF_TITLE = "listener";
    final public static String REFERENT_CONF_TITLE = "serverReferent";
    final public static String SERVER_CONF_TITLE = "pf-api.platformApiServer";

    final private static PlatformLogger theLogger = PlatformLogger.getLogger(APIEngineService.class);

    private ArrayList<IServer> _apiServersList = new ArrayList<IServer>();

    @Override
    public void start() throws Exception {

        startupServers();
        MetricUtils.registerMBean(this);
    }

    @Override
    public void stop() throws Exception {

        shutdownServers();
        MetricUtils.unRegisterMBean(this);
    }

    /**
     * start all API servers.
     */
    private void startupServers() {

        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(SERVER_CONF_TITLE);
        startupServers(config);

    }

    public void startupServers(IConfigurationElement[] config) {

        if (config == null || config.length == 0) {
            return;
        }
        PropertyLoaderService propertyLoaderService = ServiceHelper.findService(PropertyLoaderService.class);
        if (propertyLoaderService == null) {
            System.err.println("Can't find the PropertyLoaderService, Server can't start without PropertyLoaderService.");
            return;
        }
        //
        ArrayList<IServer> configServerList = new ArrayList<IServer>();
        // register server from configuration.
        //Level #1 Server
        for (IConfigurationElement configElement : config) {
            try {
                // Instance implement class .
                IServer server = (IServer) configElement.createExecutableExtension(IConfiguration.IMPLEMENT_CLASS_CONF_TITLE);
                server.setID(configElement.getAttribute(IConfiguration.ID_CONF_TITLE));
                server.setName(configElement.getAttribute(IConfiguration.NAME_CONF_TITLE));
                server.setDescription(configElement.getAttribute(IConfiguration.DESCRIPTION_CONF_TITLE));
                // loading the properties from configuration.
                propertyLoaderService.loadExtensionProperties(configElement.getChildren(IConfiguration.PROPERTY_CONF_TITLE), server);
                //
                //Level #2 referent
                IConfigurationElement[] serverReferentConfig = configElement.getChildren(REFERENT_CONF_TITLE);
                if (serverReferentConfig != null && serverReferentConfig.length != 0) {
                    for (IConfigurationElement serverReferemtElement : serverReferentConfig) {
                        ServerReferent serverReferent = (ServerReferent) serverReferemtElement.createExecutableExtension(IConfiguration.IMPLEMENT_CLASS_CONF_TITLE);
                        //
                        serverReferent.setID(serverReferemtElement.getAttribute(IConfiguration.ID_CONF_TITLE));
                        serverReferent.setName(serverReferemtElement.getAttribute(IConfiguration.NAME_CONF_TITLE));
                        serverReferent.setDescription(serverReferemtElement.getAttribute(IConfiguration.DESCRIPTION_CONF_TITLE));
                        //
                        propertyLoaderService.loadExtensionProperties(serverReferemtElement.getChildren(IConfiguration.PROPERTY_CONF_TITLE), serverReferent);
                        //
                        //Level #3 handler
                        IConfigurationElement[] listenerConfig = serverReferemtElement.getChildren(LISTENER_CONF_TITLE);
                        if (listenerConfig != null && listenerConfig.length != 0) {
                            for (IConfigurationElement listenerElement : listenerConfig) {
                                //
                                serverReferent.addRequestListener(listenerElement.getAttribute(IConfiguration.NAME_CONF_TITLE), (IRequestListener) listenerElement.createExecutableExtension(IConfiguration.IMPLEMENT_CLASS_CONF_TITLE));
                                //
                                theLogger.info("bundleListener", listenerElement.getAttribute(IConfiguration.NAME_CONF_TITLE), server.getName());
                            }
                        }
                        server.setServerReferent(serverReferent);
                        theLogger.info("bundleServerReferent", serverReferent.getName(), server.getName(), serverReferent.getServerIpAddress(), serverReferent.getServerPort());
                    }
                }
                //
                theLogger.info("configureServer", server.getID(), server.getName(), server.getDescription());
                //
                configServerList.add(server);
                theLogger.info("=================================================================");
            } catch (CoreException e1) {
                theLogger.exception(e1);
            } catch (Throwable t) {
                theLogger.exception(t);
            }
        }
        //register and start
        for (int i = 0; i < configServerList.size(); i++) {
            try {
                IServer server = configServerList.get(i);
                server.start();
                this._apiServersList.add(server);
                if (server instanceof IService) {
                    ServiceHelper.registerService((IService) server, false, false);
                }
                theLogger.info("startServer", server.getID(), server.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * shutdown all API servers.
     */
    private void shutdownServers() {

        for (int i = 0; i < _apiServersList.size(); i++) {
            final IServer targetServer = _apiServersList.get(i);
            new Thread() {

                public void run() {

                    try {
                        targetServer.stop();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public List<IServer> getApiServersList() {

        return _apiServersList;
    }
}
