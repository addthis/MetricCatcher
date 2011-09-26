package com.clearspring.thetan.metricCatcher;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loader {	
	private static final Logger logger = LoggerFactory.getLogger(Loader.class); 
	private static final String defaultPropertiesFilename = "conf/config.properties";
	
	private MetricCatcher metricCatcher;
	
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
		
		metricCatcher = new MetricCatcher();
		metricCatcher.start();

		// Register a shutdown hook and wait for termination
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			};
		});
	}
	
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