package com.clearspring.thetan.metricCatcher;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yammer.metrics.core.GaugeMetric;

public class JSONMetricTest {
    JSONMetric  jsonMetric;
    
    @Before
    public void setUp() throws Exception {
        jsonMetric = new JSONMetric();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetType_MetricType() {
        jsonMetric.setType("gauge");
        assertEquals(MetricType.GAUGE, jsonMetric.getType());
    }
    
    @Test
    public void testSetType_TypeName() {
        jsonMetric.setType("gauge");
        assertEquals(MetricType.GAUGE, jsonMetric.getType());
    }
    
    @Test
    public void testGetKlass_TypeName() {
        jsonMetric.setType("gauge");
        assertEquals(GaugeMetric.class, jsonMetric.getMetricClass());
    }
}
