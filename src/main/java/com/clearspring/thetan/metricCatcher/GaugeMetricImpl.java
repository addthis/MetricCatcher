package com.clearspring.thetan.metricCatcher;

import com.yammer.metrics.core.GaugeMetric;

public class GaugeMetricImpl implements GaugeMetric<Long> {
    long value;
    
    public GaugeMetricImpl() {
        value = 0;
    }
    
    @Override
    public Long value() {
        return value;
    }
    
    public void setValue(long value) {
        this.value = value;
    }
}
