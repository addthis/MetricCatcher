package com.clearspring.thetan.metricCatcher;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricCatcher extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(MetricCatcher.class);

    AtomicBoolean shutdown = new AtomicBoolean();
	
    public MetricCatcher() throws IOException {
    }
    
	@Override
	public void run() {
		while (shutdown.get() == false) {
		}
	}

	public void shutdown() {
		shutdown.set(true);
		this.interrupt();
	}
}