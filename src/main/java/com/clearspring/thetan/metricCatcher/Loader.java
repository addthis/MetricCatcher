package com.clearspring.thetan.metricCatcher;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jackson.map.util.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.core.Metric;
import com.yammer.metrics.reporting.GangliaReporter;

public class Loader {	
	private static final Logger logger = LoggerFactory.getLogger(Loader.class); 
	private static final String defaultPropertiesFilename = "conf/config.properties";
	
	private MetricCatcher metricCatcher;
	
	/**
	 * Load properties, build a MetricCatcher, start catching
	 * 
	 * @param propertiesFile The config file
	 * @throws IOException if the properties file cannot be read
	 */
	public Loader(File propertiesFile) throws IOException {
		logger.info("Starting metricCatcher");
		
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
		
		String gangliaHost = properties.getProperty("metricCatcher.ganglia.host");
		int gangliaPort = Integer.parseInt(properties.getProperty("metricCatcher.ganglia.port"));
		GangliaReporter reporter = new GangliaReporter(gangliaHost, gangliaPort);
		
		int maxMetrics = Integer.parseInt(properties.getProperty("metricCatcher.maxMetrics"));
		Map<String, Metric> lruMap = new LRUMap<String, Metric>(10, maxMetrics);
		
		int port = Integer.parseInt(properties.getProperty("metricCatcher.udp.port"));
		DatagramSocket socket = new DatagramSocket(port);
		
		metricCatcher = new MetricCatcher(socket, reporter, lruMap);
		metricCatcher.start();

		// Register a shutdown hook and wait for termination
		Runtime.getRuntime().addShutdownHook(new Thread() {
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
		String propertiesFilename = defaultPropertiesFilename;

		// Command line arguments
		for (int i = 0; i < args.length; i++) {
		    // Specify config file
			if ("-c".equals(args[i])) {
				propertiesFilename = args[++i];
			}
		}
		
		File propertiesFile = new File(propertiesFilename);
		Loader loader = new Loader(propertiesFile);
	}
}