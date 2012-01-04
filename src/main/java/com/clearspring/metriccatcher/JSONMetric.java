/*
 * Copyright (C) 2012 Clearspring Technologies, Inc.⋅
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clearspring.metriccatcher;

public class JSONMetric {
    private MetricType type;
    private String name;
    private double value;
    private double timestamp;

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
}
