package com.clearspring.thetan.metricCatcher;

import java.util.List;

public class MetricsMessage {
    List<JSONMetric> metrics;
    String unique;
    
    public List<JSONMetric> getMetrics() {
        return metrics;
    }
    public void setMetrics(List<JSONMetric> metrics) {
        this.metrics = metrics;
    }
    
    public String getUnique() {
        return unique;
    }
    public void setUnique(String unique) {
        this.unique = unique;
    }
}
