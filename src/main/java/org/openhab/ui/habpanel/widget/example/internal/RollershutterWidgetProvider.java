/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Creative Commons BY-SA 3.0
 * license which accompanies this distribution, and is available at
 * https://creativecommons.org/licenses/by-sa/3.0/legalcode.txt
 */
package org.openhab.ui.habpanel.widget.example.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.smarthome.ui.icon.AbstractResourceIconProvider;
import org.eclipse.smarthome.ui.icon.IconProvider;
import org.eclipse.smarthome.ui.icon.IconSet;
import org.eclipse.smarthome.ui.icon.IconSet.Format;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a sample rollershutter widget for HABPanel along with
 * its associated iconset (icons from the KNX forum) and other necessary files.
 *
 * @author Yannick Schaus - Initial contribution
 */
public class RollershutterWidgetProvider extends AbstractResourceIconProvider implements IconProvider {

    private final Logger logger = LoggerFactory.getLogger(RollershutterWidgetProvider.class);

    protected HttpService httpService;

    private ConfigurationAdmin configurationAdmin;

    /**
     * The service PID for altering HABPanel configuration. Don't change.
     */
    public static final String HABPANEL_CONFIG_FILTER = "(service.pid=org.openhab.habpanel)";

    /**
     * The widget ID to add to HABPanel's configuration.
     * It must begin with "widget." or it will be ignored.
     */
    public static final String WIDGET_ID = "widget.rollershutter-example";

    /**
     * The path to the JSON file containing the widget description in the bundle.
     */
    public static final String WIDGET_FILE = "rollershutter-example.widget.json";

    /**
     * The ID of the iconset the icon provider will register.
     */

    public static final String ICONSET_ID = "knx-rollershutter";
    /**
     * The path where static resources will be served.
     * Widget templates may include those files using relative URLs, including
     * stylesheets and scripts (using the oc-lazy-load directive).
     */
    public static final String STATIC_RESOURCES_ALIAS = "/habpanel-resources/example-widget";

    @Override
    public Set<IconSet> getIconSets(Locale locale) {
        Set<Format> formats = new HashSet<>(1);
        formats.add(Format.SVG);
        String label = "KNX-Rollershutter";
        String description = "KNX forum rollershutter icons in SVG format.";
        IconSet iconSet = new IconSet(ICONSET_ID, label, description, formats);
        return Collections.singleton(iconSet);
    }

    @Override
    protected InputStream getResource(String iconSetId, String resourceName) {
        if (ICONSET_ID.equals(iconSetId)) {
            URL iconResource = context.getBundle().getEntry("icons/" + resourceName);
            try {
                return iconResource.openStream();
            } catch (IOException e) {
                logger.error("Failed to read icon '{}': {}", resourceName, e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected boolean hasResource(String iconSetId, String resourceName) {
        if (ICONSET_ID.equals(iconSetId)) {
            URL iconResource = context.getBundle().getEntry("icons/" + resourceName);
            return iconResource != null;
        } else {
            return false;
        }
    }

    @Override
    protected Integer getPriority() {
        return 0;
    }

    private String getWidgetJSON() throws Exception {
        URL widgetURL = context.getBundle().getEntry(WIDGET_FILE);
        BufferedReader in = new BufferedReader(new InputStreamReader(widgetURL.openStream()));
        return in.lines().collect(Collectors.joining("\n"));
    }

    @Override
    protected void activate(BundleContext context) {
        super.activate(context);

        // Register the static resources
        try {
            httpService.registerResources(STATIC_RESOURCES_ALIAS, "static", null);
        } catch (NamespaceException e) {
            logger.error("Error during static resources mapping: {}", e.getMessage());
        }

        // Register the widget
        try {
            Configuration[] configurations = configurationAdmin.listConfigurations(HABPANEL_CONFIG_FILTER);
            if (configurations != null) {
                Dictionary<String, Object> properties = configurations[0].getProperties();
                properties.put(WIDGET_ID, getWidgetJSON());
                configurations[0].update(properties);
            }
        } catch (Exception e) {
            logger.error("Error during widget provisioning: {}", e.toString());
        }

    }

    protected void deactivate(BundleContext context) {
        // Unregister the static resources
        httpService.unregister(STATIC_RESOURCES_ALIAS);

        // Unregister the widget
        try {
            Configuration[] configurations = configurationAdmin.listConfigurations(HABPANEL_CONFIG_FILTER);
            if (configurations != null) {
                Dictionary<String, Object> properties = configurations[0].getProperties();
                properties.remove(WIDGET_ID);
                configurations[0].update(properties);
            }
        } catch (Exception e) {
            logger.error("Error during widget unprovisioning: {}", e.getMessage());
        }
    }

    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    protected void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    protected void unsetConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = null;
    }

}
