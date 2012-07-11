/*
 * Copyright (C) 2012 Clearspring Technologies, Inc.⋅
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearspring.metriccatcher;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.jackson.map.util.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.core.Metric;
import com.yammer.metrics.reporting.GangliaReporter;
import com.yammer.metrics.reporting.GraphiteReporter;

public class Loader {
    private static final Logger logger = LoggerFactory.getLogger(Loader.class);
    private static final String defaultPropertiesFilename = "conf/config.properties";

    private MetricCatcher metricCatcher;

    // Configuration variables
    private static final String METRICCATCHER_INTERVAL = "metriccatcher.interval";
    private static final String METRICCATCHER_UDP_PORT = "metriccatcher.udp.port";
    private static final String METRICCATCHER_MAX_METRICS = "metriccatcher.maxMetrics";
    private static final String METRICCATCHER_GANGLIA_HOST = "metriccatcher.ganglia.host";
    private static final String METRICCATCHER_GANGLIA_PORT = "metriccatcher.ganglia.port";
    private static final String METRICCATCHER_GRAPHITE_HOST = "metriccatcher.graphite.host";
    private static final String METRICCATCHER_GRAPHITE_PORT = "metriccatcher.graphite.port";
    private static final String METRICCATCHER_GRAPHITE_PREFIX = "metriccatcher.graphite.prefix";

    /**
     * Load properties, build a MetricCatcher, start catching
     *
     * @param propertiesFile The config file
     * @throws IOException if the properties file cannot be read
     */
    public Loader(File propertiesFile) throws IOException {
        logger.info("Starting metriccatcher");

        logger.info("Loading configuration from: " + propertiesFile.getAbsolutePath());
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(propertiesFile));
            for (Object key : properties.keySet()) {  // copy properties into system properties
                System.setProperty((String) key, (String)properties.get(key));
            }
        } catch (IOException e) {
            logger.error("error reading properties file: " + e);
            System.exit(1);
        }

        int reportingInterval = 60;
        String intervalProperty = properties.getProperty(METRICCATCHER_INTERVAL);
        if (intervalProperty != null) {
            try {
                reportingInterval = Integer.parseInt(intervalProperty);
            } catch (NumberFormatException e) {
                logger.warn("Couldn't parse " + METRICCATCHER_INTERVAL + " setting", e);
            }
        }

        // Start a Ganglia reporter if specified in the config
        String gangliaHost = properties.getProperty(METRICCATCHER_GANGLIA_HOST);
        String gangliaPort = properties.getProperty(METRICCATCHER_GANGLIA_PORT);
        if (gangliaHost != null && gangliaPort != null) {
            logger.info("Creating Ganglia reporter pointed at " + gangliaHost + ":" + gangliaPort);
            GangliaReporter gangliaReporter = new GangliaReporter(gangliaHost, Integer.parseInt(gangliaPort));
            gangliaReporter.start(reportingInterval, TimeUnit.SECONDS);
        }

        // Start a Graphite reporter if specified in the config
        String graphiteHost = properties.getProperty(METRICCATCHER_GRAPHITE_HOST);
        String graphitePort = properties.getProperty(METRICCATCHER_GRAPHITE_PORT);
        if (graphiteHost != null && graphitePort != null) {
            String graphitePrefix = properties.getProperty(METRICCATCHER_GRAPHITE_PREFIX);
            if (graphitePrefix == null) {
                graphitePrefix = InetAddress.getLocalHost().getHostName();
            }
            logger.info("Creating Graphite reporter pointed at " + graphiteHost + ":" + graphitePort + " with prefix '" + graphitePrefix + "'");
            GraphiteReporter graphiteReporter = new GraphiteReporter(graphiteHost, Integer.parseInt(graphitePort), StringUtils.trimToNull(graphitePrefix));
            graphiteReporter.start(reportingInterval, TimeUnit.SECONDS);
        }

        int maxMetrics = Integer.parseInt(properties.getProperty(METRICCATCHER_MAX_METRICS, "500"));
        logger.info("Max metrics: " + maxMetrics);
        Map<String, Metric> lruMap = new LRUMap<String, Metric>(10, maxMetrics);

        int port = Integer.parseInt(properties.getProperty(METRICCATCHER_UDP_PORT, "1420"));
        logger.info("Listening on UDP port " + port);
        DatagramSocket socket = new DatagramSocket(port);

        metricCatcher = new MetricCatcher(socket, lruMap);
        metricCatcher.start();

        // Register a shutdown hook and wait for termination
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            };
        });
    }

    /**
     * Shut down the metric catcher
     */
    public void shutdown() {
        logger.info("shutting down...");

        // Pass shutdown to the metricCatcher
        if (metricCatcher != null)
            metricCatcher.shutdown();

        try {
            // Wait for it shutdown
            metricCatcher.join(1000);
        } catch (InterruptedException e) {
            logger.info("interrupted waiting for thread to shutdown, exiting...");
        }
    }

    /**
     * Create a loader with the given properties file, if specified
     * on the command line.
     *
     * @param args Optional '-c' to define a configuration file
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
        String propertiesFilename = defaultPropertiesFilename;

        // Command line arguments
        for (int i = 0; i < args.length; i++) {
            // Specify config file
            if ("-c".equals(args[i])) {
                propertiesFilename = args[++i];
            }
        }

        File propertiesFile = new File(propertiesFilename);
        @SuppressWarnings("unused")
        Loader loader = new Loader(propertiesFile);
    }
}
