package com.clearspring.thetan.metricCatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.util.LRUMap;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.CounterMetric;
import com.yammer.metrics.core.GaugeMetric;
import com.yammer.metrics.core.HistogramMetric;
import com.yammer.metrics.core.MeterMetric;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.TimerMetric;
import com.yammer.metrics.reporting.AbstractReporter;

public class MetricCatcher extends Thread {
	private static final Logger logger = LoggerFactory.getLogger(MetricCatcher.class);
    AtomicBoolean shutdown = new AtomicBoolean();
    
    private ObjectMapper mapper = new ObjectMapper();
    private DatagramSocket socket;
    private AbstractReporter reporter;
    private Map<String, Metric> metricCache;
	
    public MetricCatcher(DatagramSocket socket, AbstractReporter reporter, Map<String, Metric> metricCache) throws IOException {
        this.socket = socket;
        this.metricCache = metricCache;
        
        // For sending metrics on to the metric collector
        this.reporter = reporter;
        this.reporter.start(60, TimeUnit.SECONDS);
    }
    
	@Override
	public void run() {
	    // Arbi-fucking-trary. One metric with a reasonable name is less than 200b
	    // This (http://stackoverflow.com/q/3712151/17339) implies that 64 bit
	    // leenuks will handle packets up to 24,258b, so let's assume we won't
	    // get anything larger than that.  Note that this is a hard limit-you
	    // can't accumulate from the socket so anything larger is truncated.
	    byte[] data = new byte[24258];
	    
	    // Keep track of the last 1000 packets we've seen
	    Map<String, Boolean> recentMessages = new LRUMap<String, Boolean>(10, 1000);
	    
		while (shutdown.get() == false) {
		    DatagramPacket received = new DatagramPacket(data, data.length);
		    try {
		        // Pull in network data
                socket.receive(received);
                byte[] json = received.getData();
                if (logger.isDebugEnabled()) {
	                InetAddress senderAddress = received.getAddress();
	                int senderPort = received.getPort();
	                logger.debug("Got packet from " + senderAddress + ":" + senderPort);
                }
                if (logger.isTraceEnabled()) {
                    String jsonString = new String(json);
	                logger.trace("JSON: " + jsonString);
                }
                
                MetricsMessage jsonMessage = mapper.readValue(json, MetricsMessage.class);
                // Skip if this packet has been seen already
                if (recentMessages.containsKey(jsonMessage.getUnique())) {
                    logger.info("Not processing duplicate message <" + jsonMessage.getUnique() + ">");
                    continue;
                }
                recentMessages.put(jsonMessage.getUnique(), Boolean.TRUE);
                
                // Parse all of the metrics in the message
                for (JSONMetric jsonMetric : jsonMessage.getMetrics()) {
                    if (!metricCache.containsKey(jsonMetric.getName())) {
                        logger.info("Creating new metric for '" + jsonMetric.getName() + "'");
                        Metric newMetric = createMetric(jsonMetric);
                        metricCache.put(jsonMetric.getName(), newMetric);
                    }
                    
                    // Record the update
                    logger.debug("Updating '" + jsonMetric.getName() + "' with <" + jsonMetric.getValue() + ">");
                    updateMetric(metricCache.get(jsonMetric.getName()), jsonMetric.getValue());
                }
            } catch (IOException e) {
                logger.error("IO error: " + e);
            }
		}
		
		socket.close();
	}

    protected Metric createMetric(JSONMetric jsonMetric) {
	    // Split the name from the JSON on dots for the metric group/type/name
	    MetricName metricName;
	    ArrayList<String> parts = new ArrayList<String>(Arrays.asList(jsonMetric.getName().split("\\.")));
	    if (parts.size() >= 3)
	        metricName = new MetricName(parts.remove(1), parts.remove(1), StringUtils.join(parts));
        else
	        metricName = new MetricName(jsonMetric.getName(), "", "");
	    
	    Class<?> metricType = jsonMetric.getMetricClass();
	    if (metricType == GaugeMetric.class) {
	        // TODO do gauges even make sense?
		} else if (metricType == CounterMetric.class) {
		    return Metrics.newCounter(metricName);
		} else if (metricType == MeterMetric.class) {
		    // TODO timeunit
		    return Metrics.newMeter(metricName, jsonMetric.getName(), TimeUnit.MINUTES);
        } else if (metricType == HistogramMetric.class) {
            return Metrics.newHistogram(metricName, jsonMetric.isBiased());
        } else if (metricType == TimerMetric.class) {
            return Metrics.newTimer(metricName, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
        }
	    
	    // Uh-oh
	    return null;
    }

    /**
	 * Update various metric types.
	 *
	 * Gauge:
	 *
	 * Counter:
	 *     Increment or decrement based upon sign of value
	 *     Clear counter if given 0
	 *
	 * Meter:
	 *     mark() the meter with the given value
	 *
	 * Histogram:
	 *     update() the histogram with the given value
	 *
	 * Timer:
	 *
	 * @param metric
	 * @param value
	 */
	protected void updateMetric(Metric metric, long value) {
	    if (metric.getClass() == GaugeMetric.class) {
	        // TODO do gauges even make sense?
		} else if (metric.getClass() == CounterMetric.class) {
		    if (value > 0)
		        ((CounterMetric)metric).inc(value);
		    else if (value < 0)
		        ((CounterMetric)metric).dec(value * -1);
		    else
		        ((CounterMetric)metric).clear();
		} else if (metric.getClass() == MeterMetric.class) {
	        ((MeterMetric)metric).mark(value);
        } else if (metric.getClass() == HistogramMetric.class) {
            // TODO clearing?  How about no, so that we can record 0 values; it'll clear over time...
	        ((HistogramMetric)metric).update(value);
        } else if (metric.getClass() == TimerMetric.class) {
            // TODO Start or stop based upon sign?
            // update(duration)
            // update(duration, TimeUnit)
            // stop()
            // clear()
        }
    }

    public void shutdown() {
		shutdown.set(true);
		this.interrupt();
	}
}