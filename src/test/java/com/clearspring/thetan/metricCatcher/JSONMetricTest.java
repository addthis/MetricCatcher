package com.clearspring.thetan.metricCatcher;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.yammer.metrics.core.CounterMetric;
import com.yammer.metrics.core.GaugeMetric;
import com.yammer.metrics.core.HistogramMetric;
import com.yammer.metrics.core.MeterMetric;
import com.yammer.metrics.core.TimerMetric;

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
    public void testGetMetricClass_Gauge() {
        jsonMetric.setType("gauge");
        assertEquals(GaugeMetric.class, jsonMetric.getMetricClass());
    }
    
    @Test
    public void testGetMetricClass_Counter() {
        jsonMetric.setType("counter");
        assertEquals(CounterMetric.class, jsonMetric.getMetricClass());
    }
    
    @Test
    public void testGetMetricClass_Meter() {
        jsonMetric.setType("meter");
        assertEquals(MeterMetric.class, jsonMetric.getMetricClass());
    }
    
    @Test
    public void testGetMetricClass_BiasedHistogram() {
        jsonMetric.setType("biased");
        assertEquals(HistogramMetric.class, jsonMetric.getMetricClass());
    }
    
    @Test
    public void testGetMetricClass_UniformHistogram() {
        jsonMetric.setType("uniform");
        assertEquals(HistogramMetric.class, jsonMetric.getMetricClass());
    }
    
    @Test
    public void testGetMetricClass_Timer() {
        jsonMetric.setType("timer");
        assertEquals(TimerMetric.class, jsonMetric.getMetricClass());
    }
}
