package com.clearspring.thetan.metricCatcher;

import com.yammer.metrics.core.CounterMetric;
import com.yammer.metrics.core.GaugeMetric;
import com.yammer.metrics.core.HistogramMetric;
import com.yammer.metrics.core.MeterMetric;
import com.yammer.metrics.core.TimerMetric;

public enum MetricType {
    GAUGE ("gauge", GaugeMetric.class),
    COUNTER ("counter", CounterMetric.class),
    METER ("meter", MeterMetric.class),
    HISTOGRAM_BIASED ("biased", HistogramMetric.class),
    HISTOGRAM_UNIFORM ("uniform", HistogramMetric.class),
    TIMER ("timer", TimerMetric.class);
    
    private String name;
    private Class<?> klass;
    
    MetricType(String name, Class<?> className) {
        this.name = name;
        this.klass = className;
    }
    
    public String getName() {
        return name;
    }
    
    public Class<?> getKlass() {
        return klass;
    }
    
    public static MetricType fromName(String name) {
        if (name != null) {
            for (MetricType t : MetricType.values()) {
                if (t.name.equalsIgnoreCase(name))
                    return t;
            }
        }
        
        return null;
    }
}