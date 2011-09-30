package com.clearspring.thetan.metricCatcher;


public class JSONMetric {
    private MetricType type;
    private String name;
    private double value;
    private double timestamp;
    // For histograms
    private boolean biased;
    
    public MetricType getType() {
        return type;
    }
    public Class<?> getMetricClass() {
        return this.type.getKlass();
    }
    public void setType(String typeName) {
        this.type = MetricType.fromName(typeName);
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    
    public double getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isBiased() {
        return biased;
    }
    public void setBiased(boolean biased) {
        this.biased = biased;
    }
}
