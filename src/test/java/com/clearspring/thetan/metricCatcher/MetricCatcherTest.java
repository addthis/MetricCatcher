package com.clearspring.thetan.metricCatcher;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.codehaus.jackson.map.util.LRUMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yammer.metrics.core.MeterMetric;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.reporting.MockReporter;

public class MetricCatcherTest {
    MetricCatcher metricCatcher;
    JSONMetric jsonMetric;
        
    @Before
    public void setUp() throws Exception {
        MockReporter reporter = new MockReporter(new MetricsRegistry(), "mock-reporter");
		Map<String, Metric> metricCache = new LRUMap<String, Metric>(10, 10);
        metricCatcher = new MetricCatcher(65500, reporter, metricCache);
        
        jsonMetric = new JSONMetric();
        jsonMetric.setType(MetricType.METER);
        jsonMetric.setName("foo.bar.baz.metric");
        jsonMetric.setValue(1);
        jsonMetric.setTimestamp(((int)System.currentTimeMillis() / 1000));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateMetric() {
        Metric metric = metricCatcher.createMetric(jsonMetric);

        assertEquals(MeterMetric.class, metric.getClass());
        
        MeterMetric meterMetric = ((MeterMetric)metric);
        // All metrics are in minutes :-( plz2fix
        assertEquals(TimeUnit.MINUTES, meterMetric.rateUnit());
    }

    @Test
    public void testUpdateMetric() {
    }

    @Test
    public void testRun() {
    }
}
