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
