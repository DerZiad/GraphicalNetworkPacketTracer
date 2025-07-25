package org.npt.services.defaults;

import lombok.Getter;
import org.npt.exception.ShutdownException;
import org.npt.models.KnownHost;
import org.npt.services.ArpSpoofService;
import org.npt.services.DataService;
import org.npt.services.GraphicalNetworkTracerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Getter
public class DefaultGraphicalNetworkTracerFactory implements GraphicalNetworkTracerFactory {

    private static DefaultGraphicalNetworkTracerFactory instance = null;
    private static final String PATH = "/org/npt/%s";

    private DefaultGraphicalNetworkTracerFactory() {

        try {
            run();
        } catch (ShutdownException e) {
            // TODO shutdownException should be handled properly
            throw new RuntimeException(e);
        }
    }

    private final HashMap<String, KnownHost> knownHosts = new HashMap<>();

    @Override
    public ArpSpoofService getArpSpoofService() {
        return DefaultArpSpoofService.getInstance();
    }

    @Override
    public DataService getDataService() {
        return DefaultDataService.getInstance();
    }

    @Override
    public InputStream getResource(String name) {
        String resourcePath = String.format(PATH, name);
        return this.getClass().getResourceAsStream(resourcePath);
    }

    public static DefaultGraphicalNetworkTracerFactory getInstance() {
        if (instance == null) {
            instance = new DefaultGraphicalNetworkTracerFactory();
        }
        return instance;
    }

    // Private method to initiate KnownHosts if needed
    private void run() throws ShutdownException {

        InputStream is = getResource("settings/npt.properties");
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            throw new ShutdownException(String.format(ShutdownException.ERROR_FORMAT, this.getClass().getName(), "Failed to load property File"), ShutdownException.ShutdownExceptionErrorCode.FAILED_TO_LOAD_PROPERTY_FILE);
        }

        final Set<String> keys = props.stringPropertyNames();
        for (final String key : keys) {
            if (key.endsWith(".icon")) {
                final String appName = key.substring(0, key.indexOf(".icon"));
                final String iconPath = props.getProperty(key);
                final String ipKey = appName + ".ips";
                final String ipValue = props.getProperty(ipKey, "");
                final List<String> ipList = Arrays.asList(ipValue.split("\\s*,\\s*"));
                knownHosts.put(appName, new KnownHost(appName, iconPath, ipList));
            }
        }

    }
}
