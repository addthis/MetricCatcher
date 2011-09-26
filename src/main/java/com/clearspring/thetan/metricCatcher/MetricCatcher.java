package com.clearspring.thetan.metricCatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricCatcher extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(MetricCatcher.class);
    AtomicBoolean shutdown = new AtomicBoolean();
    
    private DatagramSocket socket;
    private Map metricCache;
	
    public MetricCatcher(int port, Map metricCache) throws IOException {
        socket = new DatagramSocket(port);
        metricCache = metricCache;
    }
    
	@Override
	public void run() {
	    byte[] data = new byte[1024];
	    
		while (shutdown.get() == false) {
		    DatagramPacket received = new DatagramPacket(data, data.length);
		    try {
                socket.receive(received);
                
                if (logger.isDebugEnabled()) {
	                InetAddress senderAddress = received.getAddress();
	                int senderPort = received.getPort();
	                logger.debug("Got packet from " + senderAddress + ":" + senderPort);
                }
                
                String json = new String(received.getData());
                logger.trace("JSON: " + json);
                
                // TODO record the metric
            } catch (IOException e) {
                logger.error("IO error: " + e);
            }
		}
	}

	public void shutdown() {
		shutdown.set(true);
		this.interrupt();
	}
}