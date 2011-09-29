package com.clearspring.thetan.metricCatcher;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yammer.metrics.core.HistogramMetric;

public class MetricTypeTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFromName() {
        MetricType res = MetricType.fromName("meter");
        assertEquals(MetricType.METER, res);
    }

    @Test
    public void testFromName_Invalid() {
        MetricType res = MetricType.fromName("foo");
        assertEquals(null, res);
    }
    
    @Test
    public void testKlass() {
        assertEquals(HistogramMetric.class, MetricType.HISTOGRAM_BIASED.getKlass());
    }
}
