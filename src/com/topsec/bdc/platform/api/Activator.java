package com.topsec.bdc.platform.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.topsec.bdc.platform.api.services.APIEngineService;
import com.topsec.bdc.platform.core.services.ServiceHelper;
import com.topsec.bdc.platform.log.PlatformLogger;


public class Activator implements BundleActivator {

    private static PlatformLogger logger = PlatformLogger.getLogger(Activator.class);
    private static BundleContext context;

    static BundleContext getContext() {

        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {

        Activator.context = bundleContext;
        logger.info("###PL-API is started~");
        ServiceHelper.registerService(new APIEngineService());
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {

        Activator.context = null;
    }

}
